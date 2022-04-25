package com.stereowalker.tiered.api;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ForgeArmorTags {

    public static final TagKey<Item> HELMETS = register("helmets");
    public static final TagKey<Item> CHESTPLATES = register("chestplates");
    public static final TagKey<Item> LEGGINGS = register("leggings");
    public static final TagKey<Item> BOOTS = register("boots");
    public static final TagKey<Item> SHIELDS = register("shields");

    private ForgeArmorTags() { }

    public static void init() {

    }

    private static TagKey<Item> register(String id) {
        return TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("forge", id));
    }
}
