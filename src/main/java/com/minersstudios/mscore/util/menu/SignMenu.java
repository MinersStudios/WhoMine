package com.minersstudios.mscore.util.menu;

import com.minersstudios.mscore.listeners.packet.player.PlayerUpdateSignListener;
import io.papermc.paper.adventure.PaperAdventure;
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
import org.bukkit.craftbukkit.v1_20_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R2.util.CraftLocation;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiPredicate;

/**
 * A utility class for creating sign menus
 *
 * @see #create(Component, Component, Component, Component, BiPredicate)
 */
public final class SignMenu {
    private final Component[] lines;
    private BiPredicate<Player, String[]> response;
    private Location location;

    private static final Map<Player, SignMenu> SIGN_MENU_MAP = new HashMap<>();

    private SignMenu(final Component @NotNull [] lines) {
        this.lines = lines;
    }

    /**
     * Creates a new {@link SignMenu} instance with the given text.
     * The response handler is called when the player clicks the done button.
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
     * @return The SignMenu instance
     */
    @Contract("_, _, _, _, _ -> new")
    public static @NotNull SignMenu create(
            final @NotNull Component first,
            final @NotNull Component second,
            final @NotNull Component third,
            final @NotNull Component fourth,
            final @NotNull BiPredicate<Player, String[]> response
    ) {
        final SignMenu menu = new SignMenu(new Component[] { first, second, third, fourth });
        menu.response = response;
        return menu;
    }

    /**
     * @param player The player who has the sign menu open
     * @return SignMenu opened by the player,
     *         or null if the player does not have a SignMenu open
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
     * Opens the sign for the player. It creates
     * a fake sign, sends it to the player, and
     * then opens the sign editor.
     *
     * @param player The player
     */
    public void open(@NotNull Player player) {
        this.location = player.getLocation();
        this.location.setY(this.location.getY() - 4.0d);

        final ServerGamePacketListenerImpl connection = ((CraftPlayer) player).getHandle().connection;
        final BlockPos blockPos = CraftLocation.toBlockPosition(this.location);
        final BlockState blockState = Blocks.OAK_SIGN.defaultBlockState();
        final SignBlockEntity sign = new SignBlockEntity(blockPos, blockState);
        final net.minecraft.network.chat.Component[] components = new net.minecraft.network.chat.Component[4];

        for (int i = 0; i < 4; i++) {
            if (
                    i < this.lines.length
                    && this.lines[i] != null
            ) {
                components[i] = PaperAdventure.asVanilla(this.lines[i]);
            } else {
                components[i] = net.minecraft.network.chat.Component.literal("");
            }
        }

        sign.setText(
                sign.getFrontText()
                .setMessage(0, components[0])
                .setMessage(1, components[1])
                .setMessage(2, components[2])
                .setMessage(3, components[3]),
                true
        );
        connection.send(new ClientboundBlockUpdatePacket(blockPos, blockState));
        connection.send(ClientboundBlockEntityDataPacket.create(sign));
        connection.send(new ClientboundOpenSignEditorPacket(blockPos, true));

        SIGN_MENU_MAP.put(player, this);
    }

    /**
     * Closes the sign for the player. It sends
     * a block change packet to the player to
     * reset the sign.
     *
     * @param player The player to close the sign for
     */
    public void close(final @NotNull Player player) {
        if (SIGN_MENU_MAP.remove(player) != null) {
            player.sendBlockChange(this.location, this.location.getBlock().getBlockData());
        }
    }
}
