package dev.neddslayer.sharedhealth.components;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.scoreboard.Scoreboard;

public class SharedExhaustionComponent implements IExhaustionComponent {
	float exhaustion = 0.0f;
	Scoreboard scoreboard;

	public SharedExhaustionComponent(Scoreboard scoreboard) {
		this.scoreboard = scoreboard;
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
