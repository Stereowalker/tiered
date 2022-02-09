package com.stereowalker.tiered.api;

import java.util.List;

import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;

public class PotentialAttribute {

    private final String id;
    private final int weight;
    private final List<ItemVerifier> verifiers;
    private final Style style;
    private final List<AttributeTemplate> attributes;

    public PotentialAttribute(String id, int weight, List<ItemVerifier> verifiers, Style style, List<AttributeTemplate> attributes) {
        this.id = id;
        this.weight = weight;
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
