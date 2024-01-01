package com.minersstudios.mscustoms.listener.packet.player;

import com.minersstudios.mscore.listener.api.packet.AbstractPacketListener;
import com.minersstudios.mscore.listener.api.packet.PacketListener;
import com.minersstudios.mscore.packet.PacketEvent;
import com.minersstudios.mscore.packet.PacketType;
import com.minersstudios.mscustoms.MSCustoms;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@PacketListener
public final class SwingArmListener extends AbstractPacketListener<MSCustoms> {
    private Map<String, CompletableFuture<Block>> clickRequestMap;

    public SwingArmListener() {
        super(PacketType.Play.Server.SWING_ARM);
    }

    @Override
    public void onPacketReceive(final @NotNull PacketEvent event) {
        if (this.clickRequestMap == null) {
            this.setupClickRequestMap();
        }

        if (this.clickRequestMap.isEmpty()) {
            return;
        }

        final ServerPlayer serverPlayer = event.getConnection().getPlayer();

        if (serverPlayer.gameMode.getGameModeForPlayer() == GameType.SURVIVAL) {
            final String uuid = serverPlayer.getStringUUID();
            final var future = this.clickRequestMap.get(uuid);

            if (future == null) {
                return;
            }

            this.getPlugin().runTask(() -> {
                final Block targetBlock = PlayerActionListener.getTargetBlock(serverPlayer);

                if (targetBlock != null) {
                    future.complete(targetBlock);
                    this.clickRequestMap.remove(uuid);
                }
            });
        }
    }

    private void setupClickRequestMap() {
        for (final var listener : this.getPlugin().getPacketListeners()) {
            if (listener instanceof final PlayerActionListener playerActionListener) {
                this.clickRequestMap = playerActionListener.clickRequestMap;
                break;
            }
        }
    }
}
