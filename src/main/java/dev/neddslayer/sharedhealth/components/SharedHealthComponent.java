package dev.neddslayer.sharedhealth.components;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;

public class SharedHealthComponent implements IHealthComponent {

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
    public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        this.health = tag.getFloat("playerHealth").orElse(0.0f);  // Default to 0.0f if not present
    }

    @Override
    public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        tag.putFloat("playerHealth", this.health);
    }
}
