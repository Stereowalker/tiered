package draylar.tiered.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.authlib.GameProfile;

import draylar.tiered.Tiered;
import draylar.tiered.api.ModifierUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerEntityMixin extends Player {

    private NonNullList<ItemStack> mainCopy = null;

    private ServerPlayerEntityMixin(Level world, BlockPos pos, float yaw, GameProfile profile) {
        super(world, pos, yaw, profile);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        // if items copy is null, set it to player inventory and check each stack
        if(mainCopy == null) {
            mainCopy = copyDefaultedList(inventory.items);
            runCheck();
        }

        // if items copy =/= inventory, run check and set mainCopy to inventory
        if (!inventory.items.equals(mainCopy)) {
            mainCopy = copyDefaultedList(inventory.items);
            runCheck();
        }
    }

    @Unique
    private NonNullList<ItemStack> copyDefaultedList(NonNullList<ItemStack> list) {
        NonNullList<ItemStack> newList = NonNullList.withSize(36, ItemStack.EMPTY);

        for (int i = 0; i < list.size(); i++) {
            newList.set(i, list.get(i));
        }

        return newList;
    }

    @Unique
    private void runCheck() {
        inventory.items.forEach(itemStack -> {
            // no tier on item
            if(itemStack.getTagElement(Tiered.NBT_SUBTAG_KEY) == null) {
                // attempt to get a random tier
                ResourceLocation potentialAttributeID = ModifierUtils.getRandomAttributeIDFor(itemStack.getItem());

                // found an ID
                if(potentialAttributeID != null) {
                    itemStack.getOrCreateTagElement(Tiered.NBT_SUBTAG_KEY).putString(Tiered.NBT_SUBTAG_DATA_KEY, potentialAttributeID.toString());
                }
            }
        });
    }
}
