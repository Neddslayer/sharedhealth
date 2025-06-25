package dev.neddslayer.sharedhealth.components;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;

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
    public void readData(ReadView readView) {
        this.health = readView.getFloat("playerHealth", 0.0f);  // Default to 0.0f if not present
    }

    @Override
    public void writeData(WriteView writeView) {
        writeView.putFloat("playerHealth", this.health);
    }
}
