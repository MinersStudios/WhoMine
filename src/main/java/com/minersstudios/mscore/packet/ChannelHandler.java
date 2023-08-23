package com.minersstudios.mscore.packet;

import com.minersstudios.mscore.logger.MSLogger;
import com.minersstudios.mscore.plugin.MSPlugin;
import io.netty.channel.*;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * The ChannelHandler class is responsible for handling
 * incoming and outgoing packets in the Minecraft server
 * networking pipeline. It extends `ChannelDuplexHandler`,
 * which allows handling of both inbound and outbound data.
 *
 * @see PacketType
 * @see PacketContainer
 * @see PacketEvent
 */
public class ChannelHandler extends ChannelDuplexHandler {
    private final MSPlugin plugin;
    private final Player player;

    public static final String CHANNEL_HANDLER_NAME = "ms_channel_handler";
    public static final String PACKET_HANDLER_NAME = "packet_handler";

    /**
     * Channel handler constructor
     *
     * @param plugin The plugin associated with this channel handler
     * @param player The player associated with this channel handler
     *               (can be null)
     */
    public ChannelHandler(
            final @NotNull MSPlugin plugin,
            final @NotNull Player player
    ) {
        this.plugin = plugin;
        this.player = player;
    }

    /**
     * @return The plugin associated with this channel handler
     */
    public @NotNull MSPlugin getPlugin() {
        return this.plugin;
    }

    /**
     * @return The player associated with this channel handler
     */
    public @NotNull Player getPlayer() {
        return this.player;
    }

    /**
     * This method is called when a packet is received from the client.
     * It processes the packet, creates a {@link PacketContainer}, and
     * fires a {@link PacketEvent}. If the event is not cancelled, the
     * packet is passed to the next channel handler in the pipeline.
     *
     * @param ctx The ChannelHandlerContext
     * @param msg The received packet
     * @throws Exception If an error occurs while processing the packet
     */
    @Override
    public void channelRead(
            final @NotNull ChannelHandlerContext ctx,
            final @NotNull Object msg
    ) throws Exception {
        if (!(msg instanceof final Packet<?> packet)) return;

        final PacketType packetType = PacketType.fromClass(packet.getClass());

        if (packetType == null) {
            this.plugin.runTask(() -> this.player.kick());
            MSLogger.severe("Unknown packet type: " + packet.getClass().getName() + " sent by " + this.player.getName());
            return;
        }

        final PacketContainer packetContainer = new PacketContainer(packet, packetType);
        final PacketEvent event = new PacketEvent(packetContainer, this.player);

        this.plugin.callPacketReceiveEvent(event);

        if (!event.isCancelled()) {
            super.channelRead(ctx, event.getPacketContainer().getPacket());
        }
    }

    /**
     * This method is called when a packet is about to be sent to the client.
     * It processes the packet, creates a {@link PacketContainer}, and fires
     * a {@link PacketEvent}. If the event is not cancelled, the packet is
     * passed to the next channel handler in the pipeline.
     *
     * @param ctx     The ChannelHandlerContext
     * @param msg     The packet to be sent
     * @param promise The ChannelPromise
     * @throws Exception If an error occurs while processing the packet
     */
    @Override
    public void write(
            final @NotNull ChannelHandlerContext ctx,
            final @NotNull Object msg,
            final @NotNull ChannelPromise promise
    ) throws Exception {
        if (!(msg instanceof final Packet<?> packet)) return;

        final PacketType packetType = PacketType.fromClass(packet.getClass());

        if (packetType == null) {
            this.plugin.runTask(() -> this.player.kick());
            MSLogger.severe("Unknown packet type: " + packet.getClass().getName() + " sent to " + this.player.getName());
            return;
        }

        final PacketContainer packetContainer = new PacketContainer(packet, packetType);
        final PacketEvent event = new PacketEvent(packetContainer, this.player);

        this.plugin.callPacketSendEvent(event);

        if (!event.isCancelled()) {
            super.write(ctx, event.getPacketContainer().getPacket(), promise);
        }
    }

    /**
     * Injects the {@link ChannelHandler} for a specific player into
     * the server networking pipeline
     *
     * @param player The player to inject the ChannelHandler for
     * @param plugin The MSPlugin instance associated with the ChannelHandler
     */
    public static void injectPlayer(
            final @NotNull Player player,
            final @NotNull MSPlugin plugin
    ) {
        final ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
        final ChannelPipeline pipeline = serverPlayer.connection.connection.channel.pipeline();

        if (!pipeline.names().contains(ChannelHandler.CHANNEL_HANDLER_NAME)) {
            final ChannelHandler channelHandler = new ChannelHandler(plugin, player);
            pipeline.addBefore(ChannelHandler.PACKET_HANDLER_NAME, ChannelHandler.CHANNEL_HANDLER_NAME, channelHandler);
        }
    }

    /**
     * Removes the {@link ChannelHandler} from a specific player
     * in the server networking pipeline
     *
     * @param player The player to remove the ChannelHandler for
     */
    public static void uninjectPlayer(final @NotNull Player player) {
        final ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
        final Channel channel = serverPlayer.connection.connection.channel;
        final ChannelPipeline pipeline = channel.pipeline();

        if (pipeline.names().contains(CHANNEL_HANDLER_NAME)) {
            channel.eventLoop().execute(() -> pipeline.remove(CHANNEL_HANDLER_NAME));
        }
    }
}
