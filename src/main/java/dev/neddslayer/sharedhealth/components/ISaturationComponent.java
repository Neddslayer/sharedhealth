package dev.neddslayer.sharedhealth.components;

import dev.onyxstudios.cca.api.v3.component.Component;

public interface ISaturationComponent extends Component {
	float getSaturation();
	void setSaturation(float saturation);
}
