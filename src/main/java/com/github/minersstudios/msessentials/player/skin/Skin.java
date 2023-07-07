package com.github.minersstudios.msessentials.player.skin;

import com.destroystokyo.paper.profile.CraftPlayerProfile;
import com.github.minersstudios.mscore.utils.ChatUtils;
import com.github.minersstudios.msessentials.player.PlayerFile;
import com.google.common.base.Preconditions;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.RegEx;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Skin class to create skins from values and signatures or image links.
 * Used in {@link PlayerFile} to store the skin of a player.
 *
 * @see <a href="https://wiki.vg/Mojang_API#UUID_-.3E_Profile_.2B_Skin.2FCape">Mojang API</a>
 */
public class Skin {
    private final String name;
    private final String value;
    private final String signature;

    private static final @RegEx String NAME_REGEX = "[a-zA-ZЀ-ӿ-0-9]{1,32}";
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadExecutor(Thread::new);

    private Skin(
            @NotNull String name,
            @NotNull String value,
            @NotNull String signature
    ) {
        this.name = name;
        this.value = value;
        this.signature = signature;
    }

    /**
     * Creates a skin from a value and signature.
     *
     * @param name      The name of the skin
     * @param value     The value of the skin (base64)
     * @param signature The signature of the skin (base64)
     * @return The skin
     * @throws IllegalArgumentException If the name, value, or signature is invalid
     * @see <a href="https://wiki.vg/Mojang_API#UUID_-.3E_Profile_.2B_Skin.2FCape">Mojang API</a>
     */
    @Contract(value = "_, _, _ -> new")
    public static @NotNull Skin create(
            @NotNull String name,
            @NotNull String value,
            @NotNull String signature
    ) throws IllegalArgumentException {
        Preconditions.checkArgument(matchesNameRegex(name), "The name must be between 1 and 32 characters long and only contain letters, numbers, and underscores");
        Preconditions.checkArgument(value.startsWith("ewog"), "The value must start with ewog");
        return new Skin(name, value, signature);
    }

    /**
     * Creates a skin from an image link.
     * It will attempt to retrieve the skin 3 times before giving up.
     * The value and signature generates with the MineSkinAPI.
     *
     * @param name The name of the skin
     * @param link The link to the skin, must start with https:// and end with .png
     * @return The skin if it was successfully retrieved, otherwise null
     * @throws IllegalArgumentException If the name or link is invalid
     * @see <a href="https://wiki.vg/Mojang_API#UUID_-.3E_Profile_.2B_Skin.2FCape">Mojang API</a>
     */
    public static @Nullable Skin create(
            @NotNull String name,
            @NotNull String link
    ) throws IllegalArgumentException {
        Preconditions.checkArgument(matchesNameRegex(name), "The name must be between 1 and 32 characters long and only contain letters, numbers, and underscores");
        Preconditions.checkArgument(link.startsWith("https://") || link.endsWith(".png"), "The link must start with https:// and end with .png");

        AtomicInteger retryAttempts = new AtomicInteger(0);

        do {
            CompletableFuture<Skin> future = CompletableFuture.supplyAsync(() -> handleLink(name, link), EXECUTOR_SERVICE);

            try {
                Skin skin = future.get();
                if (skin != null) return skin;
            } catch (InterruptedException | ExecutionException e) {
                Bukkit.getLogger().log(Level.SEVERE, "An error occurred while attempting to retrieve a skin from a link", e);
            }

            retryAttempts.incrementAndGet();
        } while (retryAttempts.get() < 3);

        return null;
    }

    /**
     * @return The name of the skin
     */
    public @NotNull String getName() {
        return this.name;
    }

    /**
     * @return The value of the skin (base64)
     */
    public @NotNull String getValue() {
        return this.value;
    }

    /**
     * @return The signature of the skin (base64)
     */
    public @NotNull String getSignature() {
        return this.signature;
    }

    /**
     * @return The head of the skin as an {@link ItemStack}
     */
    public @NotNull ItemStack getHead() {
        ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);

        profile.getProperties().put("textures", new Property("textures", this.value, this.signature));
        skullMeta.setPlayerProfile(CraftPlayerProfile.asBukkitCopy(profile));
        skullMeta.displayName(ChatUtils.createDefaultStyledText(this.name));
        itemStack.setItemMeta(skullMeta);

        return itemStack;
    }

    /**
     * @param skin Skin to be checked
     * @return True if the skin's name, value, and signature are equal
     */
    public boolean equals(@NotNull Skin skin) {
        return this.name.equalsIgnoreCase(skin.getName())
                && this.value.equals(skin.getValue())
                && this.signature.equals(skin.getSignature());
    }

    /**
     * @param string String to be checked
     * @return True if string matches {@link #NAME_REGEX}
     */
    @Contract(value = "null -> false", pure = true)
    public static boolean matchesNameRegex(@Nullable String string) {
        return string != null && string.matches(NAME_REGEX);
    }

    /**
     * This method will attempt to retrieve the skin 3 times before giving up.
     * The value and signature generates with the MineSkinAPI.
     * If response status code is 200, the skin will be returned.
     *
     * @param name Name of the skin
     * @param link Link to the skin
     * @return The skin if it was successfully retrieved, otherwise null
     */
    private static @Nullable Skin handleLink(
            String name,
            String link
    ) {
        Logger logger = Bukkit.getLogger();
        MineSkinResponse response;

        for (int i = 0; true; i++) {
            try {
                response = MineSkinResponse.fromLink(link);
                break;
            } catch (IOException e) {
                if (i >= 2) return null;
            }
        }

        switch (response.getStatusCode()) {
            case 200 -> {
                MineSkinJson json = response.getBodyResponse(MineSkinJson.class);
                MineSkinJson.Data.Texture texture = json.data().texture();

                return Skin.create(name, texture.value(), texture.signature());
            }
            case 500, 400 -> {
                MineSkinErrorJson errorJson = response.getBodyResponse(MineSkinErrorJson.class);
                String errorCode = errorJson.errorCode();

                switch (errorCode) {
                    case "failed_to_create_id", "skin_change_failed" -> {
                        try {
                            TimeUnit.SECONDS.sleep(5);
                        } catch (InterruptedException e) {
                            logger.log(Level.SEVERE, "Failed to sleep thread", e);
                        }
                    }
                    case "no_account_available" -> logger.severe("No account available to create skin");
                    default -> logger.severe("Unknown MineSkin error: " + errorCode);
                }
            }
            case 403 -> {
                MineSkinErrorJson errorJson = response.getBodyResponse(MineSkinErrorJson.class);
                String errorCode = errorJson.errorCode();
                String error = errorJson.error();

                if (errorCode.equals("invalid_api_key")) {
                    logger.severe("Api key is not invalid! Reason: " + error);

                    switch (error) {
                        case "Invalid API Key" ->
                                logger.severe("This api key is not registered on MineSkin!");
                        case "Client not allowed" ->
                                logger.severe("This server ip is not on the api key allowed ips list!");
                        case "Origin not allowed" ->
                                logger.severe("This server origin is not on the api key allowed origin list!");
                        case "Agent not allowed" ->
                                logger.severe("This server agent is not on the api key allowed agents list!");
                        default -> logger.severe("Unknown error :" + error);
                    }
                }
            }
            case 429 -> {
                MineSkinDelayErrorJson delayErrorJson = response.getBodyResponse(MineSkinDelayErrorJson.class);
                Integer delay = delayErrorJson.delay();
                Integer nextRequest = delayErrorJson.nextRequest();
                int sleepDuration = 2;

                if (delay != null) {
                    sleepDuration = delay;
                } else if (nextRequest != null) {
                    Instant nextRequestInstant = Instant.ofEpochSecond(nextRequest);
                    int duration = (int) Duration.between(Instant.now(), nextRequestInstant).getSeconds();

                    if (duration > 0) {
                        sleepDuration = duration;
                    }
                }

                try {
                    TimeUnit.SECONDS.sleep(sleepDuration);
                } catch (InterruptedException e) {
                    logger.log(Level.SEVERE, "Failed to sleep thread", e);
                }
            }
            default -> Bukkit.getLogger().log(Level.SEVERE, "Unknown MineSkin error: " + response.getStatusCode());
        }

        return null;
    }
}
