package com.stereowalker.tiered.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.authlib.GameProfile;
import com.stereowalker.tiered.Tiered;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
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
            inventory.items.forEach(Tiered::attemptToAffixTier);
        }

        // if items copy =/= inventory, run check and set mainCopy to inventory
        if (!inventory.items.equals(mainCopy)) {
            mainCopy = copyDefaultedList(inventory.items);
            inventory.items.forEach(Tiered::attemptToAffixTier);
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
}
