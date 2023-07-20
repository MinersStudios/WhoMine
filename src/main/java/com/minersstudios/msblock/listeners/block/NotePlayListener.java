package com.minersstudios.msblock.listeners.block;

import com.minersstudios.mscore.listener.event.MSListener;
import org.bukkit.Instrument;
import org.bukkit.Note;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import com.minersstudios.mscore.listener.event.AbstractMSListener;
import org.bukkit.event.block.NotePlayEvent;
import org.jetbrains.annotations.NotNull;

@MSListener
public class NotePlayListener extends AbstractMSListener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onNotePlay(@NotNull NotePlayEvent event) {
        if (!(event.getInstrument() == Instrument.BIT && event.getNote().equals(new Note(0)))) {
            event.setCancelled(true);
        }
    }
}
