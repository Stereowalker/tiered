package draylar.tiered.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import draylar.tiered.api.CustomEntityAttributes;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {

    private PlayerEntityMixin(EntityType<? extends LivingEntity> type, World world) {
        super(type, world);
    }

    @Inject(
            method = "func_234570_el_",
            at = @At("RETURN")
    )
    private static void initAttributes(CallbackInfoReturnable<AttributeModifierMap.MutableAttribute> ci) {
        ci.getReturnValue().createMutableAttribute(CustomEntityAttributes.CRIT_CHANCE);
    }

    @ModifyVariable(
            method = "attackTargetEntityWithCurrentItem",
            at = @At(
                    value = "JUMP",
                    ordinal = 2
            ),
            slice = @Slice(
                    from = @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/entity/player/PlayerEntity;isSprinting()Z",
                            ordinal = 1
                    )
            ),
            index = 8
    )
    private boolean attack(boolean bl3) {
        float customChance = 0;

        ModifiableAttributeInstance instance = this.getAttribute(CustomEntityAttributes.CRIT_CHANCE);

        for (AttributeModifier modifier : instance.getModifierListCopy()) {
            float amount = (float) modifier.getAmount();
            customChance += amount;
        }

        return bl3 || world.rand.nextDouble() < customChance;
    }
}