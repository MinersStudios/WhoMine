package com.github.minersstudios.msdecor.customdecor;

import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.security.SecureRandom;

@SuppressWarnings("unused")
public class SoundGroup {
	private @Nullable String placeSound;
	private float placeSoundPitch;
	private float placeSoundVolume;
	private @Nullable String breakSound;
	private float breakSoundPitch;
	private float breakSoundVolume;

	private final SecureRandom random = new SecureRandom();

	public SoundGroup(
			@Nullable String placeSound,
			float placeSoundPitch,
			float placeSoundVolume,
			@Nullable String breakSound,
			float breakSoundPitch,
			float breakSoundVolume
	) {
		this.placeSound = placeSound;
		this.placeSoundPitch = placeSoundPitch;
		this.placeSoundVolume = placeSoundVolume;
		this.breakSound = breakSound;
		this.breakSoundPitch = breakSoundPitch;
		this.breakSoundVolume = breakSoundVolume;
	}

	public void setPlaceSound(@Nullable String placeSound) {
		this.placeSound = placeSound;
	}

	public @Nullable String getPlaceSound() {
		return this.placeSound;
	}

	public void setPlaceSoundPitch(float placeSoundPitch) {
		this.placeSoundPitch = placeSoundPitch;
	}

	public float getPlaceSoundPitch() {
		return this.placeSoundPitch;
	}

	public void setPlaceSoundVolume(float placeSoundVolume) {
		this.placeSoundVolume = placeSoundVolume;
	}

	public float getPlaceSoundVolume() {
		return this.placeSoundVolume;
	}

	public void setBreakSound(@Nullable String breakSound) {
		this.breakSound = breakSound;
	}

	public @Nullable String getBreakSound() {
		return this.breakSound;
	}

	public void setBreakSoundPitch(float breakSoundPitch) {
		this.breakSoundPitch = breakSoundPitch;
	}

	public float getBreakSoundPitch() {
		return this.breakSoundPitch;
	}

	public void setBreakSoundVolume(float breakSoundVolume) {
		this.breakSoundVolume = breakSoundVolume;
	}

	public float getBreakSoundVolume() {
		return this.breakSoundVolume;
	}

	public void playPlaceSound(@NotNull Location location) {
		if (this.placeSound == null) return;
		location.getWorld().playSound(
				location,
				this.placeSound,
				SoundCategory.BLOCKS,
				this.placeSoundVolume,
				this.random.nextFloat() * 0.1f + this.placeSoundPitch
		);
	}

	public void playBreakSound(@NotNull Location location) {
		if (this.breakSound == null) return;
		location.getWorld().playSound(
				location,
				this.breakSound,
				SoundCategory.BLOCKS,
				this.breakSoundVolume,
				this.random.nextFloat() * 0.1f + this.breakSoundPitch
		);
	}
}
