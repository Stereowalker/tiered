package draylar.tiered.api;

import draylar.tiered.Tiered;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;

public class ItemVerifier {

    private final String id;
    private final String tag;

    public ItemVerifier(String id, String tag) {
        this.id = id;
        this.tag = tag;
    }

    /**
     * Returns whether the given {@link ResourceLocation} is valid for this ItemVerifier, which may check direct against either a {@link ResourceLocation} or {@link Tag<Item>}.
     * <p>The given {@link ResourceLocation} should be the ID of an {@link Item} in {@link Registry#ITEM}.
     *
     * @param itemID  item registry ID to check against this verifier
     * @return  whether the check succeeded
     */
    public boolean isValid(ResourceLocation itemID) {
        return isValid(itemID.toString());
    }

    /**
     * Returns whether the given {@link String} is valid for this ItemVerifier, which may check direct against either a {@link ResourceLocation} or {@link Tag<Item>}.
     * <p>The given {@link String} should be the ID of an {@link Item} in {@link Registry#ITEM}.
     *
     * @param itemID  item registry ID to check against this verifier
     * @return  whether the check succeeded
     */
    public boolean isValid(String itemID) {
        if(id != null) {
            return itemID.equals(id);
        } else if(tag != null) {
            Tag<Item> itemTag = ItemTags.getAllTags().getTag(new ResourceLocation(tag));

            if(itemTag != null) {
                return itemTag.contains(Registry.ITEM.get(new ResourceLocation(itemID)));
            } else {
                Tiered.LOGGER.error(tag + " was specified as an item verifier tag, but it does not exist!");
            }
        }

        return false;
    }
}
