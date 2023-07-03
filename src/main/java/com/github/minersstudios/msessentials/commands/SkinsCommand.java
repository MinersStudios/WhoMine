package com.github.minersstudios.msessentials.commands;

import com.github.minersstudios.mscore.command.MSCommand;
import com.github.minersstudios.mscore.command.MSCommandExecutor;
import com.github.minersstudios.mscore.utils.ChatUtils;
import com.github.minersstudios.mscore.utils.PlayerUtils;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;

@MSCommand(
        command = "skins",
        aliases = {"skins"},
        usage = " ꀑ §cИспользуй: /<command>",
        description = "Открывает меню с крафтами кастомных предметов/декора/блоков"
)
public class SkinsCommand implements MSCommandExecutor {

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            String @NotNull ... args
    ) {
        if (!(sender instanceof Player player)) {
            ChatUtils.sendError(sender, Component.translatable("ms.error.only_player_command"));
            return true;
        }

        switch (args[0]) {
            case "0" -> PlayerUtils.setSkin(
                    player,
                    "ewogICJ0aW1lc3RhbXAiIDogMTY4ODQxNzA1OTI2NiwKICAicHJvZmlsZUlkIiA6ICIzOTdlMmY5OTAyNmI0NjI1OTcyNTM1OTNjODgyZjRmMyIsCiAgInByb2ZpbGVOYW1lIiA6ICJ4WnlkdWVMeCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS85OTQ0M2NmN2ZiZjYwNjQ3YjY3YWFhMzg3ZjA2MTBmMDVmYTM4NDJiMzI2YjIyNzIwZmQwMmI0MzQxNDg2YmEzIiwKICAgICAgIm1ldGFkYXRhIiA6IHsKICAgICAgICAibW9kZWwiIDogInNsaW0iCiAgICAgIH0KICAgIH0KICB9Cn0=",
                    "b3r8n/Za6cKia3V2mPMlzZ53jTs+imA4YtWHMM+TdLFtW2nL1wfyJYxw/W8ZQLuOg5oBsxsXymsRFlTU61feZ3mVgvoR5jpiumUk63/wJ3JWD2sDciFlHa4M7glJQE9eCHHW1o1X3YqAGy5ViBdWQpP9ahsy3JeyLcy5y6Vswdkpp3gYrZq78Sxo60KrDp27OxJn7thd6u82Bwf7M1TjJ2M51wHRK+/yXIQPDP++GJ0v79GsxBnrD1pQofI1X1PGd0kdFb6ANHa5eoMFHhyd1Jn9nBmHSfaE2PzgvfXyBiBoL6oRJUYGwFL8SqExDAKQ6xpW2So908U1A/R7IAyZrddDJFZEWUYgn3Ta96+6vZHyOJkfgpiZwzlSMXM24tByuswz20qrPxd4hfZvRU+aryAXgTqLyfiFz96pt7+3wBdMgwA+59t1gGT+HMMSZR4o7KhLw7GIotB8Phk/38od6eTa7cxXOVmrAJN1xSfyK3TihF4fhXHv7uZ39xxR4Ya+CKgf/I/TQHfmXgpaVTyvsEj1E1w6MFKE0BJGhjBrbFSdWENcPP3fcmOtW/i8DyT8HAiM/ICu61G5NidM2Aws6cnjnexb8fxrATtohXSmEBMkH0gXMsVgAT6lU5ZAnZV1/nK2sBUH5Q9FimXuW39Y1gzJjK1ida5yPmAW/GQBqAE="
            );
            case "1" -> PlayerUtils.setSkin(
                    player,
                    "ewogICJ0aW1lc3RhbXAiIDogMTY4ODQxNDk0MDY2NSwKICAicHJvZmlsZUlkIiA6ICI2NTc3OGE5YWUzYTE0MTI5ODVlN2RjNTdhMzc3NTE1YyIsCiAgInByb2ZpbGVOYW1lIiA6ICJNYXJ0b3BoIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzVjNTY5MmUwZTUzNTRiM2VmYjUwYWM3MTc0NmY0OWVlMDljZTVhNWQ0NmJhMWE3ZmI1YmE1MWYyOTNhYmNiMjgiCiAgICB9CiAgfQp9",
                    "Sc6a2mHAgBn08CoOZOo7oJOEoGYPNrlhWbP1oPTGWFfwsIu567g65a/gWImXBc49H5+yHfiqVyq6qpmIEHXQg2qx3zskiDMV5KbcSsCkG2T4P4tFtYoDOPN+dv3dgxHuviSclV1xRNlvIjPOJ49BZ9EeWNDR8DaYlGH34ZUQCD5V5buRwmQkia3Y5X3AdOxgZLSrsqIQQdsBHkJMstzfY3Lj7LkuGIqhjMO0Akk+oR//Bd9oiVDQlHTGwJ9RFm1pFHuR+QNAj6/zp7APIawNHU9QzVU3wqmA8BZM2BaYyawJJKV9MNDTzhjzKg+OnZC3hcl/THAz9olCVcvUhcyaKr87Yny0AWJUOSQ1f3i/bbIJOo/+NCBodoc7/QYRUtOojCOnTgTWGqFdM15yESxQ7fZmRmr9+13R9oCs7zgBEoK7ybtuDfdp7sfPRyWG2IXcbi3AdU/htbumzm6Eh8Hv2i3HPi1DhUTY96aSjtFuV3JxjXIL+Ax9vBsl9V+6sfgsUwOw1blkJg0DUGEuGhtjXmnGnPMnpEIrojrLukQi1M8fx7B55KCdSdX+6JGM8juiP6xsMm3kmZXvonCVH3H7VAlEmwkvzYpbU19bnk0+9e+um6NWygHd0DBoIB+VqyGLL4H8Mx7gNcr0qU+EUe0AZIms/hviPtuP+2u1vA9x3ow="
            );
            case "2" -> PlayerUtils.setSkin(
                    player,
                    "ewogICJ0aW1lc3RhbXAiIDogMTY4ODQxNTA2MTc5OSwKICAicHJvZmlsZUlkIiA6ICI4OGE2Nzg4NDEzZDM0YjM0ODNlOGI3ZmQ0NmViYmI1MCIsCiAgInByb2ZpbGVOYW1lIiA6ICJQb2xvc2t1bl8iLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTMyYzY2YWIwODg0ZDg0NDZlZDBkNzU5ZTA2MmRjYjcwZGMxMjQyODIyY2Q4ZTY2ZTU0MTVmNjNjMjRjOTEzZSIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9LAogICAgIkNBUEUiIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzIzNDBjMGUwM2RkMjRhMTFiMTVhOGIzM2MyYTdlOWUzMmFiYjIwNTFiMjQ4MWQwYmE3ZGVmZDYzNWNhN2E5MzMiCiAgICB9CiAgfQp9",
                    "LIp+6/E2lg7o2z8tozshe5ol7jv7rA7SSxVuTxbk5z29ZQbI6LoVT4rY12YcukosjjR7yzTeMb+wzIoVZogw0CoZKdqCIxjDrepjxPAFEgpV+vcAIysRiAhY3vMR5ueQGzcsxtZbbDOp3BDuCjWOERrcWMoPnwB8v0eiET1GbqjoPceRf4LUuYD0a0F3W44RwzxYMMeIfWBSPkQjClQm44sGCc7NQnmIszvFsvS5qDpLzhVt8QIyV25YGHhLOg+qBaiY6RssG7yTTgcbx7HvxtMFPNoXJGaetSe8vRON0VVPxqYfk/Txu73t/cDVHo0sDMWYjgpo8IznMHApHSYWINcLSVKnAiisVULGbwkN8FLKSyiWals+wM8XSCPN1Ow0+nA+XvcRDwjWqsu8LKs+Sf7mhyiVFjuZG3kA2F7Qo923yR7BE5vCjrXUhZzugRfjE671lKhRb1KmtADddqZBaAwcu4KPsaO5OJ4d2+8zMSPiAD51i8m4DFs3hiEfL1W4SAbyeNOSbxd9ej9YtE3nFkoyagFlN4EGtluoeyebiFgbOnwfTJ/E2/3bFQtQ1X2TBeieikZpU38utTB9E6X2lIJ/zeBwXdknxNJrqNQykQXMpAWBHTHdyfDxWmxR/lmES7s5P+5erz4YrVOrrDFjl6fP2xDLiY4CyfZGRUN+rws="
            );
        }
        PlayerUtils.reloadPlayer(player);
        return true;
    }

    @Override
    public @Nullable CommandNode<?> getCommandNode() {
        return LiteralArgumentBuilder.literal("skins")
                .then(
                        argument("skin", IntegerArgumentType.integer())
                ).build();
    }
}
