package com.minersstudios.msblock.listeners.block;

import com.minersstudios.msblock.customblock.CustomBlockData;
import com.minersstudios.msblock.customblock.NoteBlockData;
import com.minersstudios.mscore.listener.event.AbstractMSListener;
import com.minersstudios.mscore.listener.event.MSListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.NotePlayEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@MSListener
public class NotePlayListener extends AbstractMSListener {
    private static final @NotNull NoteBlockData DEFAULT = Objects.requireNonNull(CustomBlockData.DEFAULT.getNoteBlockData());

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onNotePlay(@NotNull NotePlayEvent event) {
        if (
                !(event.getInstrument() == DEFAULT.getInstrument()
                && event.getNote().equals(DEFAULT.getNote()))
        ) {
            event.setCancelled(true);
        }
    }
}
