package com.minersstudios.msessentials.player.skin;

/**
 * This class is a record class for the MineSkin API error response. It is used
 * to deserialize the JSON error response from the MineSkin API.
 */
public record MineSkinErrorJson(
        String errorCode,
        String error
) {}
