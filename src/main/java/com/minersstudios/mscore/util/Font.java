package com.minersstudios.mscore.util;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Contract;

import static com.minersstudios.mscore.util.ChatUtils.createDefaultStyledText;

/**
 * This class provides utility methods and constants for handling fonts. It
 * contains two nested classes, {@link Chars} and {@link Components} constants
 * and components respectively. Icons for all PUA characters are set in the
 * resource-pack.
 * <br>
 * <b>NOTE:</b> PUA characters - "Private Use Area" characters, in range
 * from <code>0xE000</code> to <code>0xF8FF</code>.
 */
public final class Font {

    @Contract(" -> fail")
    private Font() {
        throw new AssertionError("Utility class");
    }

    /**
     * This class provides string constants for various font characters.
     */
    public static final class Chars {
        //<editor-fold desc="Characters" defaultstate="collapsed">
        public static final String NONE = "\uE001";
        public static final String LOGO = "\uE002";
        public static final String BIG_LOGO = "\uE004";
        public static final String DISCONNECT_SCREEN = "\uE008";
        public static final String RED_EXCLAMATION_MARK = "\uE011";
        public static final String GREEN_EXCLAMATION_MARK = "\uE012";
        public static final String YELLOW_EXCLAMATION_MARK = "\uE013";
        public static final String DISCORD = "\uE014";
        public static final String SPEECH = "\uE015";
        public static final String ARMOR = "\uE016";
        public static final String ARMOR_HALF = "\uE017";
        public static final String HUNGER = "\uE018";
        public static final String POISONED_HUNGER = "\uE019";
        public static final String HUNGER_HALF = "\uE020";
        public static final String POISONED_HUNGER_HALF = "\uE021";
        public static final String PAINTABLE = "\uE022";
        public static final String DETECTIVE = "\uE023";
        public static final String MAYOR = "\uE024";
        public static final String VICE_MAYOR = "\uE025";
        public static final String POLICE = "\uE026";
        public static final String CRAFTS_SCREEN = "\uE027";
        public static final String CRAFT_SCREEN = "\uE028";
        public static final String SHIFT_BUTTON = "\uE029";
        public static final String RENAMES_SCREEN = "\uE030";
        public static final String RENAME_SCREEN = "\uE031";
        public static final String CRAFTS_CATEGORIES_SCREEN = "\uE032";
        public static final String WRENCHABLE = "\uE033";
        public static final String DISCORD_LINK_SCREEN = "\uE034";
        public static final String SKINS_MENU_SCREEN = "\uE035";
        public static final String DISCORD_NUMBER_0 = "\uE036";
        public static final String DISCORD_NUMBER_1 = "\uE037";
        public static final String DISCORD_NUMBER_2 = "\uE038";
        public static final String DISCORD_NUMBER_3 = "\uE039";
        public static final String DISCORD_NUMBER_4 = "\uE040";
        public static final String DISCORD_NUMBER_5 = "\uE041";
        public static final String DISCORD_NUMBER_6 = "\uE042";
        public static final String DISCORD_NUMBER_7 = "\uE043";
        public static final String DISCORD_NUMBER_8 = "\uE044";
        public static final String DISCORD_NUMBER_9 = "\uE045";
        public static final String PIXEL_SPLIT_P28 = "\uE046";
        public static final String PIXEL_SPLIT_M10 = "\uE047";
        public static final String PIXEL_SPLIT_M20 = "\uE048";
        public static final String PIXEL_SPLIT_M66 = "\uE049";
        public static final String PIXEL_SPLIT_M34 = "\uE050";
        public static final String PIXEL_SPLIT_P53 = "\uE051";
        public static final String PIXEL_SPLIT_M7 = "\uE052";
        public static final String PIXEL_SPLIT_M8 = "\uE053";
        public static final String PIXEL_SPLIT_M6 = "\uE054";
        public static final String PIXEL_SPLIT_M111 = "\uE055";
        public static final String PIXEL_SPLIT_M112 = "\uE056";
        public static final String SPACE_M65 = "\uE063";
        //</editor-fold>

        private Chars() {
            throw new AssertionError("Utility class");
        }
    }

    /**
     * This class provides Component objects for various font characters.
     * Each Component is created by calling the
     * {@link ChatUtils#createDefaultStyledText(String)} method with the
     * corresponding string constant from the Chars class.
     */
    public static final class Components {
        //<editor-fold desc="Components" defaultstate="collapsed">
        public static final Component NONE = createDefaultStyledText(Chars.NONE);
        public static final Component LOGO = createDefaultStyledText(Chars.LOGO);
        public static final Component BIG_LOGO = createDefaultStyledText(Chars.BIG_LOGO);
        public static final Component DISCONNECT_SCREEN = createDefaultStyledText(Chars.DISCONNECT_SCREEN);
        public static final Component RED_EXCLAMATION_MARK = createDefaultStyledText(Chars.RED_EXCLAMATION_MARK);
        public static final Component GREEN_EXCLAMATION_MARK = createDefaultStyledText(Chars.GREEN_EXCLAMATION_MARK);
        public static final Component YELLOW_EXCLAMATION_MARK = createDefaultStyledText(Chars.YELLOW_EXCLAMATION_MARK);
        public static final Component DISCORD = createDefaultStyledText(Chars.DISCORD);
        public static final Component SPEECH = createDefaultStyledText(Chars.SPEECH);
        public static final Component ARMOR = createDefaultStyledText(Chars.ARMOR);
        public static final Component ARMOR_HALF = createDefaultStyledText(Chars.ARMOR_HALF);
        public static final Component HUNGER = createDefaultStyledText(Chars.HUNGER);
        public static final Component POISONED_HUNGER = createDefaultStyledText(Chars.POISONED_HUNGER);
        public static final Component HUNGER_HALF = createDefaultStyledText(Chars.HUNGER_HALF);
        public static final Component POISONED_HUNGER_HALF = createDefaultStyledText(Chars.POISONED_HUNGER_HALF);
        public static final Component PAINTABLE = createDefaultStyledText(Chars.PAINTABLE);
        public static final Component DETECTIVE = createDefaultStyledText(Chars.DETECTIVE);
        public static final Component MAYOR = createDefaultStyledText(Chars.MAYOR);
        public static final Component VICE_MAYOR = createDefaultStyledText(Chars.VICE_MAYOR);
        public static final Component POLICE = createDefaultStyledText(Chars.POLICE);
        public static final Component CRAFTS_SCREEN = createDefaultStyledText(Chars.CRAFTS_SCREEN);
        public static final Component CRAFT_SCREEN = createDefaultStyledText(Chars.CRAFT_SCREEN);
        public static final Component SHIFT_BUTTON = createDefaultStyledText(Chars.SHIFT_BUTTON);
        public static final Component RENAMES_SCREEN = createDefaultStyledText(Chars.RENAMES_SCREEN);
        public static final Component RENAME_SCREEN = createDefaultStyledText(Chars.RENAME_SCREEN);
        public static final Component CRAFTS_CATEGORIES_SCREEN = createDefaultStyledText(Chars.CRAFTS_CATEGORIES_SCREEN);
        public static final Component WRENCHABLE = createDefaultStyledText(Chars.WRENCHABLE);
        public static final Component DISCORD_LINK_SCREEN = createDefaultStyledText(Chars.DISCORD_LINK_SCREEN);
        public static final Component SKINS_MENU_SCREEN = createDefaultStyledText(Chars.SKINS_MENU_SCREEN);
        public static final Component DISCORD_NUMBER_0 = createDefaultStyledText(Chars.DISCORD_NUMBER_0);
        public static final Component DISCORD_NUMBER_1 = createDefaultStyledText(Chars.DISCORD_NUMBER_1);
        public static final Component DISCORD_NUMBER_2 = createDefaultStyledText(Chars.DISCORD_NUMBER_2);
        public static final Component DISCORD_NUMBER_3 = createDefaultStyledText(Chars.DISCORD_NUMBER_3);
        public static final Component DISCORD_NUMBER_4 = createDefaultStyledText(Chars.DISCORD_NUMBER_4);
        public static final Component DISCORD_NUMBER_5 = createDefaultStyledText(Chars.DISCORD_NUMBER_5);
        public static final Component DISCORD_NUMBER_6 = createDefaultStyledText(Chars.DISCORD_NUMBER_6);
        public static final Component DISCORD_NUMBER_7 = createDefaultStyledText(Chars.DISCORD_NUMBER_7);
        public static final Component DISCORD_NUMBER_8 = createDefaultStyledText(Chars.DISCORD_NUMBER_8);
        public static final Component DISCORD_NUMBER_9 = createDefaultStyledText(Chars.DISCORD_NUMBER_9);
        public static final Component PIXEL_SPLIT_P28 = createDefaultStyledText(Chars.PIXEL_SPLIT_P28);
        public static final Component PIXEL_SPLIT_M10 = createDefaultStyledText(Chars.PIXEL_SPLIT_M10);
        public static final Component PIXEL_SPLIT_M20 = createDefaultStyledText(Chars.PIXEL_SPLIT_M20);
        public static final Component PIXEL_SPLIT_M66 = createDefaultStyledText(Chars.PIXEL_SPLIT_M66);
        public static final Component PIXEL_SPLIT_M34 = createDefaultStyledText(Chars.PIXEL_SPLIT_M34);
        public static final Component PIXEL_SPLIT_P53 = createDefaultStyledText(Chars.PIXEL_SPLIT_P53);
        public static final Component PIXEL_SPLIT_M7 = createDefaultStyledText(Chars.PIXEL_SPLIT_M7);
        public static final Component PIXEL_SPLIT_M8 = createDefaultStyledText(Chars.PIXEL_SPLIT_M8);
        public static final Component PIXEL_SPLIT_M6 = createDefaultStyledText(Chars.PIXEL_SPLIT_M6);
        public static final Component PIXEL_SPLIT_M111 = createDefaultStyledText(Chars.PIXEL_SPLIT_M111);
        public static final Component PIXEL_SPLIT_M112 = createDefaultStyledText(Chars.PIXEL_SPLIT_M112);
        public static final Component SPACE_M65 = createDefaultStyledText(Chars.SPACE_M65);
        //</editor-fold>

        private Components() {
            throw new AssertionError("Utility class");
        }
    }
}
