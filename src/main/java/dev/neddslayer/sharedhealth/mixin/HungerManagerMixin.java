package dev.neddslayer.sharedhealth.mixin;

import dev.neddslayer.sharedhealth.components.SharedExhaustionComponent;
import dev.neddslayer.sharedhealth.components.SharedHungerComponent;
import dev.neddslayer.sharedhealth.components.SharedSaturationComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.food.FoodData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static dev.neddslayer.sharedhealth.components.SharedComponentsInitializer.*;

@Mixin(FoodData.class)
public class HungerManagerMixin {

    @Shadow private int foodLevel;

	@Shadow private float saturationLevel;

    @Shadow
    private float exhaustionLevel;

    @Shadow
    private int tickTimer;

    // hook into line 46
	@Inject(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/world/food/FoodData;foodLevel:I", ordinal = 1, shift = At.Shift.BEFORE))
    public void decreaseHunger(ServerPlayer player, CallbackInfo ci) {
        if (!player.level().isClientSide()) {
            SharedHungerComponent component = SHARED_HUNGER.get(player.level().getScoreboard());
            int hunger = component.getHunger();
            if (this.foodLevel == hunger) {
                component.setHunger(Math.max(this.foodLevel - 1, 0));
            }
        }
    }

	// hook into line 44
	@Inject(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/world/food/FoodData;saturationLevel:F", ordinal = 0, shift = At.Shift.BEFORE))
	public void decreaseSaturation(ServerPlayer player, CallbackInfo ci) {
		if (!player.level().isClientSide()) {
			SharedSaturationComponent component = SHARED_SATURATION.get(player.level().getScoreboard());
			float saturation = component.getSaturation();
			if (this.saturationLevel == saturation) {
				component.setSaturation(Math.max(this.saturationLevel - 1.0f, 0.0f));
			}
		}
	}

	// hook into line 42
	@Inject(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/world/food/FoodData;exhaustionLevel:F", ordinal = 1))
	public void decreaseExhaustion(ServerPlayer player, CallbackInfo ci) {
		if (!player.level().isClientSide()) {
			SharedExhaustionComponent component = SHARED_EXHAUSTION.get(player.level().getScoreboard());
			float exhaustion = component.getExhaustion();
			if (this.exhaustionLevel == exhaustion) {
				component.setExhaustion(Math.max(this.exhaustionLevel - 4.0f, 0.0f));
			}
		}
	}

	@Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;addExhaustion(F)V"))
	public void hookAddExhaustion(ServerPlayer player, CallbackInfo ci) {
		if (!player.level().isClientSide()) {
			SharedExhaustionComponent exhaustionComponent = SHARED_EXHAUSTION.get(player.level().getScoreboard());
			SharedSaturationComponent saturationComponent = SHARED_SATURATION.get(player.level().getScoreboard());
			float exhaustion = exhaustionComponent.getExhaustion();
			float saturation = saturationComponent.getSaturation();
			if (this.exhaustionLevel == exhaustion) {
				if (this.tickTimer < 80 && this.tickTimer >= 10) {
					float f = Math.min(saturation, 6.0F);
					exhaustionComponent.setExhaustion(Math.max(exhaustion + f, 0.0f));
				} else {
					exhaustionComponent.setExhaustion(Math.max(exhaustion + 6.0f, 0.0f));
				}
			}
		}
	}
}
