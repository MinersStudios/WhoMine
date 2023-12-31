package com.minersstudios.msblock.api.params;

import org.bukkit.Instrument;
import org.bukkit.Note;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.NoteBlock;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import javax.annotation.concurrent.Immutable;

/**
 * Represents the data of a note block.
 * <br>
 * Use {@link #craftNoteBlock(BlockData)} to create a {@link NoteBlock} from the
 * note block data.
 */
@Immutable
public final class NoteBlockData {
    private final Instrument instrument;
    private final Note note;
    private final boolean powered;

    private static final NoteBlockData DEFAULT = new NoteBlockData(Instrument.BIT, new Note(0), false);
    private static final int PRIME = 31;

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
     *         <ul>
     *             <li>instrument: {@link Instrument#BIT},
     *             <li>note: 0,
     *             <li>powered: false
     *         </ul>
     * @see #DEFAULT
     */
    public static @NotNull NoteBlockData defaultData() {
        return DEFAULT;
    }

    /**
     * Constructs a NoteBlockData with the given instrument, note and powered
     * state
     *
     * @param instrument The instrument of the note block
     * @param note       The note of the note block
     * @param powered    True if the note block is powered
     * @return New note block data
     */
    @Contract("_, _, _ -> new")
    public static @NotNull NoteBlockData from(
            final @NotNull Instrument instrument,
            final @NotNull Note note,
            final boolean powered
    ) {
        return new NoteBlockData(instrument, note, powered);
    }

    /**
     * Constructs a NoteBlockData with the given instrument, note and powered
     * state
     *
     * @param instrument The instrument of the note block
     * @param note       Internal note id, the value has to be in the interval
     *                   [0; 24]
     * @param powered    True if the note block is powered
     * @return New note block data
     */
    @Contract("_, _, _ -> new")
    public static @NotNull NoteBlockData from(
            final @NotNull Instrument instrument,
            final @Range(from = 0, to = 24) int note,
            final boolean powered
    ) {
        return new NoteBlockData(instrument, new Note(note), powered);
    }

    /**
     * Creates new note block data from values of the given note block
     *
     * @param noteBlock The note block to get the note block data from
     * @return New note block data
     */
    @Contract("_ -> new")
    public static @NotNull NoteBlockData from(final @NotNull NoteBlock noteBlock) {
        return new NoteBlockData(noteBlock.getInstrument(), noteBlock.getNote(), noteBlock.isPowered());
    }

    /**
     * @return The instrument of the note block data
     */
    public @NotNull Instrument instrument() {
        return this.instrument;
    }

    /**
     * Sets a new instrument of the note block data
     *
     * @param instrument The instrument to set
     */
    public @NotNull NoteBlockData instrument(final @NotNull Instrument instrument) {
        return new NoteBlockData(instrument, this.note, this.powered);
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
        return new NoteBlockData(this.instrument, note, this.powered);
    }

    /**
     * @return The internal note id of the note block data, the value is in the
     *         interval [0; 24]
     */
    public int noteId() {
        return this.note.hashCode() - PRIME;
    }

    /**
     * Sets new note of the note block data by the internal note id
     *
     * @param note Internal note id, the value has to be in the interval [0; 24]
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
        return new NoteBlockData(this.instrument, this.note, powered);
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
     * @throws IllegalArgumentException If the given block data is not a
     *                                  {@link NoteBlock}
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
     * @return The hash code of the note block data, based on the instrument,
     *         note and powered state
     */
    @Override
    public int hashCode() {
        int result = 1;

        result = PRIME * result + this.instrument.ordinal();
        result = PRIME * result + this.note.hashCode();
        result = PRIME * result + (this.powered ? 1 : 0);

        return result;
    }

    /**
     * @param obj The object to compare with
     * @return True if the given object is {@link NoteBlockData} or
     *         {@link NoteBlock} and has the same instrument, note and powered
     *         state
     */
    @Override
    public boolean equals(final @Nullable Object obj) {
        return obj == this
                || (
                        obj instanceof final NoteBlockData noteBlockData
                        && this.instrument == noteBlockData.instrument
                        && this.note == noteBlockData.note
                        && this.powered == noteBlockData.powered
                )
                || (
                        obj instanceof final NoteBlock noteBlock
                        && this.instrument == noteBlock.getInstrument()
                        && this.note == noteBlock.getNote()
                        && this.powered == noteBlock.isPowered()
                );
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
}
