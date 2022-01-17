package draylar.tiered.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.mutable.MutableInt;

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
		List<ResourceLocation> chosenAttribute = new ArrayList<>();

//		System.out.println();
		MutableInt totalWeight = new MutableInt();
		// collect all valid attributes for the given item
		Tiered.ATTRIBUTE_DATA_LOADER.getItemAttributes().forEach((id, attribute) -> {
			if(attribute.isValid(Registry.ITEM.getKey(item))) {
//				System.out.println("Adding Weights "+id+" "+attribute.getWeight()+" ("+totalWeight.getValue());
				potentialAttributes.add(new ResourceLocation(attribute.getID()));
				//Implementation of weighted system
				totalWeight.add(attribute.getWeight());
				//
			}
		});
//		System.out.println();
		//Implementation of weighted system
		if (totalWeight.getValue() > 0) {
			MutableInt randomAttribute = new MutableInt(new Random().nextInt(totalWeight.getValue())+1);
			MutableInt i = new MutableInt();
			Tiered.ATTRIBUTE_DATA_LOADER.getItemAttributes().forEach((id, attribute) -> {
				if(attribute.isValid(Registry.ITEM.getKey(item))) {
//					System.out.println("Checking Weights "+id+" "+attribute.getWeight()+" ("+i.getValue()+">"+totalWeight.getValue()+")");
					i.add(attribute.getWeight());
					if (i.getValue() >= randomAttribute.getValue()) {
						chosenAttribute.add(id);
					}
				}
			});
		}
		//

		// return an attribute with a random weight
		if(chosenAttribute.size() > 0) {
			return chosenAttribute.get(0);
		}
		//
		// return a random attribute if there are any, or null if there are none
		else if(potentialAttributes.size() > 0) {
			return potentialAttributes.get(new Random().nextInt(potentialAttributes.size()));
		} else {
			return null;
		}
	}

	private ModifierUtils() {
		// no-op
	}
}