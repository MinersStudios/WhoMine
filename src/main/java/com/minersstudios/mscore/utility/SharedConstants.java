package com.minersstudios.mscore.utility;

import org.jetbrains.annotations.Contract;

import static io.papermc.paper.configuration.PaperConfigurations.CONFIG_DIR;

/**
 * Shared constants
 */
public final class SharedConstants {
    public static final String GLOBAL_PACKAGE =                        "com.minersstudios";
    public static final String GLOBAL_FOLDER_PATH =                    CONFIG_DIR + "/minersstudios/";
    public static final String LANGUAGE_FOLDER_PATH =                  GLOBAL_FOLDER_PATH + "language/";
    public static final String PAPER_GLOBAL_CONFIG_FILE_NAME =         "paper-global.yml";
    public static final String PAPER_WORLD_DEFAULTS_CONFIG_FILE_NAME = "paper-world-defaults.yml";
    public static final String PAPER_WORLD_CONFIG_FILE_NAME =          "paper-world.yml";
    public static final String PAPER_GLOBAL_CONFIG_PATH =              CONFIG_DIR + '/' + PAPER_GLOBAL_CONFIG_FILE_NAME;
    public static final String PAPER_WORLD_DEFAULTS_PATH =             CONFIG_DIR + '/' + PAPER_WORLD_DEFAULTS_CONFIG_FILE_NAME;
    public static final String DATE_FORMAT =                           "EEE, yyyy-MM-dd HH:mm z";
    public static final String LANGUAGE_CODE =                         "ru_ru";
    public static final String LANGUAGE_FOLDER_LINK =                  "https://raw.githubusercontent.com/MinersStudios/WMConfig/release/lang/";
    public static final String DISCORD_LINK =                          "https://discord.whomine.net";
    public static final String CONSOLE_NICKNAME =                      "$Console";
    public static final String INVISIBLE_ITEM_FRAME_TAG =              "invisibleItemFrame";
    public static final String HIDE_TAGS_TEAM_NAME =                   "hide_tags";
    public static final int SIT_RANGE =                                9;

    @Contract(" -> fail")
    private SharedConstants() throws AssertionError {
        throw new AssertionError("Utility class");
    }
}
