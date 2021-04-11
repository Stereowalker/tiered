package draylar.tiered.api;

import com.google.common.collect.Multimap;
import com.google.gson.annotations.SerializedName;

import draylar.tiered.Tiered;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

/**
 * Stores information on an AttributeModifier template applied to an ItemStack.
 *
 * The ID of the AttributeTemplate is the logical ID used to determine what "type" of attribute of is.
 * An AttributeModifier has:
 *   - a UUID, which is a unique identifier to separate different attributes of the same type
 *   - a name, which is used for generating a non-specified UUID and displaying in tooltips in some context
 *   - an amount, which is used in combination with the operation to modify the final relevant value
 *   - a modifier, which can be something such as addition or subtraction
 *
 * The EquipmentSlotType is used to only apply this template to certain items.
 */
public class AttributeTemplate {

    @SerializedName("type")
    private final String attributeTypeID;

    @SerializedName("modifier")
    private final AttributeModifier attributeModifier;

    @SerializedName("required_equipment_slots")
    private final EquipmentSlotType[] requiredEquipmentSlotTypes;

    @SerializedName("optional_equipment_slots")
    private final EquipmentSlotType[] optionalEquipmentSlotTypes;

    public AttributeTemplate(String attributeTypeID, AttributeModifier AttributeModifier, EquipmentSlotType[]  requiredEquipmentSlotTypes, EquipmentSlotType[]  optionalEquipmentSlotTypes) {
        this.attributeTypeID = attributeTypeID;
        this.attributeModifier = AttributeModifier;
        this.requiredEquipmentSlotTypes = requiredEquipmentSlotTypes;
        this.optionalEquipmentSlotTypes = optionalEquipmentSlotTypes;
    }

    public EquipmentSlotType[]  getRequiredEquipmentSlot() {
        return requiredEquipmentSlotTypes;
    }

    public EquipmentSlotType[]  getOptionalEquipmentSlot() {
        return optionalEquipmentSlotTypes;
    }

    /**
     * Uses this {@link AttributeTemplate} to create an {@link AttributeModifier}, which is placed into the given {@link Multimap}.
     * <p>Note that this method assumes the given {@link Multimap} is mutable.
     *
     * @param multimap  map to add {@link AttributeTemplate}
     * @param slot
     */
    public void realize(Multimap<Attribute, AttributeModifier> multimap, EquipmentSlotType slot) {
        AttributeModifier cloneModifier = new AttributeModifier(
                Tiered.MODIFIERS[slot.getSlotIndex()],
                attributeModifier.getName() + "_" + slot.getName(),
                attributeModifier.getAmount(),
                attributeModifier.getOperation()
        );

        Attribute key = Registry.ATTRIBUTE.getOrDefault(new ResourceLocation(attributeTypeID));
        if(key == null) {
            Tiered.LOGGER.warn(String.format("%s was referenced as an attribute type, but it does not exist! A data file in /tiered/item_attributes/ has an invalid type property.", attributeTypeID));
        } else {
            multimap.put(key, cloneModifier);
        }
    }
}
