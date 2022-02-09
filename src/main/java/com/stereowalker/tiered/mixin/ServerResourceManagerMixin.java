package com.stereowalker.tiered.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.stereowalker.tiered.Tiered;

import net.minecraft.commands.Commands;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.ServerResources;
import net.minecraft.server.packs.resources.ReloadableResourceManager;

@Mixin(ServerResources.class)
public class ServerResourceManagerMixin {

    @Shadow @Final private ReloadableResourceManager resources;

    @Inject(at = @At("RETURN"), method = "<init>")
    private void onInit(RegistryAccess p_180002_, Commands.CommandSelection registrationEnvironment, int i, CallbackInfo ci) {
        this.resources.registerReloadListener(Tiered.ATTRIBUTE_DATA_LOADER);
    }
}
