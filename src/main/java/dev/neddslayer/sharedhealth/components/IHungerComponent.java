package dev.neddslayer.sharedhealth.components;

import dev.onyxstudios.cca.api.v3.component.Component;

public interface IHungerComponent extends Component {
    int getHunger();
    void setHunger(int hunger);
}
