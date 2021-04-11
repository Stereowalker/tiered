package draylar.tiered.mixin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

import draylar.tiered.Tiered;
import draylar.tiered.api.PotentialAttribute;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Shadow public abstract CompoundNBT getOrCreateChildTag(String key);

    @Shadow public abstract CompoundNBT getTag();

    @Shadow public abstract boolean hasTag();

    @Shadow public abstract CompoundNBT getChildTag(String key);

    @Redirect(
            method = "getAttributeModifiers",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;getAttributeModifiers(Lnet/minecraft/inventory/EquipmentSlotType;Lnet/minecraft/item/ItemStack;)Lcom/google/common/collect/Multimap;")
    )
    private Multimap<Attribute, AttributeModifier> go(Item item, EquipmentSlotType slot, ItemStack thisStack) {
        Multimap<Attribute, AttributeModifier> mods = item.getAttributeModifiers(slot, thisStack);
        Multimap<Attribute, AttributeModifier> newMap = LinkedListMultimap.create();
        newMap.putAll(mods);

        if(getChildTag(Tiered.NBT_SUBTAG_KEY) != null) {
            ResourceLocation tier = new ResourceLocation(getOrCreateChildTag(Tiered.NBT_SUBTAG_KEY).getString(Tiered.NBT_SUBTAG_DATA_KEY));

            if(!hasTag() || !getTag().contains("AttributeModifiers", 9)) {
                PotentialAttribute potentialAttribute = Tiered.ATTRIBUTE_DATA_LOADER.getItemAttributes().get(tier);

                if(potentialAttribute != null) {
                    potentialAttribute.getAttributes().forEach(template -> {
                        // get required equipment slots
                        if(template.getRequiredEquipmentSlot() != null) {
                            List<EquipmentSlotType> requiredEquipmentSlots = new ArrayList<>(Arrays.asList(template.getRequiredEquipmentSlot()));

                            if(requiredEquipmentSlots.contains(slot)) {
                                template.realize(newMap, slot);
                            }
                        }

                        // get optional equipment slots
                        if(template.getOptionalEquipmentSlot() != null) {
                            List<EquipmentSlotType> optionalEquipmentSlots = new ArrayList<>(Arrays.asList(template.getOptionalEquipmentSlot()));

                            // optional equipment slots are valid ONLY IF the equipment slot is valid for the thing
                            if(optionalEquipmentSlots.contains(slot) && Tiered.isPreferredEquipmentSlot((ItemStack) (Object) this, slot)) {
                                template.realize(newMap, slot);
                            }
                        }
                    });
                }
            }
        }

        return newMap;
    }
}
