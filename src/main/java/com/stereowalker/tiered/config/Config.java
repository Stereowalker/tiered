package com.stereowalker.tiered.config;

import com.stereowalker.unionlib.config.UnionConfig;

@UnionConfig(name = "reforged", translatableName = "gui.reforged.config")
public class Config {
	@UnionConfig.Entry(name = "Can Broken Items Be Reforged")
	public static boolean canReforgeBroken = false;
}
