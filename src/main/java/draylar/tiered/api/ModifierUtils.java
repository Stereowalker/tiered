package draylar.tiered.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import draylar.tiered.Tiered;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class ModifierUtils {

    /**
     * Returns the ID of a random attribute that is valid for the given {@link Item} in {@link ResourceLocation} form.
     * <p> If there is no valid attribute for the given {@link Item}, null is returned.
     *
     * @param item  {@link Item} to generate a random attribute for
     * @return  id of random attribute for item in {@link ResourceLocation} form, or null if there are no valid options
     */
    public static ResourceLocation getRandomAttributeIDFor(Item item) {
        List<ResourceLocation> potentialAttributes = new ArrayList<>();

        // collect all valid attributes for the given item
        Tiered.ATTRIBUTE_DATA_LOADER.getItemAttributes().forEach((id, attribute) -> {
            if(attribute.isValid(Registry.ITEM.getKey(item))) {
                potentialAttributes.add(new ResourceLocation(attribute.getID()));
            }
        });

        // return a random attribute if there are any, or null if there are none
        if(potentialAttributes.size() > 0) {
            return potentialAttributes.get(new Random().nextInt(potentialAttributes.size()));
        } else {
            return null;
        }
    }

    private ModifierUtils() {
        // no-op
    }
}