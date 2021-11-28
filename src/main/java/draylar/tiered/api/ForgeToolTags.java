package draylar.tiered.api;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeTagHandler;
import net.minecraftforge.registries.ForgeRegistries;

public class ForgeToolTags {

    public static final Tag<Item> SWORD = register("swords");
    public static final Tag<Item> AXE = register("axes");
    public static final Tag<Item> HOE = register("hoes");
    public static final Tag<Item> SHOVEL = register("shovels");
    public static final Tag<Item> PICKAXE = register("pickaxes");

    private ForgeToolTags() { }

    public static void init() {

    }

    private static Tag<Item> register(String id) {
        return ForgeTagHandler.makeWrapperTag(ForgeRegistries.ITEMS, new ResourceLocation("forge", id));
    }
}
