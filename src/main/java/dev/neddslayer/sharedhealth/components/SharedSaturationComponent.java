package dev.neddslayer.sharedhealth.components;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.scoreboard.Scoreboard;

public class SharedSaturationComponent implements ISaturationComponent {
	float saturation = 5.0f;
	Scoreboard scoreboard;

	public SharedSaturationComponent(Scoreboard scoreboard) {
		this.scoreboard = scoreboard;
	}
	@Override
	public float getSaturation() {
		return this.saturation;
	}

	@Override
	public void setSaturation(float saturation) {
		this.saturation = saturation;
	}

	@Override
	public void readFromNbt(NbtCompound tag) {
		this.saturation = tag.getFloat("Saturation");
	}

	@Override
	public void writeToNbt(NbtCompound tag) {
		tag.putFloat("Saturation", this.saturation);
	}
}
