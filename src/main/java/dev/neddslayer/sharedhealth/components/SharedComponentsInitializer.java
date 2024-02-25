package dev.neddslayer.sharedhealth.components;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.scoreboard.ScoreboardComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.scoreboard.ScoreboardComponentInitializer;
import net.minecraft.util.Identifier;

public class SharedComponentsInitializer implements ScoreboardComponentInitializer {

    public static final ComponentKey<SharedHealthComponent> SHARED_HEALTH =
            ComponentRegistry.getOrCreate(new Identifier("sharedhealth", "health"), SharedHealthComponent.class);

    public static final ComponentKey<SharedHungerComponent> SHARED_HUNGER =
            ComponentRegistry.getOrCreate(new Identifier("sharedhealth", "hunger"), SharedHungerComponent.class);

	public static final ComponentKey<SharedSaturationComponent> SHARED_SATURATION =
			ComponentRegistry.getOrCreate(new Identifier("sharedhealth", "saturation"), SharedSaturationComponent.class);

	public static final ComponentKey<SharedExhaustionComponent> SHARED_EXHAUSTION =
			ComponentRegistry.getOrCreate(new Identifier("sharedhealth", "exhaustion"), SharedExhaustionComponent.class);

    @Override
    public void registerScoreboardComponentFactories(ScoreboardComponentFactoryRegistry registry) {
        registry.registerScoreboardComponent(SHARED_HEALTH, SharedHealthComponent::new);
        registry.registerScoreboardComponent(SHARED_HUNGER, SharedHungerComponent::new);
		registry.registerScoreboardComponent(SHARED_SATURATION, SharedSaturationComponent::new);
		registry.registerScoreboardComponent(SHARED_EXHAUSTION, SharedExhaustionComponent::new);
    }
}
