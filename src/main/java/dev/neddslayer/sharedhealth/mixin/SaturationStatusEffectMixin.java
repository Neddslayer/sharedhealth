package dev.neddslayer.sharedhealth.mixin;

import dev.neddslayer.sharedhealth.components.SharedHungerComponent;
import dev.neddslayer.sharedhealth.components.SharedSaturationComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

import static dev.neddslayer.sharedhealth.components.SharedComponentsInitializer.*;

@Mixin(MobEffect.class)
public abstract class SaturationStatusEffectMixin {

    @Shadow
    public abstract String getDescriptionId();

    @Inject(method = "applyEffectTick", at = @At("HEAD"))
	public void applyEffectToComponent(ServerLevel world, LivingEntity entity, int amplifier, CallbackInfoReturnable<Boolean> cir) {
		if (!world.isClientSide() && entity instanceof Player playerEntity && Objects.equals(this.getDescriptionId(), "effect.minecraft.saturation")) {
			SharedHungerComponent hungerComponent = SHARED_HUNGER.get(world.getScoreboard());
			SharedSaturationComponent saturationComponent = SHARED_SATURATION.get(world.getScoreboard());
			int hunger = hungerComponent.getHunger();
			float saturation = saturationComponent.getSaturation();
			if (playerEntity.getFoodData().getFoodLevel() == hunger) {
				hungerComponent.setHunger(Math.max(playerEntity.getFoodData().getFoodLevel() + amplifier + 1, 0));
			}
			if (playerEntity.getFoodData().getSaturationLevel() == saturation) {
				saturationComponent.setSaturation(Math.min(saturation + (float)(amplifier + 1) * 2.0F, (float)hungerComponent.getHunger()));
			}
		}
	}

}
