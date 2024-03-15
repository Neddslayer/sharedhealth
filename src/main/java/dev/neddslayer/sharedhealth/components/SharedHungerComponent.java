package dev.neddslayer.sharedhealth.components;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.scoreboard.Scoreboard;

public class SharedHungerComponent implements IHungerComponent {

    int hunger = 20;
    Scoreboard scoreboard;

    public SharedHungerComponent(Scoreboard scoreboard) {
        this.scoreboard = scoreboard;
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
    public void readFromNbt(NbtCompound tag) {
        this.hunger = tag.getInt("Hunger");
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putInt("Hunger", this.hunger);
    }
}
