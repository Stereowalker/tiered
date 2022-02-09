package com.stereowalker.tiered;

import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.stereowalker.tiered.api.CustomEntityAttributes;
import com.stereowalker.tiered.api.ForgeArmorTags;
import com.stereowalker.tiered.api.ForgeToolTags;
import com.stereowalker.tiered.data.AttributeDataLoader;
import com.stereowalker.tiered.mixin.ServerResourceManagerMixin;
import com.stereowalker.tiered.network.AttributeSyncer;
import com.stereowalker.unionlib.mod.MinecraftMod;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

@Mod("tiered")
public class Tiered extends MinecraftMod {

    /**
     * Attribute Data Loader instance which handles loading attribute .json files from "data/modid/item_attributes".
     * <p> This field is registered to the server's data manager in {@link ServerResourceManagerMixin}
     */
    public static final AttributeDataLoader ATTRIBUTE_DATA_LOADER = new AttributeDataLoader();

    public static final UUID[] MODIFIERS = new UUID[] {
            UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"),
            UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"),
            UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"),
            UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150"),
            UUID.fromString("4a88bc27-9563-4eeb-96d5-fe50917cc24f"),
            UUID.fromString("fee48d8c-1b51-4c46-9f4b-c58162623a7a")
    };

    public static final Logger LOGGER = LogManager.getLogger();

    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel HANDLER = NetworkRegistry.newSimpleChannel(
            new ResourceLocation("tiered", "attribute_sync"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );
    public static final String NBT_SUBTAG_KEY = "Tiered";
    public static final String NBT_SUBTAG_DATA_KEY = "Tier";

    public Tiered() 
	{
    	super("tiered", new ResourceLocation("tiered", "textures/icon.png"), LoadType.BOTH);
		final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		modEventBus.addListener(this::setup);
		modEventBus.addListener(this::clientSetup);
		//MinecraftForge.EVENT_BUS.register(this);
	}

	private void setup(final FMLCommonSetupEvent event)
	{
		ForgeArmorTags.init();
		ForgeToolTags.init();
        CustomEntityAttributes.init();
        registerAttributeSyncer();
	}

	private void clientSetup(final FMLClientSetupEvent event) {
	}

    /**
     * Returns an {@link ResourceLocation} namespaced with this mod's modid ("tiered").
     *
     * @param path  path of identifier (eg. apple in "minecraft:apple")
     * @return  ResourceLocation created with a namespace of this mod's modid ("tiered") and provided path
     */
    public static ResourceLocation id(String path) {
        return new ResourceLocation("tiered", path);
    }

    public static boolean isPreferredEquipmentSlot(ItemStack stack, EquipmentSlot slot) {
        if(stack.getItem() instanceof ArmorItem) {
            ArmorItem item = (ArmorItem) stack.getItem();
            return item.getSlot().equals(slot);
        }

        return slot == EquipmentSlot.MAINHAND;
    }
    public static void registerAttributeSyncer() {
        HANDLER.registerMessage(0, AttributeSyncer.class, AttributeSyncer::encode, AttributeSyncer::decode, AttributeSyncer::handlePacket);
    }
}
