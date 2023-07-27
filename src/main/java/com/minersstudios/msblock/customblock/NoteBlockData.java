package com.minersstudios.msblock.customblock;

import org.bukkit.Instrument;
import org.bukkit.Note;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.NoteBlock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents the data of a note block.
 * Use {@link #craftNoteBlock(BlockData)} to create
 * a {@link NoteBlock} from the note block data.
 */
public class NoteBlockData implements Cloneable {
    private @NotNull Instrument instrument;
    private @NotNull Note note;
    private boolean powered;

    /**
     * Constructs a NoteBlockData with the given instrument and note
     *
     * @param instrument The instrument of the note block
     * @param note       The note of the note block
     * @param powered    True if the note block is powered
     */
    public NoteBlockData(
            @NotNull Instrument instrument,
            @NotNull Note note,
            boolean powered
    ) {
        this.instrument = instrument;
        this.note = note;
        this.powered = powered;
    }

    /**
     * @return The instrument of the note block data
     */
    public @NotNull Instrument getInstrument() {
        return this.instrument;
    }

    /**
     * Sets new instrument of the note block data
     *
     * @param instrument The instrument to set
     */
    public void setInstrument(@NotNull Instrument instrument) {
        this.instrument = instrument;
    }

    /**
     * @return The note of the note block data
     */
    public @NotNull Note getNote() {
        return this.note;
    }

    /**
     * Sets new note of the note block data
     *
     * @param note The note to set
     */
    public void setNote(@NotNull Note note) {
        this.note = note;
    }

    /**
     * Sets new powered state of the note block data
     *
     * @param powered The powered state to set
     */
    public void setPowered(boolean powered) {
        this.powered = powered;
    }

    /**
     * @return Powered state of the note block data
     */
    public boolean isPowered() {
        return this.powered;
    }

    /**
     * Creates a {@link NoteBlock} from the given note block
     *
     * @param blockData The block data to get the note block data from
     * @return The {@link NoteBlock} of the given block data
     * @throws IllegalArgumentException If the given block data is not a {@link NoteBlock}
     */
    public @NotNull NoteBlock craftNoteBlock(@NotNull BlockData blockData) throws IllegalArgumentException {
        if (!(blockData instanceof NoteBlock noteBlock)) {
            throw new IllegalArgumentException("BlockData must be NoteBlock");
        }

        noteBlock.setInstrument(this.instrument);
        noteBlock.setNote(this.note);
        noteBlock.setPowered(this.powered);
        return noteBlock;
    }

    /**
     * Creates a clone of this note block data
     * with the same instrument, note and powered state
     *
     * @return Clone of this note block data
     */
    @Override
    public @NotNull NoteBlockData clone() {
        try {
            return (NoteBlockData) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @return The string representation of the note block data
     */
    @Override
    public @NotNull String toString() {
        return "NoteBlockData{" +
                "instrument=" + this.instrument +
                ", note=" + this.note +
                ", powered=" + this.powered +
                '}';
    }

    /**
     * @return The hash code of the note block data
     */
    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + this.instrument.ordinal();
        result = 31 * result + this.note.hashCode();
        result = 31 * result + (this.powered ? 1 : 0);
        return result;
    }

    /**
     * @param obj The object to compare with
     * @return True if the given object is a note block data
     *         and has the same instrument, note and powered
     *         state
     */
    @Override
    public boolean equals(@Nullable Object obj) {
        return obj instanceof NoteBlockData noteBlockData
                && this.instrument == noteBlockData.instrument
                && this.note == noteBlockData.note
                && this.powered == noteBlockData.powered;
    }
}
