package com.stereowalker.tiered.mixin.curios;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.stereowalker.tiered.Tiered;
import com.stereowalker.tiered.api.PotentialAttribute;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import top.theillusivec4.curios.client.ClientEventHandler;

@Mixin(ClientEventHandler.class)
public abstract class ClientEventHandlerMixin {

    private static boolean isTiered = false;

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/attributes/AttributeModifier;getAmount()D"), method = "onTooltip", locals = LocalCapture.CAPTURE_FAILHARD)
    private void storeAttributeModifier(ItemTooltipEvent evt, CallbackInfo ci, ItemStack stack, Player player, List tooltip, CompoundTag tag, int i, Set curioTags, List slots, List tagTooltips, MutableComponent slotsTooltip, LazyOptional optionalCurio, List attributeTooltip, Iterator var13, String identifier, Multimap multimap, Iterator var16, Map.Entry entry, AttributeModifier attributemodifier) {
        isTiered = attributemodifier.getName().contains("tiered:");
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/network/chat/TranslatableComponent;withStyle(Lnet/minecraft/ChatFormatting;)Lnet/minecraft/network/chat/MutableComponent;", ordinal = 3), method = "onTooltip")
    private MutableComponent getTextFormatting(TranslatableComponent translatableText, ChatFormatting formatting, ItemTooltipEvent evt) {
        if(evt.getItemStack().hasTag() && evt.getItemStack().getTagElement(Tiered.NBT_SUBTAG_KEY) != null && isTiered) {
            ResourceLocation tier = new ResourceLocation(evt.getItemStack().getOrCreateTagElement(Tiered.NBT_SUBTAG_KEY).getString(Tiered.NBT_SUBTAG_DATA_KEY));
            PotentialAttribute attribute = Tiered.ATTRIBUTE_DATA_LOADER.getItemAttributes().get(tier);

            return translatableText.setStyle(attribute.getStyle());
        } else {
            return translatableText.withStyle(formatting);
        }
    }

    @ModifyVariable(remap = false,
            method = "onTooltip",
            at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Multimap;isEmpty()Z"),
            index = 15
    )
    private Multimap<Attribute, AttributeModifier> sort(Multimap<Attribute, AttributeModifier> map) {
        Multimap<Attribute, AttributeModifier> vanillaFirst = LinkedListMultimap.create();
        Multimap<Attribute, AttributeModifier> remaining = LinkedListMultimap.create();

        map.forEach((entityAttribute, entityAttributeModifier) -> {
            if (!entityAttributeModifier.getName().contains("tiered")) {
                vanillaFirst.put(entityAttribute, entityAttributeModifier);
            } else {
                remaining.put(entityAttribute, entityAttributeModifier);
            }
        });

        vanillaFirst.putAll(remaining);
        return vanillaFirst;
    }
}
