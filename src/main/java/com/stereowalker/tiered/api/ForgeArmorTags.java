package com.stereowalker.tiered.api;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeTagHandler;
import net.minecraftforge.registries.ForgeRegistries;

public class ForgeArmorTags {

    public static final Tag<Item> HELMETS = register("helmets");
    public static final Tag<Item> CHESTPLATES = register("chestplates");
    public static final Tag<Item> LEGGINGS = register("leggings");
    public static final Tag<Item> BOOTS = register("boots");
    public static final Tag<Item> SHIELDS = register("shields");

    private ForgeArmorTags() { }

    public static void init() {

    }

    private static Tag<Item> register(String id) {
        return ForgeTagHandler.makeWrapperTag(ForgeRegistries.ITEMS, new ResourceLocation("forge", id));
    }
}
