package com.stereowalker.tiered.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.stereowalker.tiered.Tiered;
import com.stereowalker.unionlib.util.GeneralUtilities;
import com.stereowalker.unionlib.util.RegistryHelper;

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
			if(attribute.isValid(RegistryHelper.items().getKey(item)))
				potentialAttributes.add(new ResourceLocation(attribute.getID()));
		});
		PotentialAttribute attr = GeneralUtilities.getRandomFrom(Tiered.ATTRIBUTE_DATA_LOADER.getItemAttributes().values(), 
				(attribute) -> attribute.isValid(RegistryHelper.items().getKey(item)));
		if (attr != null) return new ResourceLocation(attr.getID());
		// This returns a random attr from the list if the weighting system fails
		else if(potentialAttributes.size() > 0) return potentialAttributes.get(new Random().nextInt(potentialAttributes.size()));
		else return null;
	}

	private ModifierUtils() {
		// no-op
	}
}