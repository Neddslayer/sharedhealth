package dev.neddslayer.sharedhealth.components;

import dev.onyxstudios.cca.api.v3.component.Component;

public interface IHealthComponent extends Component {
    float getHealth();
    void setHealth(float health);
}