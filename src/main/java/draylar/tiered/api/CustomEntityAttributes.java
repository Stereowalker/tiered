package draylar.tiered.api;

import java.util.ArrayList;
import java.util.List;

import draylar.tiered.Tiered;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.registries.IForgeRegistry;

public class CustomEntityAttributes {

public static List<Attribute> ATTRIBUTE = new ArrayList<Attribute>();
    public static final Attribute CRIT_CHANCE = register(new RangedAttribute("generic.crit_chance", 0.0D, 0.0D, 1D).setSyncable(true));
//    public static final Attribute DURABLE = new ClampedAttribute(null, "generic.durable", 0.0D, 0.0D, 1D).setTracked(true);

    public static void init() {
        // NO-OP
    }

    private static Attribute register(Attribute attribute) {
    	attribute.setRegistryName(Tiered.id(attribute.getDescriptionId()));
		ATTRIBUTE.add(attribute);
		return attribute;
    }
    
	public static void registerAll(IForgeRegistry<Attribute> registry) {
		for(Attribute attribute : ATTRIBUTE) {
			registry.register(attribute);
		}
	}
}
