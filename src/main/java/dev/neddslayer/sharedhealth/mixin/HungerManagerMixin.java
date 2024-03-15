package dev.neddslayer.sharedhealth.mixin;

import dev.neddslayer.sharedhealth.components.SharedExhaustionComponent;
import dev.neddslayer.sharedhealth.components.SharedHungerComponent;
import dev.neddslayer.sharedhealth.components.SharedSaturationComponent;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

import static dev.neddslayer.sharedhealth.components.SharedComponentsInitializer.*;

@Mixin(HungerManager.class)
public class HungerManagerMixin {

    @Shadow private int foodLevel;

	@Shadow public float exhaustion;

	@Shadow private int foodTickTimer;

	@Shadow private float foodSaturationLevel;

	// hook into line 46
	@Inject(method = "update", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/HungerManager;foodLevel:I", ordinal = 1, shift = At.Shift.BEFORE))
    public void decreaseHunger(PlayerEntity player, CallbackInfo ci) {
        if (!player.getEntityWorld().isClient) {
            SharedHungerComponent component = SHARED_HUNGER.get(Objects.requireNonNull(player.getServer()).getScoreboard());
            int hunger = component.getHunger();
            if (this.foodLevel == hunger) {
                component.setHunger(Math.max(this.foodLevel - 1, 0));
            }
        }
    }

	// hook into line 44
	@Inject(method = "update", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/HungerManager;foodSaturationLevel:F", ordinal = 0, shift = At.Shift.BEFORE))
	public void decreaseSaturation(PlayerEntity player, CallbackInfo ci) {
		if (!player.getEntityWorld().isClient) {
			SharedSaturationComponent component = SHARED_SATURATION.get(Objects.requireNonNull(player.getServer()).getScoreboard());
			float saturation = component.getSaturation();
			if (this.foodSaturationLevel == saturation) {
				component.setSaturation(Math.max(this.foodSaturationLevel - 1.0f, 0.0f));
			}
		}
	}

	// hook into line 42
	@Inject(method = "update", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/HungerManager;exhaustion:F", ordinal = 1))
	public void decreaseExhaustion(PlayerEntity player, CallbackInfo ci) {
		if (!player.getEntityWorld().isClient) {
			SharedExhaustionComponent component = SHARED_EXHAUSTION.get(Objects.requireNonNull(player.getServer()).getScoreboard());
			float exhaustion = component.getExhaustion();
			if (this.exhaustion == exhaustion) {
				component.setExhaustion(Math.max(this.exhaustion - 4.0f, 0.0f));
			}
		}
	}

	@Inject(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/HungerManager;addExhaustion(F)V"))
	public void hookAddExhaustion(PlayerEntity player, CallbackInfo ci) {
		if (!player.getEntityWorld().isClient) {
			SharedExhaustionComponent exhaustionComponent = SHARED_EXHAUSTION.get(Objects.requireNonNull(player.getServer()).getScoreboard());
			SharedSaturationComponent saturationComponent = SHARED_SATURATION.get(Objects.requireNonNull(player.getServer()).getScoreboard());
			float exhaustion = exhaustionComponent.getExhaustion();
			float saturation = saturationComponent.getSaturation();
			if (this.exhaustion == exhaustion) {
				if (this.foodTickTimer < 80 && this.foodTickTimer >= 10) {
					float f = Math.min(saturation, 6.0F);
					exhaustionComponent.setExhaustion(Math.max(exhaustion + f, 0.0f));
				} else {
					exhaustionComponent.setExhaustion(Math.max(exhaustion + 6.0f, 0.0f));
				}
			}
		}
	}
}
