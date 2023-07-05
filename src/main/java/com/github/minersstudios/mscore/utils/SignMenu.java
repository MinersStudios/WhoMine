package com.github.minersstudios.mscore.utils;

import com.github.minersstudios.mscore.MSCore;
import com.google.common.collect.Lists;
import net.kyori.adventure.text.Component;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundOpenSignEditorPacket;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_20_R1.block.CraftSign;
import org.bukkit.craftbukkit.v1_20_R1.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collections;
import java.util.List;
import java.util.function.BiPredicate;

/**
 * A utility class for creating sign menus
 * <br><br>
 * @see #create(Component, Component, Component, Component, BiPredicate)
 */
public class SignMenu {
    private final List<Component> text;
    private BiPredicate<Player, String[]> response;
    private Location location;

    private SignMenu(@NotNull List<Component> text) {
        this.text = text;
    }

    /**
     * Creates a new {@link SignMenu} instance with the given text
     * <br>
     * The response handler is called when the player clicks the done button
     * <br><br>
     * Returning true will close the sign editor
     * <br>
     * Returning false will keep the sign editor open
     *
     * @param first    The first line of the sign
     * @param second   The second line of the sign
     * @param third    The third line of the sign
     * @param fourth   The fourth line of the sign
     * @param response The response handler
     * @return The SignMenu instance
     */
    @Contract("_, _, _, _, _ -> new")
    public static @NotNull SignMenu create(
            @NotNull Component first,
            @NotNull Component second,
            @NotNull Component third,
            @NotNull Component fourth,
            @NotNull BiPredicate<Player, String[]> response
    ) {
        SignMenu menu = new SignMenu(Lists.newArrayList(first, second, third, fourth));
        menu.response = response;
        return menu;
    }

    /**
     * Opens the sign for the player
     * <br>
     * It creates a fake sign, sends it to the player, and then opens the sign editor
     *
     * @param player The player
     */
    public void open(@NotNull Player player) {
        this.location = player.getLocation();
        this.location.setY(this.location.getY() - 4.0d);
        ServerGamePacketListenerImpl connection = ((CraftPlayer) player).getHandle().connection;
        BlockPos blockPos = new BlockPos(this.location.getBlockX(), this.location.getBlockY(), this.location.getBlockZ());
        net.minecraft.network.chat.Component[] components = CraftSign.sanitizeLines(this.text);
        SignBlockEntity sign = new SignBlockEntity(blockPos, Blocks.OAK_SIGN.defaultBlockState());

        sign.setText(
                sign.getFrontText()
                        .setMessage(0, components[0])
                        .setMessage(1, components[1])
                        .setMessage(2, components[2])
                        .setMessage(3, components[3]),
                true
        );
        connection.send(new ClientboundBlockUpdatePacket(blockPos, ((CraftBlockData) Material.OAK_SIGN.createBlockData()).getState()));
        connection.send(ClientboundBlockEntityDataPacket.create(sign));
        connection.send(new ClientboundOpenSignEditorPacket(blockPos, true));

        MSCore.getCache().signMenuMap.put(player, this);
    }

    /**
     * Closes the sign for the player
     * <br>
     * It sends a block change packet to the player to reset the sign
     *
     * @param player The player
     */
    public void close(@NotNull Player player) {
        MSCore.getCache().signMenuMap.remove(player);
        MSCore.getInstance().runTask(() -> {
            if (player.isOnline()) {
                player.sendBlockChange(this.location, this.location.getBlock().getBlockData());
            }
        });
    }

    /**
     * @return The text of the sign
     */
    public @NotNull @UnmodifiableView List<Component> getText() {
        return Collections.unmodifiableList(this.text);
    }

    /**
     * Uses ProtocolLib to get the response of the sign
     *
     * @return The response of the sign
     */
    public @NotNull BiPredicate<Player, String[]> getResponse() {
        return this.response;
    }

    /**
     * @return The location of the sign
     */
    public @NotNull Location getLocation() {
        return this.location;
    }
}
