package com.stereowalker.tiered.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.stereowalker.tiered.Tiered;
import com.stereowalker.tiered.data.TierAffixer;

import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

@Mixin(ServerPlayer.class)
public class ServerPlayerEntityMixin implements TierAffixer {

	private NonNullList<ItemStack> mainCopy = null;

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
       
    }

	@Override
	public ServerPlayer player() {
		return (ServerPlayer)(Object)this;
	}

	@Override
	public NonNullList<ItemStack> InvCopy() {
		return mainCopy;
	}

	@Override
	public void SetInvCopy(NonNullList<ItemStack> copy) {
		this.mainCopy = copy;
	}
}
