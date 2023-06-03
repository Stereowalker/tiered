package com.stereowalker.tiered.forge;

import static com.stereowalker.tiered.Tiered.ATTR_DATA;
import static com.stereowalker.tiered.Tiered.TIER_DATA;

import com.stereowalker.tiered.network.protocol.game.ClientboundAttributeSyncerPacket;
import com.stereowalker.tiered.network.protocol.game.ClientboundTierSyncerPacket;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class Events {
	public static void onPlayerLoggedIn(Player player){
        if(player.level.isClientSide) return;
        new ClientboundTierSyncerPacket(TIER_DATA.getTiers()).send(((ServerPlayer)player));
        new ClientboundAttributeSyncerPacket(ATTR_DATA.getTiers()).send(((ServerPlayer)player));
    }
}
