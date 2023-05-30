package com.stereowalker.tiered.api;

import com.stereowalker.unionlib.util.RegistryHelper;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ForgeTags {

	//Armor
    public static final TagKey<Item> HELMETS = register("helmets");
    public static final TagKey<Item> CHESTPLATES = register("chestplates");
    public static final TagKey<Item> LEGGINGS = register("leggings");
    public static final TagKey<Item> BOOTS = register("boots");
    public static final TagKey<Item> SHIELDS = register("shields");
    //Tools
    public static final TagKey<Item> SWORD = register("swords");
    public static final TagKey<Item> AXE = register("axes");
    public static final TagKey<Item> HOE = register("hoes");
    public static final TagKey<Item> SHOVEL = register("shovels");
    public static final TagKey<Item> PICKAXE = register("pickaxes");
    public static final TagKey<Item> BOWS = register("bows");

    private ForgeTags() { }

    public static void init() {

    }

    private static TagKey<Item> register(String id) {
        return TagKey.create(RegistryHelper.itemKey(), new ResourceLocation("forge", id));
    }
}
