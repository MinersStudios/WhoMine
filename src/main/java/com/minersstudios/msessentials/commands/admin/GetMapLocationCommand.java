package com.minersstudios.msessentials.commands.admin;

import com.minersstudios.mscore.command.MSCommand;
import com.minersstudios.mscore.command.MSCommandExecutor;
import com.minersstudios.mscore.logger.MSLogger;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

@MSCommand(
        command = "getmaplocation",
        aliases = {"getmaploc"},
        usage = " ꀑ §cИспользуй: /<command>",
        description = "Добывает координаты карты, находящейся в руке",
        permission = "msessentials.maplocation",
        permissionDefault = PermissionDefault.OP,
        playerOnly = true
)
public class GetMapLocationCommand implements MSCommandExecutor {
    private static final CommandNode<?> COMMAND_NODE = LiteralArgumentBuilder.literal("getmaplocation").build();

    private static final TranslatableComponent NO_MAP_IN_RIGHT_HAND = translatable("ms.command.get_map_location.no_map_in_right_hand");
    private static final TranslatableComponent SOMETHING_WENT_WRONG = translatable("ms.error.something_went_wrong");
    private static final TranslatableComponent GET_MAP_LOCATION_FORMAT = translatable("ms.command.get_map_location.format");
    private static final TranslatableComponent COMMAND_BUTTON_TEXT = translatable("ms.command.get_map_location.command_button_text");

    @Override
    public boolean onCommand(
            final @NotNull CommandSender sender,
            final @NotNull Command command,
            final @NotNull String label,
            final String @NotNull ... args
    ) {
        final Player player = (Player) sender;

        if (!(player.getInventory().getItemInMainHand().getItemMeta() instanceof final MapMeta mapMeta)) {
            MSLogger.warning(player, NO_MAP_IN_RIGHT_HAND);
            return true;
        }

        final MapView mapView = mapMeta.getMapView();

        if (mapView == null || mapView.getWorld() == null) {
            MSLogger.severe(sender, SOMETHING_WENT_WRONG);
            return true;
        }

        final int x = mapView.getCenterX();
        final int z = mapView.getCenterZ();
        final int y = mapView.getWorld().getHighestBlockYAt(x, z) + 1;

        MSLogger.warning(
                player,
                GET_MAP_LOCATION_FORMAT.args(
                        text(mapView.getWorld().getName(), NamedTextColor.WHITE),
                        text(x, NamedTextColor.WHITE),
                        text(y, NamedTextColor.WHITE),
                        text(z, NamedTextColor.WHITE),
                        COMMAND_BUTTON_TEXT
                        .decorate(TextDecoration.BOLD)
                        .clickEvent(ClickEvent.runCommand("/tp " + x + " " + y + " " + z))
                )
        );
        return true;
    }

    @Override
    public @Nullable CommandNode<?> getCommandNode() {
        return COMMAND_NODE;
    }
}
