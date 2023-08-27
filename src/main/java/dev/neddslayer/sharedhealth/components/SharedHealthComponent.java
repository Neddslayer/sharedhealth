package dev.neddslayer.sharedhealth.components;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;

public class SharedHealthComponent implements ISharedComponent {

    float health = 20;

    Scoreboard scoreboard;
    MinecraftServer server;

    public SharedHealthComponent(Scoreboard scoreboard, MinecraftServer server) {
        this.scoreboard = scoreboard;
        this.server = server;
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
