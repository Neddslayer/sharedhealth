package dev.neddslayer.sharedhealth.mixin;

import dev.neddslayer.sharedhealth.components.SharedHealthComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static dev.neddslayer.sharedhealth.components.SharedComponentsInitializer.SHARED_HEALTH;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    @Shadow public abstract boolean isAlive();

    public LivingEntityMixin(EntityType<?> type, Level world) {
        super(type, world);
    }

    @Inject(method = "heal", at=@At("HEAD"))
    public void healListener(float amount, CallbackInfo ci) {
        if ((LivingEntity) (Object) this instanceof ServerPlayer player && this.isAlive()) {
            float currentHealth = player.getHealth();
            SharedHealthComponent component = SHARED_HEALTH.get(player.level().getScoreboard());
            float knownHealth = component.getHealth();
            if (currentHealth == knownHealth) {
                component.setHealth(knownHealth + amount);
            }
        }
    }
}
