package dev.neddslayer.sharedhealth.components;

import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.scoreboard.ScoreboardComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.scoreboard.ScoreboardComponentInitializer;
import net.minecraft.util.Identifier;


public class SharedComponentsInitializer implements ScoreboardComponentInitializer {

    public static Identifier id(String path) {
        return Identifier.of("sharedhealth", path);
    }

    public static final ComponentKey<SharedHealthComponent> SHARED_HEALTH =
            ComponentRegistry.getOrCreate(id("health"), SharedHealthComponent.class);

    public static final ComponentKey<SharedHungerComponent> SHARED_HUNGER =
            ComponentRegistry.getOrCreate(id("hunger"), SharedHungerComponent.class);

    public static final ComponentKey<SharedSaturationComponent> SHARED_SATURATION =
            ComponentRegistry.getOrCreate(id("saturation"), SharedSaturationComponent.class);

    public static final ComponentKey<SharedExhaustionComponent> SHARED_EXHAUSTION =
            ComponentRegistry.getOrCreate(id("exhaustion"), SharedExhaustionComponent.class);

    @Override
    public void registerScoreboardComponentFactories(ScoreboardComponentFactoryRegistry registry) {
        registry.registerScoreboardComponent(SHARED_HEALTH, SharedHealthComponent::new);
        registry.registerScoreboardComponent(SHARED_HUNGER, SharedHungerComponent::new);
		registry.registerScoreboardComponent(SHARED_SATURATION, SharedSaturationComponent::new);
		registry.registerScoreboardComponent(SHARED_EXHAUSTION, SharedExhaustionComponent::new);
    }
}
