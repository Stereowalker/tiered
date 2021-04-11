package draylar.tiered.api;

import net.minecraft.item.Item;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeTagHandler;
import net.minecraftforge.registries.ForgeRegistries;

public class ForgeToolTags {

    public static final ITag<Item> SWORD = register("swords");
    public static final ITag<Item> AXE = register("axes");
    public static final ITag<Item> HOE = register("hoes");
    public static final ITag<Item> SHOVEL = register("shovels");
    public static final ITag<Item> PICKAXE = register("pickaxes");

    private ForgeToolTags() { }

    public static void init() {

    }

    private static ITag<Item> register(String id) {
        return ForgeTagHandler.makeWrapperTag(ForgeRegistries.ITEMS, new ResourceLocation("forge", id));
    }
}
