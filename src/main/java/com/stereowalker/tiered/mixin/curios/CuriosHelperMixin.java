package com.stereowalker.tiered.mixin.curios;

import java.util.UUID;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.stereowalker.tiered.Tiered;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.util.ICuriosHelper;
import top.theillusivec4.curios.common.CuriosHelper;

@Mixin(CuriosHelper.class)
public abstract class CuriosHelperMixin implements ICuriosHelper {
	
    @Inject(remap = false,
            method = "getAttributeModifiers(Ltop/theillusivec4/curios/api/SlotContext;Ljava/util/UUID;Lnet/minecraft/world/item/ItemStack;)Lcom/google/common/collect/Multimap;",
            at = @At(value = "RETURN", ordinal = 1), cancellable = true
    )
    private void go(SlotContext slotContext, UUID uuid, ItemStack stack, CallbackInfoReturnable<Multimap<Attribute, AttributeModifier>> cir) {
    	cir.setReturnValue(getCurio(stack).map(curio -> {
    		return Tiered.AppendAttributesToOriginal(stack, slotContext.identifier(), Tiered.isPreferredCurioSlot(stack, slotContext.identifier()), "CurioAttributeModifiers", curio.getAttributeModifiers(slotContext, uuid),
    				template -> template.getRequiredCurioSlot(), 
    				template -> template.getOptionalCurioSlot(), 
    				(template, newMap) -> template.realize(newMap, slotContext.identifier()));
    		
    	})
        .orElse(Tiered.AppendAttributesToOriginal(stack, slotContext.identifier(), Tiered.isPreferredCurioSlot(stack, slotContext.identifier()), "CurioAttributeModifiers", HashMultimap.create(),
				template -> template.getRequiredCurioSlot(), 
				template -> template.getOptionalCurioSlot(), 
				(template, newMap) -> template.realize(newMap, slotContext.identifier()))));
    }
}
