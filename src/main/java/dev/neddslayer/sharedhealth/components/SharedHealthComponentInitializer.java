package dev.neddslayer.sharedhealth.components;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.scoreboard.ScoreboardComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.scoreboard.ScoreboardComponentInitializer;
import net.minecraft.util.Identifier;

public class SharedHealthComponentInitializer implements ScoreboardComponentInitializer {

    public static final ComponentKey<SharedHealthComponent> SHARED_HEALTH =
            ComponentRegistry.getOrCreate(new Identifier("sharedhealth", "health"), SharedHealthComponent.class);

    @Override
    public void registerScoreboardComponentFactories(ScoreboardComponentFactoryRegistry registry) {
        registry.registerScoreboardComponent(SHARED_HEALTH, SharedHealthComponent::new);
    }
}
