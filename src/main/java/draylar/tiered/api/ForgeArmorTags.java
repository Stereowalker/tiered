package draylar.tiered.api;

import net.minecraft.item.Item;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeTagHandler;
import net.minecraftforge.registries.ForgeRegistries;

public class ForgeArmorTags {

    public static final ITag<Item> HELMETS = register("helmets");
    public static final ITag<Item> CHESTPLATES = register("chestplates");
    public static final ITag<Item> LEGGINGS = register("leggings");
    public static final ITag<Item> BOOTS = register("boots");

    private ForgeArmorTags() { }

    public static void init() {

    }

    private static ITag<Item> register(String id) {
        return ForgeTagHandler.makeWrapperTag(ForgeRegistries.ITEMS, new ResourceLocation("forge", id));
    }
}
