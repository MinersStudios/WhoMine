package com.github.minersstudios.msessentials.player.skin;

import com.google.gson.annotations.SerializedName;

import java.util.UUID;

/**
 * This class is a record class for the MineSkin API response.
 * It is used to deserialize the JSON response from the MineSkin API.
 */
public record MineSkinJson(
        String id,
        String idStr,
        String uuid,
        String name,
        String variant,
        Data data,
        long timestamp,
        int duration,
        int account,
        String server,
        @SerializedName("private") boolean $private,
        int views,
        int nextRequest,
        boolean duplicate
) {

    public record Data(
            UUID uuid,
            Texture texture
    ) {

        public record Texture(
                String value,
                String signature,
                String url
        ) {}
    }
}
