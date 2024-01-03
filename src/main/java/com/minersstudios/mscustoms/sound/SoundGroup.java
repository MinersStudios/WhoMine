package com.minersstudios.mscustoms.sound;

import com.minersstudios.mscore.location.MSPosition;
import net.minecraft.world.level.block.SoundType;
import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.concurrent.Immutable;
import java.util.Objects;

import static org.bukkit.SoundCategory.BLOCKS;
import static org.bukkit.SoundCategory.PLAYERS;

/**
 * Represents a group of sounds for a block.
 * <br>
 * A sound group contains 4 sounds :
 * <ul>
 *     <li>Place sound</li>
 *     <li>Break sound</li>
 *     <li>Hit sound</li>
 *     <li>Step sound</li>
 * </ul>
 *
 * Sound group can be created with :
 * <ul>
 *     <li>{@link #create(SoundType)} - for vanilla sounds</li>
 *     <li>{@link #create(Sound, Sound, Sound, Sound)} - for custom sounds</li>
 * </ul>
 *
 * This class also contains all the vanilla sound constants for the blocks
 *
 * @version 1.20.4
 */
@Immutable
public final class SoundGroup {
    private final Sound placeSound;
    private final Sound breakSound;
    private final Sound hitSound;
    private final Sound stepSound;

    //<editor-fold desc="Vanilla sounds" defaultstate="collapsed">
    public static final SoundGroup EMPTY =                    create(SoundType.EMPTY);
    public static final SoundGroup WOOD =                     create(SoundType.WOOD);
    public static final SoundGroup GRAVEL =                   create(SoundType.GRAVEL);
    public static final SoundGroup GRASS =                    create(SoundType.GRASS);
    public static final SoundGroup LILY_PAD =                 create(SoundType.LILY_PAD);
    public static final SoundGroup STONE =                    create(SoundType.STONE);
    public static final SoundGroup METAL =                    create(SoundType.METAL);
    public static final SoundGroup GLASS =                    create(SoundType.GLASS);
    public static final SoundGroup WOOL =                     create(SoundType.WOOL);
    public static final SoundGroup SAND =                     create(SoundType.SAND);
    public static final SoundGroup SNOW =                     create(SoundType.SNOW);
    public static final SoundGroup POWDER_SNOW =              create(SoundType.POWDER_SNOW);
    public static final SoundGroup LADDER =                   create(SoundType.LADDER);
    public static final SoundGroup ANVIL =                    create(SoundType.ANVIL);
    public static final SoundGroup SLIME_BLOCK =              create(SoundType.SLIME_BLOCK);
    public static final SoundGroup HONEY_BLOCK =              create(SoundType.HONEY_BLOCK);
    public static final SoundGroup WET_GRASS =                create(SoundType.WET_GRASS);
    public static final SoundGroup CORAL_BLOCK =              create(SoundType.CORAL_BLOCK);
    public static final SoundGroup BAMBOO =                   create(SoundType.BAMBOO);
    public static final SoundGroup BAMBOO_SAPLING =           create(SoundType.BAMBOO_SAPLING);
    public static final SoundGroup SCAFFOLDING =              create(SoundType.SCAFFOLDING);
    public static final SoundGroup SWEET_BERRY_BUSH =         create(SoundType.SWEET_BERRY_BUSH);
    public static final SoundGroup CROP =                     create(SoundType.CROP);
    public static final SoundGroup HARD_CROP =                create(SoundType.HARD_CROP);
    public static final SoundGroup VINE =                     create(SoundType.VINE);
    public static final SoundGroup NETHER_WART =              create(SoundType.NETHER_WART);
    public static final SoundGroup LANTERN =                  create(SoundType.LANTERN);
    public static final SoundGroup STEM =                     create(SoundType.STEM);
    public static final SoundGroup NYLIUM =                   create(SoundType.NYLIUM);
    public static final SoundGroup FUNGUS =                   create(SoundType.FUNGUS);
    public static final SoundGroup ROOTS =                    create(SoundType.ROOTS);
    public static final SoundGroup SHROOMLIGHT =              create(SoundType.SHROOMLIGHT);
    public static final SoundGroup WEEPING_VINES =            create(SoundType.WEEPING_VINES);
    public static final SoundGroup TWISTING_VINES =           create(SoundType.TWISTING_VINES);
    public static final SoundGroup SOUL_SAND =                create(SoundType.SOUL_SAND);
    public static final SoundGroup SOUL_SOIL =                create(SoundType.SOUL_SOIL);
    public static final SoundGroup BASALT =                   create(SoundType.BASALT);
    public static final SoundGroup WART_BLOCK =               create(SoundType.WART_BLOCK);
    public static final SoundGroup NETHERRACK =               create(SoundType.NETHERRACK);
    public static final SoundGroup NETHER_BRICKS =            create(SoundType.NETHER_BRICKS);
    public static final SoundGroup NETHER_SPROUTS =           create(SoundType.NETHER_SPROUTS);
    public static final SoundGroup NETHER_ORE =               create(SoundType.NETHER_ORE);
    public static final SoundGroup BONE_BLOCK =               create(SoundType.BONE_BLOCK);
    public static final SoundGroup NETHERITE_BLOCK =          create(SoundType.NETHERITE_BLOCK);
    public static final SoundGroup ANCIENT_DEBRIS =           create(SoundType.ANCIENT_DEBRIS);
    public static final SoundGroup LODESTONE =                create(SoundType.LODESTONE);
    public static final SoundGroup CHAIN =                    create(SoundType.CHAIN);
    public static final SoundGroup NETHER_GOLD_ORE =          create(SoundType.NETHER_GOLD_ORE);
    public static final SoundGroup GILDED_BLACKSTONE =        create(SoundType.GILDED_BLACKSTONE);
    public static final SoundGroup CANDLE =                   create(SoundType.CANDLE);
    public static final SoundGroup AMETHYST =                 create(SoundType.AMETHYST);
    public static final SoundGroup AMETHYST_CLUSTER =         create(SoundType.AMETHYST_CLUSTER);
    public static final SoundGroup SMALL_AMETHYST_BUD =       create(SoundType.SMALL_AMETHYST_BUD);
    public static final SoundGroup MEDIUM_AMETHYST_BUD =      create(SoundType.MEDIUM_AMETHYST_BUD);
    public static final SoundGroup LARGE_AMETHYST_BUD =       create(SoundType.LARGE_AMETHYST_BUD);
    public static final SoundGroup TUFF =                     create(SoundType.TUFF);
    public static final SoundGroup TUFF_BRICKS =              create(SoundType.TUFF_BRICKS);
    public static final SoundGroup POLISHED_TUFF =            create(SoundType.POLISHED_TUFF);
    public static final SoundGroup CALCITE =                  create(SoundType.CALCITE);
    public static final SoundGroup DRIPSTONE_BLOCK =          create(SoundType.DRIPSTONE_BLOCK);
    public static final SoundGroup POINTED_DRIPSTONE =        create(SoundType.POINTED_DRIPSTONE);
    public static final SoundGroup COPPER =                   create(SoundType.COPPER);
    public static final SoundGroup COPPER_BULB =              create(SoundType.COPPER_BULB);
    public static final SoundGroup COPPER_GRATE =             create(SoundType.COPPER_GRATE);
    public static final SoundGroup CAVE_VINES =               create(SoundType.CAVE_VINES);
    public static final SoundGroup SPORE_BLOSSOM =            create(SoundType.SPORE_BLOSSOM);
    public static final SoundGroup AZALEA =                   create(SoundType.AZALEA);
    public static final SoundGroup FLOWERING_AZALEA =         create(SoundType.FLOWERING_AZALEA);
    public static final SoundGroup MOSS_CARPET =              create(SoundType.MOSS_CARPET);
    public static final SoundGroup PINK_PETALS =              create(SoundType.PINK_PETALS);
    public static final SoundGroup MOSS =                     create(SoundType.MOSS);
    public static final SoundGroup BIG_DRIPLEAF =             create(SoundType.BIG_DRIPLEAF);
    public static final SoundGroup SMALL_DRIPLEAF =           create(SoundType.SMALL_DRIPLEAF);
    public static final SoundGroup ROOTED_DIRT =              create(SoundType.ROOTED_DIRT);
    public static final SoundGroup HANGING_ROOTS =            create(SoundType.HANGING_ROOTS);
    public static final SoundGroup AZALEA_LEAVES =            create(SoundType.AZALEA_LEAVES);
    public static final SoundGroup SCULK_SENSOR =             create(SoundType.SCULK_SENSOR);
    public static final SoundGroup SCULK_CATALYST =           create(SoundType.SCULK_CATALYST);
    public static final SoundGroup SCULK =                    create(SoundType.SCULK);
    public static final SoundGroup SCULK_VEIN =               create(SoundType.SCULK_VEIN);
    public static final SoundGroup SCULK_SHRIEKER =           create(SoundType.SCULK_SHRIEKER);
    public static final SoundGroup GLOW_LICHEN =              create(SoundType.GLOW_LICHEN);
    public static final SoundGroup DEEPSLATE =                create(SoundType.DEEPSLATE);
    public static final SoundGroup DEEPSLATE_BRICKS =         create(SoundType.DEEPSLATE_BRICKS);
    public static final SoundGroup DEEPSLATE_TILES =          create(SoundType.DEEPSLATE_TILES);
    public static final SoundGroup POLISHED_DEEPSLATE =       create(SoundType.POLISHED_DEEPSLATE);
    public static final SoundGroup FROGLIGHT =                create(SoundType.FROGLIGHT);
    public static final SoundGroup FROGSPAWN =                create(SoundType.FROGSPAWN);
    public static final SoundGroup MANGROVE_ROOTS =           create(SoundType.MANGROVE_ROOTS);
    public static final SoundGroup MUDDY_MANGROVE_ROOTS =     create(SoundType.MUDDY_MANGROVE_ROOTS);
    public static final SoundGroup MUD =                      create(SoundType.MUD);
    public static final SoundGroup MUD_BRICKS =               create(SoundType.MUD_BRICKS);
    public static final SoundGroup PACKED_MUD =               create(SoundType.PACKED_MUD);
    public static final SoundGroup HANGING_SIGN =             create(SoundType.HANGING_SIGN);
    public static final SoundGroup NETHER_WOOD_HANGING_SIGN = create(SoundType.NETHER_WOOD_HANGING_SIGN);
    public static final SoundGroup BAMBOO_WOOD_HANGING_SIGN = create(SoundType.BAMBOO_WOOD_HANGING_SIGN);
    public static final SoundGroup BAMBOO_WOOD =              create(SoundType.BAMBOO_WOOD);
    public static final SoundGroup NETHER_WOOD =              create(SoundType.NETHER_WOOD);
    public static final SoundGroup CHERRY_WOOD =              create(SoundType.CHERRY_WOOD);
    public static final SoundGroup CHERRY_SAPLING =           create(SoundType.CHERRY_SAPLING);
    public static final SoundGroup CHERRY_LEAVES =            create(SoundType.CHERRY_LEAVES);
    public static final SoundGroup CHERRY_WOOD_HANGING_SIGN = create(SoundType.CHERRY_WOOD_HANGING_SIGN);
    public static final SoundGroup CHISELED_BOOKSHELF =       create(SoundType.CHISELED_BOOKSHELF);
    public static final SoundGroup SUSPICIOUS_SAND =          create(SoundType.SUSPICIOUS_SAND);
    public static final SoundGroup SUSPICIOUS_GRAVEL =        create(SoundType.SUSPICIOUS_GRAVEL);
    public static final SoundGroup DECORATED_POT =            create(SoundType.DECORATED_POT);
    public static final SoundGroup DECORATED_POT_CRACKED =    create(SoundType.DECORATED_POT_CRACKED);
    public static final SoundGroup TRIAL_SPAWNER =            create(SoundType.TRIAL_SPAWNER);
    public static final SoundGroup SPONGE =                   create(SoundType.SPONGE);
    public static final SoundGroup WET_SPONGE =               create(SoundType.WET_SPONGE);
    //</editor-fold>

    /**
     * Constructs a SoundGroup with the specified sounds
     *
     * @param placeSound The place sound
     * @param breakSound The break sound
     * @param hitSound   The hit sound
     * @param stepSound  The step sound
     */
    private SoundGroup(
            final @NotNull Sound placeSound,
            final @NotNull Sound breakSound,
            final @NotNull Sound hitSound,
            final @NotNull Sound stepSound
    ) {
        this.placeSound = placeSound;
        this.breakSound = breakSound;
        this.hitSound = hitSound;
        this.stepSound = stepSound;
    }

    /**
     * Creates a new SoundGroup based on the specified SoundType with the
     * {@link SoundCategory#PLAYERS} category for the step sound and the
     * {@link SoundCategory#BLOCKS} category for the other sounds.
     * <br>
     * The key, pitch and volume of the specified SoundType will be used to
     * create the sound instances.
     * <br>
     * Pitch and volume for sounds :
     * <ul>
     *     <li>Place : {@code pitch = specified, volume = specified}</li>
     *     <li>Break : {@code pitch = specified, volume = specified}</li>
     *     <li>Hit : {@code pitch = 0.5f, volume = 0.5f}</li>
     *     <li>Step : {@code pitch = 0.3f, volume = 0.9f}</li>
     * </ul>
     *
     * @param soundType The sound type
     * @return A new SoundGroup created based on the specified SoundType
     */
    @Contract("_ -> new")
    public static @NotNull SoundGroup create(final @NotNull SoundType soundType) {
        final float pitch = soundType.getPitch();
        final float volume = soundType.getVolume();

        return new SoundGroup(
                Sound.create(soundType.getPlaceSound(), BLOCKS, pitch, volume),
                Sound.create(soundType.getBreakSound(), BLOCKS, pitch, volume),
                Sound.create(soundType.getHitSound(), BLOCKS, 0.5f, 0.5f),
                Sound.create(soundType.getStepSound(), PLAYERS, 0.3f, 0.9f)
        );
    }

    /**
     * Creates a new SoundGroup with the specified sounds
     *
     * @param placeSound The place sound
     * @param breakSound The break sound
     * @param hitSound   The hit sound
     * @param stepSound  The step sound
     * @return A new SoundGroup with the specified sounds
     */
    @Contract("_, _, _, _ -> new")
    public static @NotNull SoundGroup create(
            final @NotNull Sound placeSound,
            final @NotNull Sound breakSound,
            final @NotNull Sound hitSound,
            final @NotNull Sound stepSound
    ) {
        return new SoundGroup(
                placeSound,
                breakSound,
                hitSound,
                stepSound
        );
    }

    /**
     * @return The place sound of this SoundGroup, or null if the place sound is
     *         not set
     */
    public @NotNull Sound getPlaceSound() {
        return this.placeSound;
    }

    /**
     * @return The break sound of this SoundGroup, or null if the break sound is
     *         not set
     */
    public @NotNull Sound getBreakSound() {
        return this.breakSound;
    }

    /**
     * @return The hit sound of this SoundGroup, or null if the hit sound is not
     *         set
     */
    public @NotNull Sound getHitSound() {
        return this.hitSound;
    }

    /**
     * @return The step sound of this SoundGroup, or null if the step sound is
     *         not set
     */
    public @NotNull Sound getStepSound() {
        return this.stepSound;
    }

    /**
     * Plays the place sound of this SoundGroup to the specified player from the
     * specified entity. If the place sound is {@link Sound#empty()}, nothing
     * will be played.
     *
     * @param player The player to play the place sound to
     * @param entity The entity source of this sound
     * @see Sound#play(Player, Entity)
     */
    public void playPlaceSound(
            final @NotNull Player player,
            final @NotNull Entity entity
    ) {
        this.placeSound.play(player, entity);
    }

    /**
     * Plays the place sound of this SoundGroup to the specified player at the
     * specified position. If the place sound is {@link Sound#empty()}, nothing
     * will be played.
     *
     * @param player   The player to play the place sound to
     * @param position The position to play the place sound at
     * @see Sound#play(Player, MSPosition)
     */
    public void playPlaceSound(
            final @NotNull Player player,
            final @NotNull MSPosition position
    ) {
        this.placeSound.play(player, position);
    }

    /**
     * Plays the place sound of this SoundGroup to the specified player at the
     * specified location. If the place sound is {@link Sound#empty()}, nothing
     * will be played.
     *
     * @param player   The player to play the place sound to
     * @param location The location to play the place sound at
     * @see Sound#play(Player, Location)
     */
    public void playPlaceSound(
            final @NotNull Player player,
            final @NotNull Location location
    ) {
        this.placeSound.play(player, location);
    }

    /**
     * Plays the place sound of this SoundGroup from the specified entity. If
     * the place sound is {@link Sound#empty()}, nothing will be played.
     *
     * @param entity The entity source of this sound
     * @see Sound#play(Entity)
     */
    public void playPlaceSound(final @NotNull Entity entity) {
        this.placeSound.play(entity);
    }

    /**
     * Plays the place sound of this SoundGroup at the specified position. If
     * the place sound is {@link Sound#empty()}}, nothing will be played.
     *
     * @param position The location to play the place sound at
     * @throws IllegalStateException If the world of the position is null
     * @see Sound#play(MSPosition)
     */
    public void playPlaceSound(final @NotNull MSPosition position) throws IllegalStateException {
        this.placeSound.play(position);
    }

    /**
     * Plays the place sound of this SoundGroup at the specified location. If
     * the place sound is {@link Sound#empty()}}, nothing will be played.
     *
     * @param location The location to play the place sound at
     * @throws IllegalStateException If the world of the location is null
     * @see Sound#play(Location)
     */
    public void playPlaceSound(final @NotNull Location location) throws IllegalStateException {
        this.placeSound.play(location);
    }

    /**
     * Plays the break sound of this SoundGroup to the specified player from the
     * specified entity. If the break sound is {@link Sound#empty()}, nothing
     * will be played.
     *
     * @param player The player to play the break sound to
     * @param entity The entity source of this sound
     * @see Sound#play(Player, Entity)
     */
    public void playBreakSound(
            final @NotNull Player player,
            final @NotNull Entity entity
    ) {
        this.breakSound.play(player, entity);
    }

    /**
     * Plays the break sound of this SoundGroup to the specified player at the
     * specified position. If the break sound is {@link Sound#empty()}, nothing
     * will be played.
     *
     * @param player   The player to play the break sound to
     * @param position The position to play the break sound at
     * @see Sound#play(Player, MSPosition)
     */
    public void playBreakSound(
            final @NotNull Player player,
            final @NotNull MSPosition position
    ) {
        this.breakSound.play(player, position);
    }

    /**
     * Plays the break sound of this SoundGroup to the specified player at the
     * specified location. If the break sound is {@link Sound#empty()}, nothing
     * will be played.
     *
     * @param player   The player to play the break sound to
     * @param location The location to play the break sound at
     * @see Sound#play(Player, Location)
     */
    public void playBreakSound(
            final @NotNull Player player,
            final @NotNull Location location
    ) {
        this.breakSound.play(player, location);
    }

    /**
     * Plays the break sound of this SoundGroup from the specified entity. If
     * the break sound is {@link Sound#empty()}, nothing will be played.
     *
     * @param entity The entity source of this sound
     * @see Sound#play(Entity)
     */
    public void playBreakSound(final @NotNull Entity entity) {
        this.breakSound.play(entity);
    }

    /**
     * Plays the break sound of this SoundGroup at the specified location. If
     * the break sound is {@link Sound#empty()}, nothing will be played.
     *
     * @param position The position to play the break sound at
     * @throws IllegalStateException If the world of the position is null
     * @see Sound#play(MSPosition)
     */
    public void playBreakSound(final @NotNull MSPosition position) throws IllegalStateException {
        this.playBreakSound(position.toLocation());
    }

    /**
     * Plays the break sound of this SoundGroup at the specified location. If
     * the break sound is {@link Sound#empty()}, nothing will be played.
     *
     * @param location The location to play the break sound at
     * @throws IllegalStateException If the world of the location is null
     * @see Sound#play(Location)
     */
    public void playBreakSound(final @NotNull Location location) throws IllegalStateException {
        this.breakSound.play(location);
    }

    /**
     * Plays the hit sound of this SoundGroup to the specified player from the
     * specified entity. If the hit sound is {@link Sound#empty()}, nothing
     * will be played.
     *
     * @param player The player to play the hit sound to
     * @param entity The entity source of this sound
     * @see Sound#play(Player, Entity)
     */
    public void playHitSound(
            final @NotNull Player player,
            final @NotNull Entity entity
    ) {
        this.hitSound.play(player, entity);
    }

    /**
     * Plays the hit sound of this SoundGroup to the specified player at the
     * specified position. If the hit sound is {@link Sound#empty()}, nothing
     * will be played.
     *
     * @param player   The player to play the hit sound to
     * @param position The position to play the hit sound at
     * @see Sound#play(Player, MSPosition)
     */
    public void playHitSound(
            final @NotNull Player player,
            final @NotNull MSPosition position
    ) {
        this.hitSound.play(player, position);
    }

    /**
     * Plays the hit sound of this SoundGroup to the specified player at the
     * specified location. If the hit sound is {@link Sound#empty()}, nothing
     * will be played.
     *
     * @param player   The player to play the hit sound to
     * @param location The location to play the hit sound at
     * @see Sound#play(Player, Location)
     */
    public void playHitSound(
            final @NotNull Player player,
            final @NotNull Location location
    ) {
        this.hitSound.play(player, location);
    }

    /**
     * Plays the hit sound of this SoundGroup from the specified entity. If the
     * hit sound is {@link Sound#empty()}, nothing will be played.
     *
     * @param entity The entity source of this sound
     * @see Sound#play(Entity)
     */
    public void playHitSound(final @NotNull Entity entity) {
        this.hitSound.play(entity);
    }

    /**
     * Plays the hit sound of this SoundGroup at the specified location. If the
     * hit sound is {@link Sound#empty()}, nothing will be played.
     *
     * @param position The position to play the hit sound at
     * @throws IllegalStateException If the world of the position is null
     * @see Sound#play(MSPosition)
     */
    public void playHitSound(final @NotNull MSPosition position) throws IllegalStateException {
        this.playHitSound(position.toLocation());
    }

    /**
     * Plays the hit sound of this SoundGroup at the specified location. If the
     * hit sound is {@link Sound#empty()}, nothing will be played.
     *
     * @param location The location to play the hit sound at
     * @throws IllegalStateException If the world of the location is null
     * @see Sound#play(Location)
     */
    public void playHitSound(final @NotNull Location location) throws IllegalStateException {
        this.hitSound.play(location);
    }

    /**
     * Plays the step sound of this SoundGroup to the specified player from the
     * specified entity. If the step sound is {@link Sound#empty()}, nothing
     * will be played.
     *
     * @param player The player to play the step sound to
     * @param entity The entity source of this sound
     * @see Sound#play(Player, Entity)
     */
    public void playStepSound(
            final @NotNull Player player,
            final @NotNull Entity entity
    ) {
        this.stepSound.play(player, entity);
    }

    /**
     * Plays the step sound of this SoundGroup to the specified player at the
     * specified position. If the step sound is {@link Sound#empty()}, nothing
     * will be played.
     *
     * @param player   The player to play the step sound to
     * @param position The position to play the step sound at
     * @see Sound#play(Player, MSPosition)
     */
    public void playStepSound(
            final @NotNull Player player,
            final @NotNull MSPosition position
    ) {
        this.stepSound.play(player, position);
    }

    /**
     * Plays the step sound of this SoundGroup to the specified player at the
     * specified location. If the step sound is {@link Sound#empty()}, nothing
     * will be played.
     *
     * @param player   The player to play the step sound to
     * @param location The location to play the step sound at
     * @see Sound#play(Player, Location)
     */
    public void playStepSound(
            final @NotNull Player player,
            final @NotNull Location location
    ) {
        this.stepSound.play(player, location);
    }

    /**
     * Plays the step sound of this SoundGroup from the specified entity. If the
     * step sound is {@link Sound#empty()}, nothing will be played.
     *
     * @param entity The entity source of this sound
     * @see Sound#play(Entity)
     */
    public void playStepSound(final @NotNull Entity entity) {
        this.stepSound.play(entity);
    }

    /**
     * Plays the step sound of this SoundGroup at the specified location. If the
     * step sound is {@link Sound#empty()}, nothing will be played.
     *
     * @param position The position to play the step sound at
     * @throws IllegalStateException If the world of the position is null
     * @see Sound#play(MSPosition)
     */
    public void playStepSound(final @NotNull MSPosition position) throws IllegalStateException {
        this.playStepSound(position.toLocation());
    }

    /**
     * Plays the step sound of this SoundGroup at the specified location. If the
     * step sound is {@link Sound#empty()}, nothing will be played.
     *
     * @param location The location to play the step sound at
     * @throws IllegalStateException If the world of the location is null
     * @see Sound#play(Location)
     */
    public void playStepSound(final @NotNull Location location) throws IllegalStateException {
        this.stepSound.play(location);
    }

    /**
     * @return A hash code value for this SoundGroup
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;

        result = prime * result + this.placeSound.hashCode();
        result = prime * result + this.breakSound.hashCode();
        result = prime * result + this.hitSound.hashCode();
        result = prime * result + this.stepSound.hashCode();

        return result;
    }

    /**
     * @param obj The reference object with which to compare
     * @return True if this SoundGroup is the same as the obj argument
     */
    @Override
    public boolean equals(final @Nullable Object obj) {
        return this == obj
                || (
                        obj instanceof final SoundGroup that
                        && Objects.equals(this.placeSound, that.placeSound)
                        && Objects.equals(this.breakSound, that.breakSound)
                        && Objects.equals(this.hitSound, that.hitSound)
                        && Objects.equals(this.stepSound, that.stepSound)
                );
    }

    /**
     * @return A string representation of this SoundGroup
     */
    @Override
    public @NotNull String toString() {
        return "SoundGroup{" +
                "placeSound=" + this.placeSound +
                ", breakSound=" + this.breakSound +
                ", hitSound=" + this.hitSound +
                ", stepSound=" + this.stepSound +
                '}';
    }
}
