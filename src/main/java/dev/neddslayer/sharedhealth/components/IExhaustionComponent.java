package dev.neddslayer.sharedhealth.components;

import org.ladysnake.cca.api.v3.component.ComponentV3;

public interface IExhaustionComponent extends ComponentV3 {
	float getExhaustion();
	void setExhaustion(float exhaustion);
}
