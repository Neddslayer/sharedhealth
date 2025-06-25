package dev.neddslayer.sharedhealth.components;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;

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
	public void readData(ReadView readView) {
		this.saturation = readView.getFloat("Saturation", 0);
	}

	@Override
	public void writeData(WriteView writeView) {
		writeView.putFloat("Saturation", this.saturation);
	}
}
