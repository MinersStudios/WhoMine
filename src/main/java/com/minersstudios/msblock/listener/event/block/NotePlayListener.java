package com.minersstudios.msblock.listener.event.block;

import com.minersstudios.msblock.MSBlock;
import com.minersstudios.msblock.api.file.NoteBlockData;
import com.minersstudios.mscore.listener.api.event.AbstractEventListener;
import com.minersstudios.mscore.listener.api.event.EventListener;
import org.bukkit.Instrument;
import org.bukkit.Note;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.NotePlayEvent;
import org.jetbrains.annotations.NotNull;

@EventListener
public final class NotePlayListener extends AbstractEventListener<MSBlock> {
    private static final @NotNull Instrument DEFAULT_INSTRUMENT = NoteBlockData.defaultData().instrument();
    private static final @NotNull Note DEFAULT_NOTE = NoteBlockData.defaultData().note();

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onNotePlay(final @NotNull NotePlayEvent event) {
        if (
                !(event.getInstrument() == DEFAULT_INSTRUMENT
                && event.getNote().equals(DEFAULT_NOTE))
        ) {
            event.setCancelled(true);
        }
    }
}
