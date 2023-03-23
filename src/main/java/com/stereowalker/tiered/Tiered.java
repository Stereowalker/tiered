package com.stereowalker.tiered;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Function;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.stereowalker.tiered.api.AttributeTemplate;
import com.stereowalker.tiered.api.ForgeArmorTags;
import com.stereowalker.tiered.api.ForgeToolTags;
import com.stereowalker.tiered.api.PotentialAttribute;
import com.stereowalker.tiered.data.AttributeDataLoader;
import com.stereowalker.tiered.network.protocol.game.ClientboundAttributeSyncerPacket;
import com.stereowalker.unionlib.UnionLib;
import com.stereowalker.unionlib.core.registries.RegistryHolder;
import com.stereowalker.unionlib.core.registries.RegistryObject;
import com.stereowalker.unionlib.mod.IPacketHolder;
import com.stereowalker.unionlib.mod.MinecraftMod;
import com.stereowalker.unionlib.network.PacketRegistry;
import com.stereowalker.unionlib.world.entity.AccessorySlot;
import com.stereowalker.unionlib.world.item.AccessoryItem;

import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
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

	public static final AttributeDataLoader ATTRIBUTE_DATA_LOADER = new AttributeDataLoader();

	public static final UUID[] MODIFIERS = new UUID[] {
			//Equipment
			UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"),
			UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"),
			UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"),
			UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150"),
			UUID.fromString("4a88bc27-9563-4eeb-96d5-fe50917cc24f"),
			UUID.fromString("fee48d8c-1b51-4c46-9f4b-c58162623a7a"),
			//Accessory slots 
			UUID.fromString("3ac44786-fd3d-43db-8283-6822f7d62ea4"),
			UUID.fromString("2671f9f5-4ca6-4d09-b4bb-b958ac6d31e7"),
			UUID.fromString("31a9945e-2c8d-4894-86b6-87ba416c2e18"),
			UUID.fromString("63360860-88b6-4395-a561-151cd51dc91b"),
			UUID.fromString("7ec914a0-7b1d-4bec-ba17-d435ffa49eb4"),
			UUID.fromString("2dcd3ee4-cadb-4fa4-9bd4-b90b67ab77ff"),
			UUID.fromString("031de3a3-4368-4021-a6b1-42e8c454cfc1"),
			UUID.fromString("62c90c65-0f18-4d8d-afb2-340e5ff17fc5"),
			UUID.fromString("b8c433d5-1ae0-4ab1-9a40-000a6aab3f29"),
			//Accessory groups
			UUID.fromString("b340cc35-ef8e-4fa6-b21f-9a60e5d4e4b3"),
			UUID.fromString("24cf925c-bfac-4729-9bad-57e1dc4502f7"),
			UUID.fromString("1732e8f1-8c5e-4f1f-aa34-b2489b4259c9")
	};

	public static final Map<String, UUID> CURIO_MODIFIERS = Util.make(Maps.newHashMap(), (map) -> {
		map.put("ring", UUID.fromString("fee48d8c-1b51-4c46-9f4b-c58162623a7b"));
	});

	public static final Logger LOGGER = LogManager.getLogger();

	public static final String NBT_SUBTAG_KEY = "Tiered";
	public static final String NBT_SUBTAG_DATA_KEY = "Tier";

	public static Tiered instance;
	public Tiered() 
	{
		super("tiered", new ResourceLocation("tiered", "textures/icon.png"), LoadType.BOTH);
		UnionLib.Modules.applyDefaultDrawSpeedToBows();
		instance = this;
		final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		modEventBus.addListener(this::setup);
		modEventBus.addListener(this::clientSetup);
		MinecraftForge.EVENT_BUS.addListener((Consumer<AddReloadListenerEvent>)event -> event.addListener(ATTRIBUTE_DATA_LOADER));
		new ResourceLocation("tiered", "attribute_sync");
	}

	private void setup(final FMLCommonSetupEvent event)
	{
		ForgeArmorTags.init();
		ForgeToolTags.init();
	}

	@Override
	public IRegistries getRegistries() {
		return (reg) -> {
			reg.add(ItemRegistries.class);
			MinecraftForge.EVENT_BUS.addListener(ItemRegistries::reforge);
			MinecraftForge.EVENT_BUS.addListener(ItemRegistries::trade);
		};
	}
	
	@Override
	public void populateCreativeTabs(CreativeTabPopulator populator) {
		if (populator.getTab() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
			populator.getOutput().accept(ItemRegistries.ARMORERS_HAMMER);
			populator.getOutput().accept(ItemRegistries.TOOLSMITHS_HAMMER);
			populator.getOutput().accept(ItemRegistries.WEAPONSMITHS_HAMMER);
		}
	}
	
	@RegistryHolder(registry = Item.class, namespace = "tiered")
	public class ItemRegistries {
		@RegistryObject("armorers_hammer")
		public static final Item ARMORERS_HAMMER = new Item(new Item.Properties().defaultDurability(10));
		@RegistryObject("toolsmiths_hammer")
		public static final Item TOOLSMITHS_HAMMER = new Item(new Item.Properties().defaultDurability(10));
		@RegistryObject("weaponsmiths_hammer")
		public static final Item WEAPONSMITHS_HAMMER = new Item(new Item.Properties().defaultDurability(10));
		public static void trade(VillagerTradesEvent event) {
			if (event.getType() == VillagerProfession.ARMORER)
				event.getTrades().get(3).add(new VillagerTrades.ItemsForEmeralds(ARMORERS_HAMMER, 64, 1, 1, 10));
			if (event.getType() == VillagerProfession.TOOLSMITH)
				event.getTrades().get(3).add(new VillagerTrades.ItemsForEmeralds(TOOLSMITHS_HAMMER, 64, 1, 1, 10));
			if (event.getType() == VillagerProfession.WEAPONSMITH)
				event.getTrades().get(4).add(new VillagerTrades.ItemsForEmeralds(WEAPONSMITHS_HAMMER, 64, 1, 1, 10));
		}
		public static void reforge(AnvilUpdateEvent event) {
			if (!event.getLeft().isDamaged() && event.getLeft().getTagElement(NBT_SUBTAG_KEY) != null) {
				PotentialAttribute reforgedAttribute = ATTRIBUTE_DATA_LOADER.getItemAttributes().get(new ResourceLocation(event.getLeft().getTagElement(Tiered.NBT_SUBTAG_KEY).getString("Tier")));
				if (reforgedAttribute.getReforgeItem() != null) {
					if (BuiltInRegistries.ITEM.getKey(event.getRight().getItem()).equals(new ResourceLocation(reforgedAttribute.getReforgeItem())) && (event.getRight().getMaxDamage() - event.getRight().getDamageValue()) >= reforgedAttribute.getReforgeDurabilityCost()) {
						ItemStack copy = event.getLeft().copy();
						copy.removeTagKey(NBT_SUBTAG_KEY);
						event.setOutput(copy);
						event.setCost(reforgedAttribute.getReforgeExperienceCost());
					}
				} else {
					LOGGER.info(reforgedAttribute.getID()+" cannot be reforged because it either does not provide any reforging info or the info it provides is not complete");
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

		if(stack.getItem() instanceof ShieldItem) {
			return slot == EquipmentSlot.MAINHAND || slot == EquipmentSlot.OFFHAND;
		}

		return slot == EquipmentSlot.MAINHAND;
	}

	public static boolean isPreferredAccessorySlot(ItemStack stack, AccessorySlot slot) {
		if(stack.getItem() instanceof AccessoryItem) {
			AccessoryItem item = (AccessoryItem) stack.getItem();
			return item.getAccessorySlots().contains(slot);
		}

		return false;
	}

	public static boolean isPreferredAccessorySlot(ItemStack stack, AccessorySlot.Group group) {
		for (AccessorySlot slot : AccessorySlot.values()) {
			if (slot.getGroup() == group && !isPreferredAccessorySlot(stack, slot)) {
				return false;
			}
		}
		return true;
	}

	public static boolean isPreferredCurioSlot(ItemStack stack, String slot) {
		return stack.is(TagKey.create(Registries.ITEM, new ResourceLocation("curios", slot)));
	}

	@Override
	public void registerClientboundPackets(SimpleChannel arg0) {
		PacketRegistry.registerMessage(channel, 0, ClientboundAttributeSyncerPacket.class, (packetBuffer) -> new ClientboundAttributeSyncerPacket(packetBuffer));
	}

	@Override
	public void registerServerboundPackets(SimpleChannel arg0) {

	}

	public static <T extends Object> Multimap<Attribute, AttributeModifier> AppendAttributesToOriginal(ItemStack stack, T slot, boolean isPreferredSlot, String customAttributes, 
			Multimap<Attribute, AttributeModifier> original, Function<AttributeTemplate,T[]> requiredSlotsArray, 
			Function<AttributeTemplate,T[]> optionalSlotsArray, BiConsumer<AttributeTemplate,Multimap<Attribute, AttributeModifier>> realize) {
		Multimap<Attribute, AttributeModifier> newMap = LinkedListMultimap.create();
		newMap.putAll(original);

		if(stack.getTagElement(Tiered.NBT_SUBTAG_KEY) != null) {
			ResourceLocation tier = new ResourceLocation(stack.getOrCreateTagElement(Tiered.NBT_SUBTAG_KEY).getString(Tiered.NBT_SUBTAG_DATA_KEY));

			if(!stack.hasTag() || !stack.getTag().contains(customAttributes, 9)) {
				PotentialAttribute potentialAttribute = Tiered.ATTRIBUTE_DATA_LOADER.getItemAttributes().get(tier);

				if(potentialAttribute != null) {
					potentialAttribute.getAttributes().forEach(template -> {
						// get required equipment slots
						if(requiredSlotsArray.apply(template) != null) {
							List<T> requiredSlots = new ArrayList<>(Arrays.asList(requiredSlotsArray.apply(template)));
							if(requiredSlots.contains(slot))
								realize.accept(template, newMap);
						}

						// get optional equipment slots
						if(optionalSlotsArray.apply(template) != null) {
							List<T> optionalSlots = new ArrayList<>(Arrays.asList(optionalSlotsArray.apply(template)));
							// optional equipment slots are valid ONLY IF the equipment slot is valid for the thing
							if(optionalSlots.contains(slot) && isPreferredSlot)
								realize.accept(template, newMap);
						}
					});
				}
			}
		}
		return newMap;
	}
}
