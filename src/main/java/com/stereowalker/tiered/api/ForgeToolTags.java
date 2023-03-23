package com.stereowalker.tiered.api;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ForgeToolTags {

    public static final TagKey<Item> SWORD = register("swords");
    public static final TagKey<Item> AXE = register("axes");
    public static final TagKey<Item> HOE = register("hoes");
    public static final TagKey<Item> SHOVEL = register("shovels");
    public static final TagKey<Item> PICKAXE = register("pickaxes");
    public static final TagKey<Item> BOWS = register("bows");

    private ForgeToolTags() { }

    public static void init() {

    }

    private static TagKey<Item> register(String id) {
        return TagKey.create(Registries.ITEM, new ResourceLocation("forge", id));
    }
}
