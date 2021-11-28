package draylar.tiered.network;

import draylar.tiered.api.PotentialAttribute;
import draylar.tiered.data.AttributeDataLoader;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static draylar.tiered.Tiered.ATTRIBUTE_DATA_LOADER;

public class AttributeSyncer {
    public int size;
    public Map<ResourceLocation, PotentialAttribute> attribute;
    public static final Map<ResourceLocation, PotentialAttribute> CACHED_ATTRIBUTES = new HashMap<>();

    public AttributeSyncer()
    {
        this.size = ATTRIBUTE_DATA_LOADER.getItemAttributes().size();
        this.attribute = ATTRIBUTE_DATA_LOADER.getItemAttributes();
    }

    public static AttributeSyncer decode( FriendlyByteBuf buf )
    {
        AttributeSyncer packet = new AttributeSyncer();

        packet.size = buf.readInt();
        for (int i = 0; i < packet.size; i++) {
            ResourceLocation id = new ResourceLocation(buf.readUtf());
            PotentialAttribute pa = AttributeDataLoader.GSON.fromJson(buf.readUtf(), PotentialAttribute.class);
            packet.attribute.put(id, pa);
        }
        return packet;
    }

    public static void encode( AttributeSyncer packet, FriendlyByteBuf buf )
    {
        // serialize each attribute file as a string to the packet
        buf.writeInt(packet.size);

        // write each value
        packet.attribute.forEach((id, attribute) -> {
            buf.writeUtf(id.toString());
            buf.writeUtf(AttributeDataLoader.GSON.toJson(attribute));
        });
    }

    public static void handlePacket( AttributeSyncer packet, Supplier<NetworkEvent.Context> ctx )
    {
        if(ctx.get().getDirection().getReceptionSide().isClient()) {
            ctx.get().enqueueWork(() -> {
                CACHED_ATTRIBUTES.putAll(ATTRIBUTE_DATA_LOADER.getItemAttributes());
                ATTRIBUTE_DATA_LOADER.clear();

                ATTRIBUTE_DATA_LOADER.replace(packet.attribute);
                if (ATTRIBUTE_DATA_LOADER.getItemAttributes().size() == 0) {
                    ATTRIBUTE_DATA_LOADER.replace(CACHED_ATTRIBUTES);
                }
            });
            ctx.get().setPacketHandled(true);
        }
    }
}
