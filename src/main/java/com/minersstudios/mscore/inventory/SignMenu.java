package com.minersstudios.mscore.inventory;

import com.minersstudios.mscore.listener.impl.packet.player.PlayerUpdateSignListener;
import com.minersstudios.mscore.utility.LocationUtils;
import io.papermc.paper.adventure.AdventureComponent;
import net.kyori.adventure.text.Component;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundOpenSignEditorPacket;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiPredicate;

/**
 * A utility class for creating sign menus
 */
public final class SignMenu implements Cloneable {
    private final Component[] lines;
    private final BiPredicate<Player, String[]> response;
    private Location location;

    private static final Map<Player, SignMenu> SIGN_MENU_MAP = new ConcurrentHashMap<>();

    /**
     * Creates a new {@link SignMenu} instance with the given text. The response
     * handler is called when the player clicks the done button.
     * <br>
     * Returning true will close the sign editor.
     * <br>
     * Returning false will keep the sign editor open.
     *
     * @param first    The first line of the sign
     * @param second   The second line of the sign
     * @param third    The third line of the sign
     * @param fourth   The fourth line of the sign
     * @param response The response handler
     */
    public SignMenu(
            final @NotNull Component first,
            final @NotNull Component second,
            final @NotNull Component third,
            final @NotNull Component fourth,
            final @NotNull BiPredicate<Player, String[]> response
    ) {
        this.lines = new Component[] { first, second, third, fourth };
        this.response = response;
    }

    /**
     * @param player The player who has the sign menu open
     * @return SignMenu opened by the player, or null if the player does not
     *         have a SignMenu open
     */
    public static @Nullable SignMenu getSignMenu(final @NotNull Player player) {
        return SIGN_MENU_MAP.get(player);
    }

    /**
     * @return The lines of the sign
     */
    public Component @NotNull [] getLines() {
        return this.lines;
    }

    /**
     * Uses the packet listener to get the sign text
     *
     * @return The response of the sign
     * @see PlayerUpdateSignListener
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

    /**
     * Opens the sign for the player. It creates a fake sign, sends it to the
     * player, and then opens the sign editor.
     *
     * @param player The player
     */
    public void open(@NotNull Player player) {
        final SignMenu clone = this.clone();

        clone.location = player.getLocation().subtract(0.0d, 4.0d, 0.0d);

        final ServerGamePacketListenerImpl connection = ((CraftPlayer) player).getHandle().connection;
        final BlockPos blockPos = LocationUtils.bukkitToNms(clone.location);
        final BlockState blockState = Blocks.OAK_SIGN.defaultBlockState();
        final SignBlockEntity sign = new SignBlockEntity(blockPos, blockState);

        sign.setText(
                sign.getFrontText()
                .setMessage(0, new AdventureComponent(this.lines[0]))
                .setMessage(1, new AdventureComponent(this.lines[1]))
                .setMessage(2, new AdventureComponent(this.lines[2]))
                .setMessage(3, new AdventureComponent(this.lines[3])),
                true
        );

        connection.send(new ClientboundBlockUpdatePacket(blockPos, blockState));
        connection.send(ClientboundBlockEntityDataPacket.create(sign));
        connection.send(new ClientboundOpenSignEditorPacket(blockPos, true));

        SIGN_MENU_MAP.put(player, clone);
    }

    /**
     * Closes the sign for the player. It sends a block change packet to the
     * player to reset the sign.
     *
     * @param player The player to close the sign for
     */
    public void close(final @NotNull Player player) {
        final SignMenu menu = SIGN_MENU_MAP.remove(player);

        if (menu != null) {
            player.sendBlockChange(menu.location, menu.location.getBlock().getBlockData());
        }
    }

    /**
     * Creates and returns a clone of this sign menu
     *
     * @return A clone of this sign menu
     */
    @Override
    public @NotNull SignMenu clone() {
        try {
            return (SignMenu) super.clone();
        } catch (final CloneNotSupportedException e) {
            throw new AssertionError("An error occurred while cloning SignMenu", e);
        }
    }
}
