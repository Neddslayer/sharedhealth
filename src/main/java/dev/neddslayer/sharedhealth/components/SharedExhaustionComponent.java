package dev.neddslayer.sharedhealth.components;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
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
	public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
		this.exhaustion = tag.getFloat("Exhaustion").orElse(0.0f);  // Default to 0.0f if not present

	}

	@Override
	public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
		tag.putFloat("Exhaustion", this.exhaustion);
	}
}
