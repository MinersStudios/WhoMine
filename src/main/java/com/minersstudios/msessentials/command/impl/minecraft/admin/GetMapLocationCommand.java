package com.minersstudios.msessentials.command.impl.minecraft.admin;

import com.minersstudios.mscore.command.api.Command;
import com.minersstudios.mscore.command.api.CommandExecutor;
import com.minersstudios.mscore.plugin.MSLogger;
import com.minersstudios.mscore.utility.Font;
import com.minersstudios.msessentials.MSEssentials;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.minersstudios.mscore.language.LanguageRegistry.Components.*;
import static net.kyori.adventure.text.Component.text;

@Command(
        command = "getmaplocation",
        aliases = {"getmaploc"},
        usage = " " + Font.Chars.RED_EXCLAMATION_MARK + " §cИспользуй: /<command>",
        description = "Добывает координаты карты, находящейся в руке",
        permission = "msessentials.maplocation",
        permissionDefault = PermissionDefault.OP,
        playerOnly = true
)
public final class GetMapLocationCommand extends CommandExecutor<MSEssentials> {
    private static final CommandNode<?> COMMAND_NODE = LiteralArgumentBuilder.literal("getmaplocation").build();

    @Override
    public boolean onCommand(
            final @NotNull CommandSender sender,
            final @NotNull org.bukkit.command.Command command,
            final @NotNull String label,
            final String @NotNull ... args
    ) {
        final Player player = (Player) sender;

        if (!(player.getInventory().getItemInMainHand().getItemMeta() instanceof final MapMeta mapMeta)) {
            MSLogger.warning(
                    player,
                    COMMAND_GET_MAP_LOCATION_NO_MAP_IN_RIGHT_HAND
            );
            return true;
        }

        final MapView mapView = mapMeta.getMapView();

        if (
                mapView == null
                || mapView.getWorld() == null
        ) {
            MSLogger.severe(
                    sender,
                    ERROR_SOMETHING_WENT_WRONG
            );
            return true;
        }

        final int x = mapView.getCenterX();
        final int z = mapView.getCenterZ();
        final int y = mapView.getWorld().getHighestBlockYAt(x, z) + 1;

        MSLogger.warning(
                player,
                COMMAND_GET_MAP_LOCATION_FORMAT
                .args(
                        text(mapView.getWorld().getName(), NamedTextColor.WHITE),
                        text(x, NamedTextColor.WHITE),
                        text(y, NamedTextColor.WHITE),
                        text(z, NamedTextColor.WHITE),
                        COMMAND_GET_MAP_LOCATION_COMMAND_BUTTON_TEXT
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
