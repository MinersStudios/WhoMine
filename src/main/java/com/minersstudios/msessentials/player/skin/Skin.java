package com.minersstudios.msessentials.player.skin;

import com.destroystokyo.paper.profile.CraftPlayerProfile;
import com.minersstudios.mscore.logger.MSLogger;
import com.minersstudios.mscore.util.ChatUtils;
import com.minersstudios.msessentials.player.PlayerFile;
import com.google.common.base.Preconditions;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.RegEx;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        Preconditions.checkArgument(isValidBase64(value), "The value must be a valid Base64 string");
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
     * @throws IllegalArgumentException If the name or link is invalid or the image is not 64x64 pixels
     * @see <a href="https://wiki.vg/Mojang_API#UUID_-.3E_Profile_.2B_Skin.2FCape">Mojang API</a>
     */
    public static @Nullable Skin create(
            @NotNull String name,
            @NotNull String link
    ) throws IllegalArgumentException {
        Preconditions.checkArgument(matchesNameRegex(name), "The name must be between 1 and 32 characters long and only contain letters, numbers, and underscores");
        Preconditions.checkArgument(isValidSkinImg(link), "The link must start with https:// and end with .png and the image must be 64x64 pixels");

        AtomicInteger retryAttempts = new AtomicInteger(0);

        do {
            CompletableFuture<Skin> future = CompletableFuture.supplyAsync(() -> handleLink(name, link), EXECUTOR_SERVICE);

            try {
                Skin skin = future.get();
                if (skin != null) return skin;
            } catch (InterruptedException | ExecutionException e) {
                MSLogger.log(Level.SEVERE, "An error occurred while attempting to retrieve a skin from a link", e);
            }

            retryAttempts.incrementAndGet();
        } while (retryAttempts.get() < 3);

        return null;
    }

    /**
     * Serializes the skin into a map.
     * The map contains the name, value, and signature of the skin.
     * Used to save the skin to a yaml file.
     *
     * @return A map containing the name, value, and signature of the skin
     * @see #deserialize(String)
     */
    public static @NotNull Map<String, String> serialize(@NotNull Skin skin) {
        Map<String, String> serialized = new HashMap<>();

        serialized.put("name", skin.getName());
        serialized.put("value", skin.getValue());
        serialized.put("signature", skin.getSignature());
        return serialized;
    }

    /**
     * Deserializes a skin from a map.
     * The map must contain the name, value, and signature of the skin.
     * Used to load the skin from a yaml file.
     *
     * @param string Map string containing the name, value, and signature of the skin.
     *               Example of string : "name=a, value=b, signature=c"
     * @return The skin if the map contains the name, value, and signature of the skin
     *         and the skin is valid, otherwise null
     * @see #serialize(Skin)
     * @see #create(String, String, String)
     */
    public static @Nullable Skin deserialize(@NotNull String string) {
        Map<String, String> map = new HashMap<>();
        Matcher matcher = Pattern.compile("(name|value|signature)=([^,}]+)").matcher(string);

        while (matcher.find()) {
            map.put(matcher.group(1).toLowerCase(Locale.ROOT), matcher.group(2));
        }

        if (map.size() != 3) return null;

        String name = map.get("name");
        String value = map.get("value");
        String signature = map.get("signature");

        if (
                name == null
                || value == null
                || signature == null
        ) return null;

        try {
            return Skin.create(name, value, signature);
        } catch (IllegalArgumentException e) {
            MSLogger.log(Level.SEVERE, "Failed to deserialize skin: " + name, e);
            return null;
        }
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
    @Contract("null -> false")
    public boolean equals(@Nullable Skin skin) {
        return skin != null
                && this.name.equalsIgnoreCase(skin.getName())
                && this.value.equals(skin.getValue())
                && this.signature.equals(skin.getSignature());
    }

    /**
     * @param link Link to be checked
     * @return True if the link starts with https:// and ends with .png and the image is 64x64
     */
    public static boolean isValidSkinImg(@NotNull String link) {
        if (!link.startsWith("https://") || !link.endsWith(".png")) return false;
        try {
            BufferedImage image = ImageIO.read(new URL(link));
            return image.getWidth() == 64 && image.getHeight() == 64;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * @param src String to be checked
     * @return True if string is in valid Base64 scheme
     */
    public static boolean isValidBase64(@NotNull String src) {
        try {
            Base64.getDecoder().decode(src);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * @param string String to be checked
     * @return True if string matches {@link #NAME_REGEX}
     */
    @Contract(value = "null -> false")
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
                String value = texture.value();
                String signature = texture.signature();

                try {
                    return Skin.create(name, value, signature);
                } catch (IllegalArgumentException e) {
                    MSLogger.log(Level.SEVERE, "Failed to create skin : \"" + name + "\" with value : " + value + " and signature : " + signature, e);
                }
            }
            case 500, 400 -> {
                MineSkinErrorJson errorJson = response.getBodyResponse(MineSkinErrorJson.class);
                String errorCode = errorJson.errorCode();

                switch (errorCode) {
                    case "failed_to_create_id", "skin_change_failed" -> {
                        try {
                            TimeUnit.SECONDS.sleep(5);
                        } catch (InterruptedException e) {
                            MSLogger.log(Level.SEVERE, "Failed to sleep thread", e);
                        }
                    }
                    case "no_account_available" -> MSLogger.severe("No account available to create skin");
                    default -> MSLogger.severe("Unknown MineSkin error: " + errorCode);
                }
            }
            case 403 -> {
                MineSkinErrorJson errorJson = response.getBodyResponse(MineSkinErrorJson.class);
                String errorCode = errorJson.errorCode();
                String error = errorJson.error();

                if (errorCode.equals("invalid_api_key")) {
                    MSLogger.severe("Api key is not invalid! Reason: " + error);

                    switch (error) {
                        case "Invalid API Key" ->
                                MSLogger.severe("This api key is not registered on MineSkin!");
                        case "Client not allowed" ->
                                MSLogger.severe("This server ip is not on the api key allowed ips list!");
                        case "Origin not allowed" ->
                                MSLogger.severe("This server origin is not on the api key allowed origin list!");
                        case "Agent not allowed" ->
                                MSLogger.severe("This server agent is not on the api key allowed agents list!");
                        default -> MSLogger.severe("Unknown error :" + error);
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
                    MSLogger.log(Level.SEVERE, "Failed to sleep thread", e);
                }
            }
            default -> MSLogger.log(Level.SEVERE, "Unknown MineSkin error: " + response.getStatusCode());
        }

        return null;
    }
}
