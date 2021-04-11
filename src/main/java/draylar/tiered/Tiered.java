package draylar.tiered;

import java.util.List;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.stereowalker.unionlib.mod.UnionMod;

import draylar.tiered.api.CustomEntityAttributes;
import draylar.tiered.api.ForgeArmorTags;
import draylar.tiered.api.ForgeToolTags;
import draylar.tiered.api.PotentialAttribute;
import draylar.tiered.data.AttributeDataLoader;
import draylar.tiered.mixin.ServerResourceManagerMixin;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("tiered")
public class Tiered extends UnionMod {

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

    public static final String NBT_SUBTAG_KEY = "Tiered";
    public static final String NBT_SUBTAG_DATA_KEY = "Tier";

    public Tiered() 
	{
    	super("tiered", new ResourceLocation("tiered", "textures/icon.png"), LoadType.BOTH);
		final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		modEventBus.addListener(this::setup);
		modEventBus.addListener(this::clientSetup);
		MinecraftForge.EVENT_BUS.register(this);
	}

	private void setup(final FMLCommonSetupEvent event)
	{
		ForgeArmorTags.init();
		ForgeToolTags.init();
        CustomEntityAttributes.init();
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

    /**
     * Creates an {@link ItemTooltipCallback} listener that adds the modifier name at the top of an Item tooltip.
     * <p>A tool name is only displayed if the item has a modifier.
     */
    @SubscribeEvent
    public static void setupModifierLabel(ItemTooltipEvent event) {
    	ItemStack stack = event.getItemStack();
//    	tooltipContext, 
    	List<ITextComponent> lines = event.getToolTip();
//        ItemTooltipCallback.EVENT.register((stack, tooltipContext, lines) -> {
            // has tier
            if(stack.getChildTag(NBT_SUBTAG_KEY) != null) {
                // get tier
                ResourceLocation tier = new ResourceLocation(stack.getOrCreateChildTag(NBT_SUBTAG_KEY).getString(Tiered.NBT_SUBTAG_DATA_KEY));

                // attempt to display attribute if it is valid
                PotentialAttribute potentialAttribute = Tiered.ATTRIBUTE_DATA_LOADER.getItemAttributes().get(tier);

                if(potentialAttribute != null) {
                    lines.add(1, new TranslationTextComponent(potentialAttribute.getID() + ".label").setStyle(potentialAttribute.getStyle()));
                }
            }
//        });
    }

    public static boolean isPreferredEquipmentSlot(ItemStack stack, EquipmentSlotType slot) {
        if(stack.getItem() instanceof ArmorItem) {
            ArmorItem item = (ArmorItem) stack.getItem();
            return item.getEquipmentSlot().equals(slot);
        }

        return slot == EquipmentSlotType.MAINHAND;
    }
}
