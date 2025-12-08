package dev.neddslayer.sharedhealth.mixin;

import dev.neddslayer.sharedhealth.components.SharedHungerComponent;
import dev.neddslayer.sharedhealth.components.SharedSaturationComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

import static dev.neddslayer.sharedhealth.components.SharedComponentsInitializer.*;

@Mixin(StatusEffect.class)
public abstract class SaturationStatusEffectMixin {

	@Shadow public abstract String getTranslationKey();

	@Inject(method = "applyUpdateEffect", at = @At("HEAD"))
	public void applyEffectToComponent(ServerWorld world, LivingEntity entity, int amplifier, CallbackInfoReturnable<Boolean> cir) {
		if (!world.isClient() && entity instanceof PlayerEntity playerEntity && Objects.equals(this.getTranslationKey(), "effect.minecraft.saturation")) {
			SharedHungerComponent hungerComponent = SHARED_HUNGER.get(world.getScoreboard());
			SharedSaturationComponent saturationComponent = SHARED_SATURATION.get(world.getScoreboard());
			int hunger = hungerComponent.getHunger();
			float saturation = saturationComponent.getSaturation();
			if (playerEntity.getHungerManager().getFoodLevel() == hunger) {
				hungerComponent.setHunger(Math.max(playerEntity.getHungerManager().getFoodLevel() + amplifier + 1, 0));
			}
			if (playerEntity.getHungerManager().getSaturationLevel() == saturation) {
				saturationComponent.setSaturation(Math.min(saturation + (float)(amplifier + 1) * 2.0F, (float)hungerComponent.getHunger()));
			}
		}
	}

}
