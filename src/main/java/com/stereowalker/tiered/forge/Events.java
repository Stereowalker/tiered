package com.stereowalker.tiered.forge;

import static com.stereowalker.tiered.Tiered.ATTRIBUTE_DATA_LOADER;

import com.stereowalker.tiered.network.protocol.game.ClientboundAttributeSyncerPacket;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class Events {
	public static void onPlayerLoggedIn(Player player){
        if(player.level.isClientSide) return;
        new ClientboundAttributeSyncerPacket(ATTRIBUTE_DATA_LOADER.getItemAttributes()).send(((ServerPlayer)player));
    }
}
