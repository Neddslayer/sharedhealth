package dev.neddslayer.sharedhealth.components;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.scores.Scoreboard;

public class SharedExhaustionComponent implements IExhaustionComponent {
	float exhaustion = 0.0f;
	Scoreboard scoreboard;
	MinecraftServer server;

	public SharedExhaustionComponent(Scoreboard scoreboard, MinecraftServer server) {
		this.scoreboard = scoreboard;
		this.server = server;
	}

	@Override
	public float getExhaustion() {
		return this.exhaustion;
	}

	@Override
	public void setExhaustion(float exhaustion) {
		this.exhaustion = exhaustion;
	}

	@Override
	public void readData(ValueInput readView) {
		this.exhaustion = readView.getFloatOr("Exhaustion", 0.0f);
	}

	@Override
	public void writeData(ValueOutput writeView) {
		writeView.putFloat("Exhaustion", this.exhaustion);
	}
}
