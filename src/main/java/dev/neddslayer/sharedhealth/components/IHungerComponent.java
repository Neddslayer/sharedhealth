package dev.neddslayer.sharedhealth.components;

import org.ladysnake.cca.api.v3.component.ComponentV3;

public interface IHungerComponent extends ComponentV3 {
    int getHunger();
    void setHunger(int hunger);
}
