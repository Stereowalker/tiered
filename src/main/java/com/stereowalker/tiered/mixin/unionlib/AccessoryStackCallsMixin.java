package com.stereowalker.tiered.mixin.unionlib;

import java.util.function.BiConsumer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.stereowalker.tiered.Tiered;
import com.stereowalker.unionlib.hook.AccessoryStackCalls;
import com.stereowalker.unionlib.world.entity.AccessorySlot;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;

@Mixin(AccessoryStackCalls.class)
public abstract class AccessoryStackCallsMixin {
	@Inject(method = "forEachModifier", at = @At("TAIL"), remap = false)
    public void go(ItemStack thisStack, AccessorySlot slot, BiConsumer<Holder<Attribute>, AttributeModifier> pAction, CallbackInfo ci) {
    	Tiered.AppendAttributesToOriginal(thisStack, slot, Tiered.isPreferredAccessorySlot(thisStack, slot), "",
				template -> template.getRequiredAccessorySlot(), 
				template -> template.getOptionalAccessorySlot(), 
				(template) -> template.realize(pAction, slot));
    }
	
//	@Redirect(remap = false, 
//			method = "getAttributeModifiersForGroup",
//			at = @At(value = "INVOKE", target = "Lcom/stereowalker/unionlib/world/item/AccessoryItem;getAttributeModifiers(Lcom/stereowalker/unionlib/world/entity/AccessorySlot$Group;Lnet/minecraft/world/item/ItemStack;)Lcom/google/common/collect/Multimap;")
//			)
//	private static Multimap<Attribute, AttributeModifier> go2(AccessoryItem item, AccessorySlot.Group group, ItemStack stack) {
//		return Tiered.AppendAttributesToOriginal(stack, group, Tiered.isPreferredAccessorySlot(stack, group), "AccessoryAttributeModifiers", item.getAttributeModifiers(group, stack),
//				template -> template.getRequiredAccessoryGroup(), 
//				template -> template.getOptionalAccessoryGroup(), 
//				(template, newMap) -> template.realize(newMap, group));
//	}
}
