/*
 * SkinsRestorer
 *
 * Copyright (C) 2023 SkinsRestorer
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 */
package com.minersstudios.msessentials.player.skin;

import com.google.gson.Gson;
import com.minersstudios.mscore.utility.ChatUtils;
import com.minersstudios.msessentials.MSEssentials;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static com.minersstudios.msessentials.player.skin.Skin.isValidSkinImg;

/**
 * Represents a response from the MineSkin API.
 */
public final class MineSkinResponse {
    private final int statusCode;
    private final String body;

    private static final Gson GSON = new Gson();
    private static final String MINE_SKIN_API_URL = "https://api.mineskin.org/generate/url/";

    private MineSkinResponse(
            final int statusCode,
            final @NotNull String body
    ) {
        this.statusCode = statusCode;
        this.body = body;
    }

    /**
     * Creates a new MineSkinResponse from a link to a skin image. It uses the
     * MineSkin API to generate a skin from the image, and returns the response.
     * If a MineSkin API key is set in the configuration, it will be used.
     *
     * @param plugin The plugin instance
     * @param link   The link to the skin must start with https:// and end with
     *               .png
     * @return The response from the MineSkin API
     * @throws IOException if the connection could not be established
     * @throws IllegalArgumentException If the link is not a valid skin image
     * @see #MINE_SKIN_API_URL
     */
    @Contract("_, _ -> new")
    public static @NotNull MineSkinResponse fromLink(
            final @NotNull MSEssentials plugin,
            final @NotNull String link
    ) throws IOException, IllegalArgumentException {
        if (!isValidSkinImg(link)) {
            throw new IllegalArgumentException("The link must be a valid skin image");
        }

        final String requestBody = "url=" + URLEncoder.encode(link, StandardCharsets.UTF_8);
        final String apiKey = plugin.getConfiguration().getMineSkinApiKey();
        final HttpURLConnection connection = (HttpURLConnection) new URL(MINE_SKIN_API_URL).openConnection();

        connection.setRequestMethod("POST");
        connection.setConnectTimeout(90000);
        connection.setReadTimeout(90000);
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setUseCaches(false);
        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestProperty("User-Agent", "WhoMine/MineSkinAPI");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        if (ChatUtils.isNotBlank(apiKey)) {
            connection.setRequestProperty("Authorization", "Bearer " + apiKey);
        }

        try (final var output = connection.getOutputStream()) {
            output.write(requestBody.getBytes(StandardCharsets.UTF_8));
        }

        connection.connect();

        return new MineSkinResponse(
                connection.getResponseCode(),
                getBody(connection)
        );
    }

    /**
     * @return The status code of the response
     */
    public int getStatusCode() {
        return this.statusCode;
    }

    /**
     * @return The body of the response
     */
    public @NotNull String getBody() {
        return this.body;
    }

    /**
     * @param clazz The class to deserialize the body to
     * @param <T>   The type of the class, use {@link MineSkinJson} if you want
     *              to get the generated skin data, or {@link MineSkinErrorJson}
     *              if you want to get the error data, or
     *              {@link MineSkinDelayErrorJson} if you want to get the delay
     *              error data
     * @return The body of the response deserialized to the given class
     */
    public <T> @NotNull T getBodyResponse(final @NotNull Class<T> clazz) {
        return GSON.fromJson(this.body, clazz);
    }

    private static @NotNull String getBody(final @NotNull HttpURLConnection connection) throws IOException {
        final StringBuilder body = new StringBuilder();
        InputStream is;

        try {
            is = connection.getInputStream();
        } catch (final IOException ignored) {
            is = connection.getErrorStream();
        }

        if (is == null) {
            throw new IOException("Failed to get input stream");
        }

        try (final var input = new DataInputStream(is)) {
            final byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = input.read(buffer)) != -1) {
                body.append(new String(buffer, 0, bytesRead, StandardCharsets.UTF_8));
            }
        }

        return body.toString();
    }
}
