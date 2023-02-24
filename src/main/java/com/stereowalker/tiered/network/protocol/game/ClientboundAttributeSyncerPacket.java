package com.stereowalker.tiered.network.protocol.game;

import static com.stereowalker.tiered.Tiered.ATTRIBUTE_DATA_LOADER;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.Maps;
import com.stereowalker.tiered.Tiered;
import com.stereowalker.tiered.api.PotentialAttribute;
import com.stereowalker.tiered.data.AttributeDataLoader;
import com.stereowalker.unionlib.network.protocol.game.ClientboundUnionPacket;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public class ClientboundAttributeSyncerPacket extends ClientboundUnionPacket {
    public int size;
    public Map<ResourceLocation, PotentialAttribute> attribute;
    public static final Map<ResourceLocation, PotentialAttribute> CACHED_ATTRIBUTES = new HashMap<>();

    public ClientboundAttributeSyncerPacket(Map<ResourceLocation, PotentialAttribute> attribute) {
    	super(Tiered.instance.channel);
    	this.attribute = attribute;
        this.size = attribute.size();
    }

	public ClientboundAttributeSyncerPacket(FriendlyByteBuf buf) {
		super(buf, Tiered.instance.channel);
		this.size = buf.readInt();
		this.attribute = Maps.newHashMap();
        for (int i = 0; i < this.size; i++) {
            ResourceLocation id = buf.readResourceLocation();
            PotentialAttribute pa = AttributeDataLoader.GSON.fromJson(buf.readUtf(), PotentialAttribute.class);
            this.attribute.put(id, pa);
        }
	}

	@Override
	public void encode(final FriendlyByteBuf buf) {
        buf.writeInt(this.size);

        this.attribute.forEach((id, attribute) -> {
            buf.writeResourceLocation(id);
            buf.writeUtf(AttributeDataLoader.GSON.toJson(attribute));
        });
	}

	@Override
	public boolean handleOnClient(LocalPlayer player) {
		CACHED_ATTRIBUTES.putAll(ATTRIBUTE_DATA_LOADER.getItemAttributes());
        ATTRIBUTE_DATA_LOADER.clear();

        ATTRIBUTE_DATA_LOADER.replace(this.attribute);
        if (ATTRIBUTE_DATA_LOADER.getItemAttributes().size() == 0) {
            ATTRIBUTE_DATA_LOADER.replace(CACHED_ATTRIBUTES);
        }
		return true;
	}
}
