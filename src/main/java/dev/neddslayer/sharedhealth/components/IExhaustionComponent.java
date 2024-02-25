package dev.neddslayer.sharedhealth.components;

import dev.onyxstudios.cca.api.v3.component.ComponentV3;

public interface IExhaustionComponent extends ComponentV3 {
	float getExhaustion();
	void setExhaustion(float exhaustion);
}
