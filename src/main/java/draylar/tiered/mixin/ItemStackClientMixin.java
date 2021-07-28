package draylar.tiered.mixin;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

import draylar.tiered.Tiered;
import draylar.tiered.api.PotentialAttribute;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

@Mixin(ItemStack.class)
public abstract class ItemStackClientMixin {

    @Shadow public abstract CompoundNBT getOrCreateChildTag(String key);

    @Shadow public abstract boolean hasTag();

    @Shadow public abstract CompoundNBT getChildTag(String key);

    private boolean isTiered = false;

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/attributes/AttributeModifier;getAmount()D"), method = "getTooltip", locals = LocalCapture.CAPTURE_FAILHARD)
    private void storeAttributeModifier(PlayerEntity player, ITooltipFlag context, CallbackInfoReturnable<List> cir, List list, IFormattableTextComponent component, int i , EquipmentSlotType var6[], int var7, int var8, EquipmentSlotType equipmentSlot, Multimap multimap, Iterator var11, Map.Entry entry, AttributeModifier entityAttributeModifier) {
        isTiered = entityAttributeModifier.getName().contains("tiered:");
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/util/text/TranslationTextComponent;mergeStyle(Lnet/minecraft/util/text/TextFormatting;)Lnet/minecraft/util/text/IFormattableTextComponent;", ordinal = 2), method = "getTooltip")
    private IFormattableTextComponent getTextFormatting(TranslationTextComponent translatableText, TextFormatting formatting) {
        if(this.hasTag() && this.getChildTag(Tiered.NBT_SUBTAG_KEY) != null && isTiered) {
            ResourceLocation tier = new ResourceLocation(this.getOrCreateChildTag(Tiered.NBT_SUBTAG_KEY).getString(Tiered.NBT_SUBTAG_DATA_KEY));
            PotentialAttribute attribute = Tiered.ATTRIBUTE_DATA_LOADER.getItemAttributes().get(tier);

            return translatableText.setStyle(attribute.getStyle());
        } else {
            return translatableText.mergeStyle(formatting);
        }
    }

    @ModifyVariable(
            method = "getTooltip",
            at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Multimap;isEmpty()Z"),
            index = 10
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

    @Inject(
            method = "getDisplayName",
            at = @At("RETURN"),
            cancellable = true
    )
    private void modifyName(CallbackInfoReturnable<ITextComponent> cir) {
        if(this.hasTag() && this.getChildTag("display") == null && this.getChildTag(Tiered.NBT_SUBTAG_KEY) != null) {
            ResourceLocation tier = new ResourceLocation(getOrCreateChildTag(Tiered.NBT_SUBTAG_KEY).getString(Tiered.NBT_SUBTAG_DATA_KEY));

            // attempt to display attribute if it is valid
            PotentialAttribute potentialAttribute = Tiered.ATTRIBUTE_DATA_LOADER.getItemAttributes().get(tier);

            if(potentialAttribute != null) {
                cir.setReturnValue(new TranslationTextComponent(potentialAttribute.getID() + ".label").appendString(" ").appendSibling(cir.getReturnValue()).setStyle(potentialAttribute.getStyle()));
            }
        }
    }
}
