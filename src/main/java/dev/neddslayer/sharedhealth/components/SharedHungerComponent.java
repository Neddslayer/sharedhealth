package dev.neddslayer.sharedhealth.components;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.scores.Scoreboard;

public class SharedHungerComponent implements IHungerComponent {

    int hunger = 20;
    Scoreboard scoreboard;
    MinecraftServer server;

    public SharedHungerComponent(Scoreboard scoreboard, MinecraftServer server) {
        this.scoreboard = scoreboard;
        this.server = server;
    }


    @Override
    public int getHunger() {
        return this.hunger;
    }

    @Override
    public void setHunger(int hunger) {
        this.hunger = hunger;
    }

    @Override
    public void readData(ValueInput readView) {
       this.hunger = readView.getIntOr("Hunger", 20);  // Default to 20 if not present
    }

    @Override
    public void writeData(ValueOutput writeView) {
        writeView.putInt("Hunger", this.hunger);
    }
}
