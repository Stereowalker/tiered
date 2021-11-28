package draylar.tiered.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import draylar.tiered.api.CustomEntityAttributes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

@Mixin(Player.class)
public abstract class PlayerEntityMixin extends LivingEntity {

    private PlayerEntityMixin(EntityType<? extends LivingEntity> type, Level world) {
        super(type, world);
    }

    @Inject(
            method = "createAttributes",
            at = @At("RETURN")
    )
    private static void initAttributes(CallbackInfoReturnable<AttributeSupplier.Builder> ci) {
        ci.getReturnValue().add(CustomEntityAttributes.CRIT_CHANCE);
    }

    @ModifyVariable(
            method = "attack",
            at = @At(
                    value = "JUMP",
                    ordinal = 2
            ),
            slice = @Slice(
                    from = @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/world/entity/player/Player;isSprinting()Z",
                            ordinal = 1
                    )
            ),
            index = 8
    )
    private boolean attack(boolean bl3) {
        float customChance = 0;

        AttributeInstance instance = this.getAttribute(CustomEntityAttributes.CRIT_CHANCE);

        if(instance != null) {
        	for (AttributeModifier modifier : instance.getModifiers()) {
        		float amount = (float) modifier.getAmount();
        		customChance += amount;
        	}
        }

        return bl3 || level.random.nextDouble() < customChance;
    }
}