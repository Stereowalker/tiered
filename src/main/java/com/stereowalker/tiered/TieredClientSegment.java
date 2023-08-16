package com.stereowalker.tiered;

import com.stereowalker.unionlib.mod.ClientSegment;

import net.minecraft.resources.ResourceLocation;

public class TieredClientSegment extends ClientSegment {

	@Override
	public ResourceLocation getModIcon() {
		return new ResourceLocation("tiered", "textures/icon.png");
	}

}
