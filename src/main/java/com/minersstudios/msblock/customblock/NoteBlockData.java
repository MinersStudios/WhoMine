package com.minersstudios.msblock.customblock;

import org.bukkit.Instrument;
import org.bukkit.Note;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.NoteBlock;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NoteBlockData implements Cloneable {
    private @NotNull Instrument instrument;
    private @NotNull Note note;
    private boolean powered;

    public NoteBlockData(
            @NotNull Instrument instrument,
            @NotNull Note note,
            boolean powered
    ) {
        this.instrument = instrument;
        this.note = note;
        this.powered = powered;
    }

    public @NotNull Integer toInt() {
        int instrument = this.instrument.ordinal();
        int note = this.note.hashCode();
        int powered = this.powered ? 1 : 0;
        return Integer.parseInt(instrument + "00" + note + "00" + powered);
    }

    public @NotNull NoteBlock craftNoteBlock(@NotNull BlockData blockData) {
        if (!(blockData instanceof NoteBlock noteBlock)) {
            throw new IllegalArgumentException("BlockData must be NoteBlock");
        }

        noteBlock.setInstrument(this.instrument);
        noteBlock.setNote(this.note);
        noteBlock.setPowered(this.powered);
        return noteBlock;
    }

    @Contract("null -> false")
    public boolean isSimilar(@Nullable NoteBlockData noteBlockData) {
        return noteBlockData != null
                && this.instrument == noteBlockData.instrument
                && this.note.equals(noteBlockData.note)
                && this.powered == noteBlockData.powered;
    }

    public void setInstrument(@NotNull Instrument instrument) {
        this.instrument = instrument;
    }

    public @NotNull Instrument getInstrument() {
        return this.instrument;
    }

    public void setNote(@NotNull Note note) {
        this.note = note;
    }

    public @NotNull Note getNote() {
        return this.note;
    }

    public void setPowered(boolean powered) {
        this.powered = powered;
    }

    public boolean isPowered() {
        return this.powered;
    }

    @Override
    public @NotNull NoteBlockData clone() {
        try {
            return (NoteBlockData) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
