package com.stereowalker.tiered.mixin.unionlib;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.google.common.collect.Multimap;
import com.stereowalker.tiered.Tiered;
import com.stereowalker.unionlib.hook.AccessoryStackCalls;
import com.stereowalker.unionlib.world.entity.AccessorySlot;
import com.stereowalker.unionlib.world.item.AccessoryItem;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;

@Mixin(AccessoryStackCalls.class)
public abstract class AccessoryStackCallsMixin {
	@Redirect(
			method = "getAttributeModifiers",
			at = @At(value = "INVOKE", target = "Lcom/stereowalker/unionlib/world/item/AccessoryItem;getAttributeModifiers(Lcom/stereowalker/unionlib/world/entity/AccessorySlot;Lnet/minecraft/world/item/ItemStack;)Lcom/google/common/collect/Multimap;")
			)
	private static Multimap<Attribute, AttributeModifier> go(AccessoryItem item, AccessorySlot slot, ItemStack stack) {
		return Tiered.AppendAttributesToOriginal(stack, slot, Tiered.isPreferredAccessorySlot(stack, slot), "AccessoryAttributeModifiers", item.getAttributeModifiers(slot, stack),
				template -> template.getRequiredAccessorySlot(), 
				template -> template.getOptionalAccessorySlot(), 
				(template, newMap) -> template.realize(newMap, slot));
	}

	@Redirect(
			method = "getAttributeModifiersForGroup",
			at = @At(value = "INVOKE", target = "Lcom/stereowalker/unionlib/world/item/AccessoryItem;getAttributeModifiers(Lcom/stereowalker/unionlib/world/entity/AccessorySlot$Group;Lnet/minecraft/world/item/ItemStack;)Lcom/google/common/collect/Multimap;")
			)
	private static Multimap<Attribute, AttributeModifier> go2(AccessoryItem item, AccessorySlot.Group group, ItemStack stack) {
		return Tiered.AppendAttributesToOriginal(stack, group, Tiered.isPreferredAccessorySlot(stack, group), "AccessoryAttributeModifiers", item.getAttributeModifiers(group, stack),
				template -> template.getRequiredAccessoryGroup(), 
				template -> template.getOptionalAccessoryGroup(), 
				(template, newMap) -> template.realize(newMap, group));
	}
}
