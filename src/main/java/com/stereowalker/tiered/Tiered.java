package com.stereowalker.tiered;

import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.stereowalker.tiered.api.CustomEntityAttributes;
import com.stereowalker.tiered.api.ForgeArmorTags;
import com.stereowalker.tiered.api.ForgeToolTags;
import com.stereowalker.tiered.api.PotentialAttribute;
import com.stereowalker.tiered.data.AttributeDataLoader;
import com.stereowalker.tiered.mixin.ReloadableServerResourcesMixin;
import com.stereowalker.tiered.network.protocol.game.ClientboundAttributeSyncerPacket;
import com.stereowalker.unionlib.core.registries.RegistryHolder;
import com.stereowalker.unionlib.core.registries.RegistryObject;
import com.stereowalker.unionlib.mod.IPacketHolder;
import com.stereowalker.unionlib.mod.MinecraftMod;
import com.stereowalker.unionlib.network.PacketRegistry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.simple.SimpleChannel;

@Mod("tiered")
public class Tiered extends MinecraftMod implements IPacketHolder {

    /**
     * Attribute Data Loader instance which handles loading attribute .json files from "data/modid/item_attributes".
     * <p> This field is registered to the server's data manager in {@link ReloadableServerResourcesMixin}
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

    public static final String NBT_SUBTAG_KEY = "Tiered";
    public static final String NBT_SUBTAG_DATA_KEY = "Tier";

    public static Tiered instance;
    public Tiered() 
	{
    	super("tiered", new ResourceLocation("tiered", "textures/icon.png"), LoadType.BOTH);
    	instance = this;
		final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		modEventBus.addListener(this::setup);
		modEventBus.addListener(this::clientSetup);
		new ResourceLocation("tiered", "attribute_sync");
	}
    
	private void setup(final FMLCommonSetupEvent event)
	{
		ForgeArmorTags.init();
		ForgeToolTags.init();
        CustomEntityAttributes.init();
	}
	
	@Override
	public IRegistries getRegistries() {
		return (reg) -> {
			reg.add(ItemRegistries.class);
			MinecraftForge.EVENT_BUS.addListener(ItemRegistries::reforge);
			MinecraftForge.EVENT_BUS.addListener(ItemRegistries::trade);
		};
	}
	@RegistryHolder(registry = Item.class, namespace = "tiered")
	public class ItemRegistries {
		@RegistryObject("armorers_hammer")
		public static final Item ARMORERS_HAMMER = new Item(new Item.Properties().tab(CreativeModeTab.TAB_TOOLS).defaultDurability(10));
		@RegistryObject("toolsmiths_hammer")
		public static final Item TOOLSMITHS_HAMMER = new Item(new Item.Properties().tab(CreativeModeTab.TAB_TOOLS).defaultDurability(10));
		@RegistryObject("weaponsmiths_hammer")
		public static final Item WEAPONSMITHS_HAMMER = new Item(new Item.Properties().tab(CreativeModeTab.TAB_TOOLS).defaultDurability(10));
		public static void trade(VillagerTradesEvent event) {
			if (event.getType() == VillagerProfession.ARMORER) {
				event.getTrades().get(3).add(new VillagerTrades.ItemsForEmeralds(ARMORERS_HAMMER, 64, 1, 1, 10));
			}
			if (event.getType() == VillagerProfession.TOOLSMITH) {
				event.getTrades().get(3).add(new VillagerTrades.ItemsForEmeralds(TOOLSMITHS_HAMMER, 64, 1, 1, 10));
			}
			if (event.getType() == VillagerProfession.WEAPONSMITH) {
				event.getTrades().get(4).add(new VillagerTrades.ItemsForEmeralds(WEAPONSMITHS_HAMMER, 64, 1, 1, 10));
			}
		}
		public static void reforge(AnvilUpdateEvent event) {
			if (!event.getLeft().isDamaged() && event.getLeft().getTagElement(NBT_SUBTAG_KEY) != null) {
				PotentialAttribute reforgedAttribute = ATTRIBUTE_DATA_LOADER.getItemAttributes().get(new ResourceLocation(event.getLeft().getTagElement(Tiered.NBT_SUBTAG_KEY).getString("Tier")));
				if (event.getRight().getItem().getRegistryName().equals(new ResourceLocation(reforgedAttribute.getReforgeItem())) && (event.getRight().getMaxDamage() - event.getRight().getDamageValue()) >= reforgedAttribute.getReforgeDurabilityCost()) {
					ItemStack copy = event.getLeft().copy();
					copy.removeTagKey(NBT_SUBTAG_KEY);
					event.setOutput(copy);
					event.setCost(reforgedAttribute.getReforgeExperienceCost());
				}
			}
		}
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

	@Override
	public void registerClientboundPackets(SimpleChannel arg0) {
		PacketRegistry.registerMessage(channel, 0, ClientboundAttributeSyncerPacket.class, (packetBuffer) -> new ClientboundAttributeSyncerPacket(packetBuffer));
	}

	@Override
	public void registerServerboundPackets(SimpleChannel arg0) {
		
	}
}
