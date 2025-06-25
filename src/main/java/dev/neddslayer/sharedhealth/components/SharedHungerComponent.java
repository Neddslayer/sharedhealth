package dev.neddslayer.sharedhealth.components;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;

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
    public void readData(ReadView readView) {
       this.hunger = readView.getInt("Hunger", 20);  // Default to 20 if not present
    }

    @Override
    public void writeData(WriteView writeView) {
        writeView.putInt("Hunger", this.hunger);
    }
}
