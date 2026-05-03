package dev.neddslayer.sharedhealth.components;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.scores.Scoreboard;

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
	public void readData(ValueInput readView) {
		this.saturation = readView.getFloatOr("Saturation", 5.0f);
	}

	@Override
	public void writeData(ValueOutput writeView) {
		writeView.putFloat("Saturation", this.saturation);
	}
}
