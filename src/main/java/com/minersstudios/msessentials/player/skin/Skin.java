package com.minersstudios.msessentials.player.skin;

import com.destroystokyo.paper.profile.CraftPlayerProfile;
import com.minersstudios.mscore.util.ChatUtils;
import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.player.PlayerFile;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
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
public final class Skin implements ConfigurationSerializable {
    private final String name;
    private final String value;
    private final String signature;

    private static final String NAME_REGEX = "[a-zA-ZЀ-ӿ-0-9]{1,32}";
    private static final Pattern NAME_PATTERN = Pattern.compile(NAME_REGEX);
    private static final Pattern DESERIALIZE_PATTERN = Pattern.compile("(name|value|signature)=([^,}]+)");
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadExecutor(Thread::new);

    private Skin(
            final @NotNull String name,
            final @NotNull String value,
            final @NotNull String signature
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
    @Contract("_, _, _ -> new")
    public static @NotNull Skin create(
            final @NotNull String name,
            final @NotNull String value,
            final @NotNull String signature
    ) throws IllegalArgumentException {
        if (!matchesNameRegex(name)) {
            throw new IllegalArgumentException("The name must be between 1 and 32 characters long and only contain letters, numbers, and underscores");
        }

        if (!isValidBase64(value)) {
            throw new IllegalArgumentException("The value must be a valid Base64 string");
        }

        if (!isValidBase64(signature)) {
            throw new IllegalArgumentException("The signature must be a valid Base64 string");
        }

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
            final @NotNull String name,
            final @NotNull String link
    ) throws IllegalArgumentException {
        if (!matchesNameRegex(name)) {
            throw new IllegalArgumentException("The name must be between 1 and 32 characters long and only contain letters, numbers, and underscores");
        }

        if (!isValidSkinImg(link)) {
            throw new IllegalArgumentException("The link must start with https:// and end with .png and the image must be 64x64 pixels");
        }

        final AtomicInteger retryAttempts = new AtomicInteger(0);

        do {
            final var future = CompletableFuture.supplyAsync(() -> handleLink(name, link), EXECUTOR_SERVICE);

            try {
                final Skin skin = future.get();
                if (skin != null) return skin;
            } catch (final InterruptedException | ExecutionException e) {
                MSEssentials.logger().log(Level.SEVERE, "An error occurred while attempting to retrieve a skin from a link", e);
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
        final ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);
        final SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
        final GameProfile profile = new GameProfile(UUID.randomUUID(), this.name);

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
    public boolean equals(final @Nullable Skin skin) {
        return skin != null
                && this.name.equalsIgnoreCase(skin.getName())
                && this.value.equals(skin.getValue())
                && this.signature.equals(skin.getSignature());
    }

    /**
     * Serializes the skin into a map.
     * The map contains the name, value, and signature of the skin.
     * Used to save the skin to a yaml file.
     *
     * @return A map containing the name, value, and signature of the skin
     * @see #deserialize(String)
     */
    @Override
    public @NotNull Map<String, Object> serialize() {
        final var serialized = new HashMap<String, Object>();

        serialized.put("name", this.name);
        serialized.put("value", this.value);
        serialized.put("signature", this.signature);

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
     * @see #serialize()
     * @see #create(String, String, String)
     */
    public static @Nullable Skin deserialize(final @Nullable String string) {
        if (StringUtils.isBlank(string)) return null;

        final var map = new HashMap<String, String>();
        final Matcher matcher = DESERIALIZE_PATTERN.matcher(string);

        while (matcher.find()) {
            map.put(matcher.group(1), matcher.group(2));
        }

        if (map.size() != 3) return null;

        final String name = map.get("name");
        final String value = map.get("value");
        final String signature = map.get("signature");

        if (
                name == null
                || value == null
                || signature == null
        ) return null;

        try {
            return Skin.create(name, value, signature);
        } catch (final IllegalArgumentException e) {
            MSEssentials.logger().log(Level.SEVERE, "Failed to deserialize skin: " + name, e);
            return null;
        }
    }

    /**
     * @param link Link to be checked
     * @return True if the link starts with https:// and ends with .png and the image is 64x64
     */
    public static boolean isValidSkinImg(final @NotNull String link) {
        if (!link.startsWith("https://") || !link.endsWith(".png")) return false;
        try {
            final BufferedImage image = ImageIO.read(new URL(link));
            return image.getWidth() == 64 && image.getHeight() == 64;
        } catch (final IOException ignored) {
            return false;
        }
    }

    /**
     * @param string String to be checked
     * @return True if string matches {@link #NAME_REGEX}
     */
    @Contract("null -> false")
    public static boolean matchesNameRegex(final @Nullable String string) {
        return string != null && NAME_PATTERN.matcher(string).matches();
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
            final @NotNull String name,
            final @NotNull String link
    ) {
        MineSkinResponse response;

        for (int i = 0; true; ++i) {
            try {
                response = MineSkinResponse.fromLink(link);
                break;
            } catch (final IOException ignored) {
                if (i >= 2) return null;
            }
        }

        switch (response.getStatusCode()) {
            case 200 -> {
                final MineSkinJson.Data.Texture texture = response.getBodyResponse(MineSkinJson.class).data().texture();
                final String value = texture.value();
                final String signature = texture.signature();

                try {
                    return Skin.create(name, value, signature);
                } catch (final IllegalArgumentException e) {
                    MSEssentials.logger().log(Level.SEVERE, "Failed to create skin : '" + name + "' with value : '" + value + "' and signature : '" + signature + "'", e);
                }
            }
            case 500, 400 -> {
                final MineSkinErrorJson errorJson = response.getBodyResponse(MineSkinErrorJson.class);
                final String errorCode = errorJson.errorCode();

                switch (errorCode) {
                    case "failed_to_create_id", "skin_change_failed" -> {
                        try {
                            TimeUnit.SECONDS.sleep(5);
                        } catch (final InterruptedException e) {
                            MSEssentials.logger().log(Level.SEVERE, "Failed to sleep thread", e);
                        }
                    }
                    case "no_account_available" -> MSEssentials.logger().severe("No account available to create skin");
                    default -> MSEssentials.logger().severe("Unknown MineSkin error: " + errorCode);
                }
            }
            case 403 -> {
                final MineSkinErrorJson errorJson = response.getBodyResponse(MineSkinErrorJson.class);
                final String errorCode = errorJson.errorCode();
                final String error = errorJson.error();

                if (errorCode.equals("invalid_api_key")) {
                    MSEssentials.logger().severe("Api key is not invalid! Reason: " + error);

                    switch (error) {
                        case "Invalid API Key" ->
                                MSEssentials.logger().severe("This api key is not registered on MineSkin!");
                        case "Client not allowed" ->
                                MSEssentials.logger().severe("This server ip is not on the api key allowed ips list!");
                        case "Origin not allowed" ->
                                MSEssentials.logger().severe("This server origin is not on the api key allowed origin list!");
                        case "Agent not allowed" ->
                                MSEssentials.logger().severe("This server agent is not on the api key allowed agents list!");
                        default -> MSEssentials.logger().severe("Unknown error :" + error);
                    }
                }
            }
            case 429 -> {
                final MineSkinDelayErrorJson delayErrorJson = response.getBodyResponse(MineSkinDelayErrorJson.class);
                final Integer delay = delayErrorJson.delay();
                final Integer nextRequest = delayErrorJson.nextRequest();
                long sleepDuration = 2;

                if (delay != null) {
                    sleepDuration = delay;
                } else if (nextRequest != null) {
                    final long duration = Duration.between(Instant.now(), Instant.ofEpochSecond(nextRequest)).getSeconds();

                    if (duration > 0) {
                        sleepDuration = duration;
                    }
                }

                try {
                    TimeUnit.SECONDS.sleep(sleepDuration);
                } catch (final InterruptedException e) {
                    MSEssentials.logger().log(Level.SEVERE, "Failed to sleep thread", e);
                }
            }
            default -> MSEssentials.logger().log(Level.SEVERE, "Unknown MineSkin error: " + response.getStatusCode());
        }

        return null;
    }

    /**
     * @param src String to be checked
     * @return True if string is in valid Base64 scheme
     */
    private static boolean isValidBase64(final @NotNull String src) {
        try {
            Base64.getDecoder().decode(src);
            return true;
        } catch (final IllegalArgumentException ignored) {
            return false;
        }
    }
}
