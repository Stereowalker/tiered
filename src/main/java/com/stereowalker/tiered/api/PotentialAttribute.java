package com.stereowalker.tiered.api;

import java.util.List;

import com.stereowalker.unionlib.util.GeneralUtilities.WeightedObject;

import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;

public class PotentialAttribute implements WeightedObject {

    private final String id;
    private final int weight;
    
    private final int reforge_durability_cost;
    private final int reforge_experience_cost;
    private final String reforge_item;
    private final List<ItemVerifier> verifiers;
    private final Style style;
    private final List<AttributeTemplate> attributes;

    public PotentialAttribute(String id, int weight, int reforge_durability_cost, int reforge_experience_cost, String reforge_item, List<ItemVerifier> verifiers, Style style, List<AttributeTemplate> attributes) {
        this.id = id;
        this.weight = weight;
        this.reforge_durability_cost = reforge_durability_cost;
        this.reforge_experience_cost = reforge_experience_cost;
        this.reforge_item = reforge_item;
        this.verifiers = verifiers;
        this.style = style;
        this.attributes = attributes;
    }
    
    public String getID() {
        return id;
    }
    
    public int getWeight() {
		return weight;
	}
    
    public int getReforgeDurabilityCost() {
		return Math.max(reforge_durability_cost, 1);
	}
    
    public int getReforgeExperienceCost() {
		return Math.max(reforge_experience_cost, 1);
	}
    
    public String getReforgeItem() {
		return reforge_item;
	}

    public List<ItemVerifier> getVerifiers() {
        return verifiers;
    }

    public boolean isValid(ResourceLocation id) {
        for(ItemVerifier verifier : verifiers) {
            if(verifier.isValid(id)) {
                return true;
            }
        }

        return false;
    }

    public Style getStyle() {
        return style;
    }

    public List<AttributeTemplate> getAttributes() {
        return attributes;
    }
}
