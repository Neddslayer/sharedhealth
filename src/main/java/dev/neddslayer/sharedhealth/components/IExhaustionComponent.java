package dev.neddslayer.sharedhealth.components;

import dev.onyxstudios.cca.api.v3.component.Component;

public interface IExhaustionComponent extends Component {
	float getExhaustion();
	void setExhaustion(float exhaustion);
}
