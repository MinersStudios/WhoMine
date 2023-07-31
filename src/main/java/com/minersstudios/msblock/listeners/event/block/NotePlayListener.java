package com.minersstudios.msblock.listeners.event.block;

import com.minersstudios.msblock.customblock.file.NoteBlockData;
import com.minersstudios.mscore.listener.event.AbstractMSListener;
import com.minersstudios.mscore.listener.event.MSListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.NotePlayEvent;
import org.jetbrains.annotations.NotNull;

@MSListener
public class NotePlayListener extends AbstractMSListener {
    private static final @NotNull NoteBlockData DEFAULT = NoteBlockData.getDefault();

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onNotePlay(@NotNull NotePlayEvent event) {
        if (
                !(event.getInstrument() == DEFAULT.instrument()
                && event.getNote().equals(DEFAULT.note()))
        ) {
            event.setCancelled(true);
        }
    }
}
