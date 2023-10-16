package com.minersstudios.msblock.customblock.file;

import org.bukkit.Instrument;
import org.bukkit.Note;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.NoteBlock;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

/**
 * Represents the data of a note block.
 * <p>
 * Use {@link #craftNoteBlock(BlockData)} to create
 * a {@link NoteBlock} from the note block data.
 */
public class NoteBlockData implements Cloneable {
    private Instrument instrument;
    private Note note;
    private boolean powered;

    private static final NoteBlockData DEFAULT = new NoteBlockData(Instrument.BIT, new Note(0), false);

    private NoteBlockData(
            final @NotNull Instrument instrument,
            final @NotNull Note note,
            final boolean powered
    ) {
        this.instrument = instrument;
        this.note = note;
        this.powered = powered;
    }

    /**
     * @return Default note block data, with the following values:
     *     <p> - instrument: {@link Instrument#BIT},
     *     <p> - note: 0,
     *     <p> - powered: false
     * @see #DEFAULT
     */
    public static @NotNull NoteBlockData defaultData() {
        return DEFAULT;
    }

    /**
     * Constructs a NoteBlockData with the given instrument, note
     * and powered state
     *
     * @param instrument The instrument of the note block
     * @param note       The note of the note block
     * @param powered    True if the note block is powered
     * @return New note block data
     */
    @Contract(value = "_, _, _ -> new", pure = true)
    public static @NotNull NoteBlockData fromParams(
            final @NotNull Instrument instrument,
            final @NotNull Note note,
            final boolean powered
    ) {
        return new NoteBlockData(instrument, note, powered);
    }

    /**
     * Constructs a NoteBlockData with the given instrument, note
     * and powered state
     *
     * @param instrument The instrument of the note block
     * @param note       Internal note id,
     *                   the value has to be in the interval [0; 24]
     * @param powered    True if the note block is powered
     * @return New note block data
     */
    @Contract("_, _, _ -> new")
    public static @NotNull NoteBlockData fromParams(
            final @NotNull Instrument instrument,
            final @Range(from = 0, to = 24) int note,
            final boolean powered
    ) {
        return new NoteBlockData(instrument, new Note(note), powered);
    }

    /**
     * Creates a new note block data from values of the given note block
     *
     * @param noteBlock The note block to get the note block data from
     * @return New note block data
     */
    @Contract("_ -> new")
    public static @NotNull NoteBlockData fromNoteBlock(final @NotNull NoteBlock noteBlock) {
        return new NoteBlockData(noteBlock.getInstrument(), noteBlock.getNote(), noteBlock.isPowered());
    }

    /**
     * @return The instrument of the note block data
     */
    public @NotNull Instrument instrument() {
        return this.instrument;
    }

    /**
     * Sets new instrument of the note block data
     *
     * @param instrument The instrument to set
     */
    public @NotNull NoteBlockData instrument(final @NotNull Instrument instrument) {
        this.instrument = instrument;
        return this;
    }

    /**
     * @return The note of the note block data
     */
    public @NotNull Note note() {
        return this.note;
    }

    /**
     * Sets new note of the note block data
     *
     * @param note The note to set
     * @return The note block data with the new note
     */
    public @NotNull NoteBlockData note(final @NotNull Note note) {
        this.note = note;
        return this;
    }

    /**
     * @return The internal note id of the note block data,
     *         the value is in the interval [0; 24]
     */
    public int noteId() {
        final int prime = 31;
        return this.note.hashCode() - prime;
    }

    /**
     * Sets new note of the note block data by the internal note id
     *
     * @param note Internal note id,
     *             the value has to be in the interval [0; 24]
     * @return The note block data with the new note
     * @see #note(Note)
     */
    public @NotNull NoteBlockData noteId(final @Range(from = 0, to = 24) int note) {
        return this.note(new Note(note));
    }

    /**
     * @return Powered state of the note block data
     */
    public boolean powered() {
        return this.powered;
    }

    /**
     * Sets new powered state of the note block data
     *
     * @param powered The powered state to set
     * @return The note block data with the new powered state
     */
    public @NotNull NoteBlockData powered(final boolean powered) {
        this.powered = powered;
        return this;
    }

    /**
     * @return True if the note block data is default
     * @see #defaultData()
     */
    public boolean isDefault() {
        return this.equals(DEFAULT);
    }

    /**
     * Creates a {@link NoteBlock} from the given note block
     *
     * @param blockData The block data to get the note block data from
     * @return The {@link NoteBlock} of the given block data
     * @throws IllegalArgumentException If the given block data is not a {@link NoteBlock}
     */
    public @NotNull NoteBlock craftNoteBlock(final @NotNull BlockData blockData) throws IllegalArgumentException {
        if (!(blockData instanceof final NoteBlock noteBlock)) {
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
        } catch (final CloneNotSupportedException e) {
            throw new AssertionError("An error occurred while cloning '" + this + "'", e);
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
     * @return The hash code of the note block data,
     *         based on the instrument, note and powered state
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;

        result = prime * result + this.instrument.ordinal();
        result = prime * result + this.note.hashCode();
        result = prime * result + (this.powered ? 1 : 0);

        return result;
    }

    /**
     * @param obj The object to compare with
     * @return True if the given object is {@link NoteBlockData}
     *         or {@link NoteBlock} and has the same instrument,
     *         note and powered state
     */
    @Override
    public boolean equals(final @Nullable Object obj) {
        if (obj == this) return true;

        if (obj instanceof final NoteBlockData noteBlockData) {
            return this.instrument == noteBlockData.instrument
                    && this.note == noteBlockData.note
                    && this.powered == noteBlockData.powered;
        }

        if (obj instanceof final NoteBlock noteBlock) {
            return this.instrument == noteBlock.getInstrument()
                    && this.note == noteBlock.getNote()
                    && this.powered == noteBlock.isPowered();
        }

        return false;
    }
}
