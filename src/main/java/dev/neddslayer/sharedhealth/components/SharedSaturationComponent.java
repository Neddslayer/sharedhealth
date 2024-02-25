package dev.neddslayer.sharedhealth.components;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;

public class SharedSaturationComponent implements ISaturationComponent {
	float saturation = 5.0f;
	Scoreboard scoreboard;
	MinecraftServer server;

	public SharedSaturationComponent(Scoreboard scoreboard, MinecraftServer server) {
		this.scoreboard = scoreboard;
		this.server = server;
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
