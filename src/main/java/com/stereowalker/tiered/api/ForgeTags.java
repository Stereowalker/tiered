package com.stereowalker.tiered.api;

import com.stereowalker.unionlib.util.RegistryHelper;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ForgeTags {

	//Armor
    public static final TagKey<Item> SHIELDS = register("shields");
    //Tools

    private ForgeTags() { }

    public static void init() {

    }

    private static TagKey<Item> register(String id) {
        return TagKey.create(RegistryHelper.itemKey(), new ResourceLocation("forge", id));
    }
}
