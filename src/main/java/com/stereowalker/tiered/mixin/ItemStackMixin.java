package com.stereowalker.tiered.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.google.common.collect.Multimap;
import com.stereowalker.tiered.Tiered;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Redirect(
            method = "getAttributeModifiers",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;getAttributeModifiers(Lnet/minecraft/world/entity/EquipmentSlot;Lnet/minecraft/world/item/ItemStack;)Lcom/google/common/collect/Multimap;")
    )
    private Multimap<Attribute, AttributeModifier> go(Item item, EquipmentSlot slot, ItemStack thisStack) {
    	return Tiered.AppendAttributesToOriginal(thisStack, slot, Tiered.isPreferredEquipmentSlot(thisStack, slot), "AttributeModifiers", item.getAttributeModifiers(slot, thisStack),
				template -> template.getRequiredEquipmentSlot(), 
				template -> template.getOptionalEquipmentSlot(), 
				(template, newMap) -> template.realize(newMap, slot));
    }
}
