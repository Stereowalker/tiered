package draylar.tiered.forge;

import draylar.tiered.network.AttributeSyncer;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.NetworkDirection;

import static draylar.tiered.Tiered.HANDLER;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.FORGE, modid = "tiered")
public class ForgeEvents {
    @SubscribeEvent
    public static void onPlayerLoggedIn(final PlayerEvent.PlayerLoggedInEvent par0){
        if(par0.getPlayer().world.isRemote) return;
        HANDLER.sendTo(new AttributeSyncer(), ((ServerPlayerEntity)par0.getPlayer()).connection.getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT);
    }
}
