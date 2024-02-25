package dev.neddslayer.sharedhealth.components;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;

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
	public void readFromNbt(NbtCompound tag) {
		this.exhaustion = tag.getFloat("Exhaustion");
	}

	@Override
	public void writeToNbt(NbtCompound tag) {
		tag.putFloat("Exhaustion", this.exhaustion);
	}
}
