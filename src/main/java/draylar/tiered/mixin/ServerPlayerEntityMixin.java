package draylar.tiered.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.authlib.GameProfile;

import draylar.tiered.Tiered;
import draylar.tiered.api.ModifierUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {

    private NonNullList<ItemStack> mainCopy = null;

    private ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile profile) {
        super(world, pos, yaw, profile);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        // if mainInventory copy is null, set it to player inventory and check each stack
        if(mainCopy == null) {
            mainCopy = copyDefaultedList(inventory.mainInventory);
            runCheck();
        }

        // if mainInventory copy =/= inventory, run check and set mainCopy to inventory
        if (!inventory.mainInventory.equals(mainCopy)) {
            mainCopy = copyDefaultedList(inventory.mainInventory);
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
        inventory.mainInventory.forEach(itemStack -> {
            // no tier on item
            if(itemStack.getChildTag(Tiered.NBT_SUBTAG_KEY) == null) {
                // attempt to get a random tier
                ResourceLocation potentialAttributeID = ModifierUtils.getRandomAttributeIDFor(itemStack.getItem());

                // found an ID
                if(potentialAttributeID != null) {
                    itemStack.getOrCreateChildTag(Tiered.NBT_SUBTAG_KEY).putString(Tiered.NBT_SUBTAG_DATA_KEY, potentialAttributeID.toString());
                }
            }
        });
    }
}
