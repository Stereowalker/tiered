package com.stereowalker.tiered.forge;

import static com.stereowalker.tiered.Tiered.ATTRIBUTE_DATA_LOADER;

import com.stereowalker.tiered.network.protocol.game.ClientboundAttributeSyncerPacket;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.FORGE, modid = "tiered")
public class ForgeEvents {
    @SubscribeEvent
    public static void onPlayerLoggedIn(final PlayerEvent.PlayerLoggedInEvent par0){
        if(par0.getPlayer().level.isClientSide) return;
        new ClientboundAttributeSyncerPacket(ATTRIBUTE_DATA_LOADER.getItemAttributes()).send(((ServerPlayer)par0.getPlayer()));
    }
}
