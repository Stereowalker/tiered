package com.stereowalker.tiered.mixin;

import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.stereowalker.tiered.Tiered;

import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.PreparableReloadListener;

@Mixin(ReloadableServerResources.class)
public class ReloadableServerResourcesMixin {

    @Inject(at = @At("RETURN"), method = "listeners", cancellable = true)
    private void onInit(CallbackInfoReturnable<List<PreparableReloadListener>> ci) {
    	ci.setReturnValue(List.of(ArrayUtils.add(ci.getReturnValue().toArray(new PreparableReloadListener[0]), Tiered.ATTRIBUTE_DATA_LOADER)));
    }
}
