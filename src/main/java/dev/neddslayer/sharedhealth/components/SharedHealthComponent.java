package dev.neddslayer.sharedhealth.components;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.scores.Scoreboard;

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
    public void readData(ValueInput readView) {
        this.health = readView.getFloatOr("playerHealth", 20.0f);  // Default to 20.0f if not present
    }

    @Override
    public void writeData(ValueOutput writeView) {
        writeView.putFloat("playerHealth", this.health);
    }
}
