package com.minersstudios.mscore.utils;

import com.google.common.collect.ImmutableList;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Contract;

import static com.minersstudios.mscore.utils.ChatUtils.createDefaultStyledText;

/**
 * Badges, used for message decoration
 */
public final class Badges {
    public static final Component GREEN_EXCLAMATION_MARK = createDefaultStyledText(" ꀒ ");
    public static final Component YELLOW_EXCLAMATION_MARK = createDefaultStyledText(" ꀓ ");
    public static final Component RED_EXCLAMATION_MARK = createDefaultStyledText(" ꀑ ");
    public static final Component SPEECH = createDefaultStyledText(" ꀕ ");
    public static final Component DISCORD = createDefaultStyledText(" ꀔ ");
    public static final Component PAINTABLE_LORE = createDefaultStyledText("ꀢ");
    public static final Component WRENCHABLE_LORE = createDefaultStyledText("ꀢ");

    public static final ImmutableList<Component> PAINTABLE_LORE_LIST = ImmutableList.of(PAINTABLE_LORE);
    public static final ImmutableList<Component> WRENCHABLE_LORE_LIST = ImmutableList.of(WRENCHABLE_LORE);

    @Contract(" -> fail")
    private Badges() {
        throw new AssertionError("Utility class");
    }
}
