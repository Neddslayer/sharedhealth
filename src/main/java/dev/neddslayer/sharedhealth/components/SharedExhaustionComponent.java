package dev.neddslayer.sharedhealth.components;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;

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
	public void readData(ReadView readView) {
		this.exhaustion = readView.getFloat("Exhaustion", 0.0f);
	}

	@Override
	public void writeData(WriteView writeView) {
		writeView.putFloat("Exhaustion", this.exhaustion);
	}
}
