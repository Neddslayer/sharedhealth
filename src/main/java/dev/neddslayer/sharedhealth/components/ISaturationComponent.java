package dev.neddslayer.sharedhealth.components;

import dev.onyxstudios.cca.api.v3.component.ComponentV3;

public interface ISaturationComponent extends ComponentV3 {
	float getSaturation();
	void setSaturation(float saturation);
}
