package com.minersstudios.msessentials.player.skin;

import com.destroystokyo.paper.profile.CraftPlayerProfile;
import com.minersstudios.mscore.utility.ChatUtils;
import com.minersstudios.msessentials.MSEssentials;
import com.minersstudios.msessentials.player.PlayerFile;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
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
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Skin class to create skins from values and signatures or image links. Used in
 * {@link PlayerFile} to store the skin of a player.
 *
 * @see <a href="https://wiki.vg/Mojang_API#UUID_to_Profile_and_Skin.2FCape">Mojang API</a>
 * @see <a href="https://mineskin.org">MineSkin API</a>
 */
public final class Skin implements ConfigurationSerializable {
    private final String name;
    private final String value;
    private final String signature;

    private static final String NAME_REGEX = "[a-zA-ZЀ-ӿ-0-9]{1,32}";
    private static final Pattern NAME_PATTERN = Pattern.compile(NAME_REGEX);
    private static final Pattern DESERIALIZE_PATTERN = Pattern.compile("(name|value|signature)=([^,}]+)");
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadExecutor(Thread::new);
    private static final Logger LOGGER = Logger.getLogger("SkinSystem");

    //<editor-fold defaultstate="collapsed" desc="Error codes">
    private static final String CODE_FAILED_TO_CREATE_ID =  "failed_to_create_id";
    private static final String CODE_SKIN_CHANGE_FAILED =   "skin_change_failed";
    private static final String CODE_NO_ACCOUNT_AVAILABLE = "no_account_available";
    private static final String CODE_INVALID_API_KEY =      "invalid_api_key";
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Error messages">
    private static final String ERROR_INVALID_API_KEY = "Invalid API Key";
    private static final String ERROR_CLIENT_NOT_ALLOWED = "Client not allowed";
    private static final String ERROR_ORIGIN_NOT_ALLOWED = "Origin not allowed";
    private static final String ERROR_AGENT_NOT_ALLOWED = "Agent not allowed";
    //</editor-fold>

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
     * @throws IllegalArgumentException If the name, value, or signature is not
     *                                  valid
     * @see <a href="https://wiki.vg/Mojang_API#UUID_to_Profile_and_Skin.2FCape">Mojang API</a>
     * @see <a href="https://mineskin.org">MineSkin API</a>
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
     * Creates a skin from an image link. It will attempt to retrieve the skin
     * 3 times before giving up. The value and signature generates with the
     * MineSkinAPI.
     *
     * @param plugin Plugin instance
     * @param name   The name of the skin
     * @param link   The link to the skin must start with "https://" or "http://"
     * @return The skin if it was successfully retrieved, otherwise null
     * @throws IllegalArgumentException If the name or link is invalid or the
     *                                  image is not 64x64 pixels
     * @see <a href="https://wiki.vg/Mojang_API#UUID_to_Profile_and_Skin.2FCape">Mojang API</a>
     * @see <a href="https://mineskin.org">MineSkin API</a>
     */
    public static @Nullable Skin create(
            final @NotNull MSEssentials plugin,
            final @NotNull String name,
            final @NotNull String link
    ) throws IllegalArgumentException {
        if (!matchesNameRegex(name)) {
            throw new IllegalArgumentException(
                    "The name must be between 1 and 32 characters long and only contain letters, numbers, and underscores"
            );
        }

        final AtomicInteger retryAttempts = new AtomicInteger(0);

        do {
            final var future =
                    CompletableFuture
                    .supplyAsync(() -> handleLink(plugin, name, link), EXECUTOR_SERVICE)
                    .exceptionally(ignored -> null);

            if (future == null) {
                throw new IllegalArgumentException(
                        "The link must be a valid image link"
                );
            }

            try {
                return future.get();
            } catch (final InterruptedException | ExecutionException e) {
                LOGGER.log(
                        Level.SEVERE,
                        "An error occurred while attempting to retrieve a skin from a link",
                        e
                );
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
     * @return A hash code value for the skin
     */
    @Override
    public int hashCode() {
        int result = 17;

        result = 31 * result + this.name.hashCode();
        result = 31 * result + this.value.hashCode();
        result = 31 * result + this.signature.hashCode();

        return result;
    }

    /**
     * @param obj Skin to compare
     * @return True if the skin's name, value, and signature are equal
     */
    @Contract("null -> false")
    public boolean equals(final @Nullable Object obj) {
        return obj == this
                || (
                        obj instanceof Skin that
                        && this.name.equalsIgnoreCase(that.getName())
                        && this.value.equals(that.getValue())
                        && this.signature.equals(that.getSignature())
                );
    }

    /**
     * Serializes the skin into a map. The map contains the name, value, and
     * signature of the skin.
     * <br>
     * Used to save the skin to a yaml file.
     *
     * @return A map containing the name, value, and signature of the skin
     * @see #deserialize(String)
     */
    @Override
    public @NotNull Map<String, Object> serialize() {
        final var serialized = new Object2ObjectOpenHashMap<String, Object>();

        serialized.put("name", this.name);
        serialized.put("value", this.value);
        serialized.put("signature", this.signature);

        return serialized;
    }

    /**
     * Deserializes a skin from a map. The map must contain the name, value, and
     * signature of the skin.
     * <br>
     * Used to load the skin from a yaml file.
     *
     * @param string Map string containing the name, value, and signature of the
     *               skin. Example of string : "name=a, value=b, signature=c"
     * @return The skin if the map contains the name, value, and signature of
     *         the skin and the skin is valid, otherwise null
     * @see #serialize()
     * @see #create(String, String, String)
     */
    public static @Nullable Skin deserialize(final @Nullable String string) {
        if (ChatUtils.isBlank(string)) {
            return null;
        }

        final var map = new Object2ObjectOpenHashMap<String, String>();
        final Matcher matcher = DESERIALIZE_PATTERN.matcher(string);

        while (matcher.find()) {
            map.put(matcher.group(1), matcher.group(2));
        }

        if (map.size() != 3) {
            return null;
        }

        final String name = map.get("name");
        final String value = map.get("value");
        final String signature = map.get("signature");

        if (
                name == null
                || value == null
                || signature == null
        ) {
            return null;
        }

        try {
            return Skin.create(name, value, signature);
        } catch (final IllegalArgumentException e) {
            LOGGER.log(
                    Level.SEVERE,
                    "Failed to deserialize skin: " + name,
                    e
            );

            return null;
        }
    }

    /**
     * @param link Link to be checked
     * @return True if the link is a valid skin image
     */
    public static boolean isValidSkinImg(final @NotNull String link) {
        try {
            final BufferedImage image = ImageIO.read(new URL(link));
            return image != null
                    && image.getWidth() == 64
                    && image.getHeight() == 64;
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
        return string != null
                && NAME_PATTERN.matcher(string).matches();
    }

    /**
     * This method will attempt to retrieve the skin 3 times before giving up.
     * The value and signature generates with the MineSkinAPI. If the response
     * status code is 200, the skin will be returned.
     *
     * @param plugin Plugin instance
     * @param name   Name of the skin
     * @param link   Link to the skin
     * @return The skin if it was successfully retrieved, otherwise null
     * @throws IllegalArgumentException If the link is not a valid skin image
     */
    private static @Nullable Skin handleLink(
            final @NotNull MSEssentials plugin,
            final @NotNull String name,
            final @NotNull String link
    ) throws IllegalArgumentException {
        MineSkinResponse response;

        for (int i = 0; true; ++i) {
            try {
                response = MineSkinResponse.fromLink(plugin, link);
                break;
            } catch (final IOException ignored) {
                if (i >= 2) {
                    return null;
                }
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
                    LOGGER.log(
                            Level.SEVERE,
                            "Failed to create skin : '" + name + "' with value : '" + value + "' and signature : '" + signature + "'",
                            e
                    );
                }
            }
            case 500, 400 -> {
                final MineSkinErrorJson errorJson = response.getBodyResponse(MineSkinErrorJson.class);
                final String errorCode = errorJson.errorCode();

                switch (errorCode) {
                    case CODE_FAILED_TO_CREATE_ID, CODE_SKIN_CHANGE_FAILED -> {
                        try {
                            TimeUnit.SECONDS.sleep(5);
                        } catch (final InterruptedException e) {
                            LOGGER.log(
                                    Level.SEVERE,
                                    "Failed to sleep thread",
                                    e
                            );
                        }
                    }
                    case CODE_NO_ACCOUNT_AVAILABLE -> LOGGER.severe("No account available to create skin");
                    default -> LOGGER.severe("Unknown MineSkin error: " + errorCode);
                }
            }
            case 403 -> {
                final MineSkinErrorJson errorJson = response.getBodyResponse(MineSkinErrorJson.class);
                final String errorCode = errorJson.errorCode();
                final String error = errorJson.error();

                if (errorCode.equals(CODE_INVALID_API_KEY)) {
                    LOGGER.severe(
                            "Api key is not valid! Reason: " + error + " "
                            + (switch (error) {
                                case ERROR_INVALID_API_KEY ->    "This api key is not registered on MineSkin!";
                                case ERROR_CLIENT_NOT_ALLOWED -> "This server ip is not on the api key allowed ips list!";
                                case ERROR_ORIGIN_NOT_ALLOWED -> "This server origin is not on the api key allowed origin list!";
                                case ERROR_AGENT_NOT_ALLOWED ->  "This server agent is not on the api key allowed agents list!";
                                default ->                       "";
                            })
                    );
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
                    final long duration = Duration.between(
                            Instant.now(),
                            Instant.ofEpochSecond(nextRequest)
                    ).getSeconds();

                    if (duration > 0) {
                        sleepDuration = duration;
                    }
                }

                try {
                    TimeUnit.SECONDS.sleep(sleepDuration);
                } catch (final InterruptedException e) {
                    LOGGER.log(
                            Level.SEVERE,
                            "Failed to sleep thread",
                            e
                    );
                }
            }
            default -> LOGGER.severe("Unknown MineSkin error: " + response.getStatusCode());
        }

        return null;
    }

    /**
     * @param src String to be checked
     * @return True if string is in a valid Base64 scheme
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
