package draylar.tiered.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import draylar.tiered.Tiered;
import net.minecraft.command.Commands;
import net.minecraft.resources.DataPackRegistries;
import net.minecraft.resources.IReloadableResourceManager;

@Mixin(DataPackRegistries.class)
public class ServerResourceManagerMixin {

    @Shadow @Final private IReloadableResourceManager resourceManager;

    @Inject(at = @At("RETURN"), method = "<init>")
    private void onInit(Commands.EnvironmentType registrationEnvironment, int i, CallbackInfo ci) {
        this.resourceManager.addReloadListener(Tiered.ATTRIBUTE_DATA_LOADER);
    }
}
