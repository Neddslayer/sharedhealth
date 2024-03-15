package dev.neddslayer.sharedhealth.components;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.scoreboard.Scoreboard;

public class SharedHealthComponent implements IHealthComponent {

    float health = 20;

    Scoreboard scoreboard;

    public SharedHealthComponent(Scoreboard scoreboard) {
        this.scoreboard = scoreboard;
    }

    @Override
    public float getHealth() {
        return this.health;
    }

    @Override
    public void setHealth(float health) {
        this.health = health ;
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        this.health = tag.getFloat("playerHealth");
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putFloat("playerHealth", this.health);
    }
}
