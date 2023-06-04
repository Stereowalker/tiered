package com.stereowalker.tiered.data;

import org.spongepowered.asm.mixin.Unique;

import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public interface TierAffixer {
	public ServerPlayer player();
	public NonNullList<ItemStack> InvCopy();
	public void SetInvCopy(NonNullList<ItemStack> copy);
	
	@Unique
    public default NonNullList<ItemStack> copyDefaultedList(NonNullList<ItemStack> list) {
        NonNullList<ItemStack> newList = NonNullList.withSize(36, ItemStack.EMPTY);

        for (int i = 0; i < list.size(); i++) {
            newList.set(i, list.get(i));
        }

        return newList;
    }
}
