package com.stereowalker.tiered.api;

import java.util.List;

import com.stereowalker.unionlib.util.GeneralUtilities.WeightedObject;

import net.minecraft.resources.ResourceLocation;

public class TierPool implements WeightedObject {

	private final int weight;
	private final List<ItemVerifier> verifiers;
	private final List<ItemVerifier> exclusions;

	private final List<String> tiers;

	public TierPool(String id, int weight, List<ItemVerifier> verifiers, List<ItemVerifier> exclusions, List<String> tiers) {
		this.weight = weight;
		this.verifiers = verifiers;
		this.exclusions = exclusions;
		this.tiers = tiers;
	}

	public int getWeight() {
		return weight;
	}

	public List<ItemVerifier> getVerifiers() {
		return verifiers;
	}

	public List<ItemVerifier> getExclusions() {
		return exclusions;
	}

	public boolean isValid(ResourceLocation id) {
		if (exclusions != null)
			for(ItemVerifier exclusion : exclusions)
				if(exclusion.isValid(id)) return false;
		for(ItemVerifier verifier : verifiers)
			if(verifier.isValid(id)) return true;
		return false;
	}


	public List<String> getTiers() {
		return tiers;
	}
}
