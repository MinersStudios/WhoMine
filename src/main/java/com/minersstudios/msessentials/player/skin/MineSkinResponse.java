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

import com.minersstudios.msessentials.MSEssentials;
import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Represents a response from the MineSkin API.
 */
public class MineSkinResponse {
    private final int statusCode;
    private final String body;

    private static final Gson GSON = new Gson();
    private static final String MINE_SKIN_API_URL = "https://api.mineskin.org/generate/url/";

    private MineSkinResponse(
            int statusCode,
            @NotNull String body
    ) {
        this.statusCode = statusCode;
        this.body = body;
    }

    /**
     * Creates a new MineSkinResponse from a link to a skin image.
     * It uses the MineSkin API to generate a skin from the image, and returns the response.
     * If a MineSkin API key is set in the configuration, it will be used.
     *
     * @param link The link to the skin, must start with https:// and end with .png
     * @return The response from the MineSkin API
     * @throws IOException if the connection could not be established
     * @throws IllegalArgumentException if the link does not start with https:// and end with .png
     * @see #MINE_SKIN_API_URL
     */
    @Contract("_ -> new")
    public static @NotNull MineSkinResponse fromLink(@NotNull String link) throws IOException, IllegalArgumentException {
        Preconditions.checkArgument(link.startsWith("https://") || link.endsWith(".png"), "The link must start with https:// and end with .png");

        String requestBody = "url=" + URLEncoder.encode(link, StandardCharsets.UTF_8);
        String apiKey = MSEssentials.getConfiguration().mineSkinApiKey;
        HttpURLConnection connection = (HttpURLConnection) new URL(MINE_SKIN_API_URL).openConnection();

        connection.setRequestMethod("POST");
        connection.setConnectTimeout(90000);
        connection.setReadTimeout(90000);
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setUseCaches(false);
        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestProperty("User-Agent", "WhoMine/MineSkinAPI");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        if (!StringUtils.isBlank(apiKey)) {
            connection.setRequestProperty("Authorization", "Bearer " + apiKey);
        }

        try (var output = connection.getOutputStream()) {
            output.write(requestBody.getBytes(StandardCharsets.UTF_8));
        }

        connection.connect();

        int responseCode = connection.getResponseCode();
        StringBuilder body = new StringBuilder();
        InputStream is;

        try {
            is = connection.getInputStream();
        } catch (IOException e) {
            is = connection.getErrorStream();
        }

        if (is == null) {
            throw new IOException("Failed to get input stream");
        }

        try (var input = new DataInputStream(is)) {
            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = input.read(buffer)) != -1) {
                body.append(new String(buffer, 0, bytesRead, StandardCharsets.UTF_8));
            }
        }

        return new MineSkinResponse(responseCode, body.toString());
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
     * @param <T>   The type of the class,
     *              use {@link MineSkinJson} if you want to get the generated skin data,
     *              or {@link MineSkinErrorJson} if you want to get the error data,
     *              or {@link MineSkinDelayErrorJson} if you want to get the delay error data
     * @return The body of the response deserialized to the given class
     */
    public <T> @NotNull T getBodyResponse(@NotNull Class<T> clazz) {
        return GSON.fromJson(this.body, clazz);
    }
}
