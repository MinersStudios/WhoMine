package com.minersstudios.mscore;

import com.minersstudios.mscore.config.Config;
import com.minersstudios.mscore.config.LanguageFile;
import com.minersstudios.mscore.logger.MSLogger;
import com.minersstudios.mscore.packet.ChannelHandler;
import com.minersstudios.mscore.plugin.MSPlugin;
import io.netty.channel.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerConnectionListener;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

/**
 * Main class of the plugin
 *
 * @see MSPlugin
 */
public final class MSCore extends MSPlugin {
    private static MSCore singleton;
    private static Config config;

    @Override
    public void enable() {
        singleton = this;
        config = new Config(this, this.getConfigFile());

        config.reload();
        LanguageFile.loadLanguage(config.languageUrl, config.languageCode);
        this.regPacketListeners();
    }

    @Override
    public void disable() {
        LanguageFile.unloadLanguage();
        this.unregPacketListeners();
    }

    /**
     * @return The instance of the plugin
     * @throws NullPointerException If the plugin is not enabled
     */
    public static @NotNull MSCore getInstance() throws NullPointerException {
        return singleton;
    }

    /**
     * @return The configuration of the plugin
     * @throws NullPointerException If the plugin is not enabled
     */
    public static @NotNull Config getConfiguration() throws NullPointerException {
        return config;
    }

    /**
     * Registers packet listeners for incoming connections.
     * Injects the ChannelHandler into the server networking pipeline.
     */
    private void regPacketListeners() {
        ServerConnectionListener connectionListener = MinecraftServer.getServer().getConnection();
        assert connectionListener != null;

        for (var connection : connectionListener.getConnections()) {
            ChannelPipeline pipeline = connection.channel.pipeline();

            var endInitProtocol = new ChannelInitializer<>() {

                @Override
                protected void initChannel(@NotNull Channel channel) {
                    try {
                        synchronized (connectionListener.getConnections()) {
                            channel.eventLoop().submit(() -> {
                                try {
                                    ChannelHandler channelHandler = new ChannelHandler(MSCore.this);

                                    channel.pipeline().addBefore(ChannelHandler.PACKET_HANDLER_NAME, ChannelHandler.CHANNEL_HANDLER_NAME, channelHandler);
                                } catch (Exception e) {
                                    MSLogger.log(Level.SEVERE, "Cannot inject incomming channel " + channel, e);
                                }
                            });
                        }
                    } catch (Exception e) {
                        MSLogger.log(Level.SEVERE, "Cannot inject incomming channel " + channel, e);
                    }
                }

            };
            var beginInitProtocol = new ChannelInitializer<>() {

                @Override
                protected void initChannel(@NotNull Channel channel) {
                    channel.pipeline().addLast(endInitProtocol);
                }
            };
            ChannelInboundHandlerAdapter packetHandler = new ChannelInboundHandlerAdapter() {

                @Override
                public void channelRead(
                        @NotNull ChannelHandlerContext ctx,
                        @NotNull Object msg
                ) {
                    Channel channel = (Channel) msg;

                    channel.pipeline().addFirst(beginInitProtocol);
                    ctx.fireChannelRead(msg);
                }
            };

            pipeline.addFirst(packetHandler);
        }
    }

    /**
     * Unregisters packet listeners for incoming connections.
     * Removes the ChannelHandler from the server networking pipeline.
     */
    private void unregPacketListeners() {
        ServerConnectionListener connectionListener = MinecraftServer.getServer().getConnection();

        if (connectionListener == null) return;

        for (var connection : connectionListener.getConnections()) {
            ChannelPipeline pipeline = connection.channel.pipeline();
            var registeredHandlers = pipeline.names();

            if (registeredHandlers.contains(ChannelHandler.CHANNEL_HANDLER_NAME)) {
                pipeline.remove(ChannelHandler.CHANNEL_HANDLER_NAME);
            }
        }
    }
}
