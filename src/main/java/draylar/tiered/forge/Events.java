package draylar.tiered.forge;

import java.util.List;

import draylar.tiered.Tiered;
import draylar.tiered.api.PotentialAttribute;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(value = Dist.CLIENT)
public class Events {


    /**
     * Creates an {@link ItemTooltipCallback} listener that adds the modifier name at the top of an Item tooltip.
     * <p>A tool name is only displayed if the item has a modifier.
     */
    @SubscribeEvent
    public static void setupModifierLabel(ItemTooltipEvent event) {
//    	ItemStack stack = event.getItemStack();
////    	tooltipContext, 
//    	List<ITextComponent> lines = event.getToolTip();
////        ItemTooltipCallback.EVENT.register((stack, tooltipContext, lines) -> {
//            // has tier
//            if(stack.getChildTag(Tiered.NBT_SUBTAG_KEY) != null) {
//                // get tier
//                ResourceLocation tier = new ResourceLocation(stack.getOrCreateChildTag(Tiered.NBT_SUBTAG_KEY).getString(Tiered.NBT_SUBTAG_DATA_KEY));
//
//                // attempt to display attribute if it is valid
//                PotentialAttribute potentialAttribute = Tiered.ATTRIBUTE_DATA_LOADER.getItemAttributes().get(tier);
//
//                if(potentialAttribute != null) {
//                    lines.add(1, new TranslationTextComponent(potentialAttribute.getID() + ".label").setStyle(potentialAttribute.getStyle()));
//                }
//            }
////        });
    }
}
