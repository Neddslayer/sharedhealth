package dev.neddslayer.sharedhealth.mixin;

import dev.neddslayer.sharedhealth.components.SharedExhaustionComponent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static dev.neddslayer.sharedhealth.components.SharedComponentsInitializer.*;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {

	@Shadow @Final private PlayerAbilities abilities;

	@Shadow protected HungerManager hungerManager;

	protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
		super(entityType, world);
	}

	@Inject(method = "addExhaustion", at = @At("HEAD"))
	public void syncExhaustion(float exhaustion, CallbackInfo ci) {
		if (!this.abilities.invulnerable) {
			if (!this.getEntityWorld().isClient()) {
				SharedExhaustionComponent component = SHARED_EXHAUSTION.get(this.getEntityWorld().getScoreboard());
				if (this.hungerManager.exhaustion == component.getExhaustion()) {
					component.setExhaustion(Math.min(component.getExhaustion() + exhaustion, 40.0F));
				}
			}

		}
	}

}
