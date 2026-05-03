package dev.neddslayer.sharedhealth.mixin;

import dev.neddslayer.sharedhealth.components.SharedExhaustionComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static dev.neddslayer.sharedhealth.components.SharedComponentsInitializer.*;

@Mixin(Player.class)
public abstract class PlayerEntityMixin extends LivingEntity {

    @Shadow
    @Final
    private Abilities abilities;

    @Shadow
    protected FoodData foodData;

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, Level world) {
		super(entityType, world);
	}

	@Inject(method = "causeFoodExhaustion", at = @At("HEAD"))
	public void syncExhaustion(float amount, CallbackInfo ci) {
		if (!this.abilities.invulnerable) {
			if (!this.level().isClientSide()) {
				SharedExhaustionComponent component = SHARED_EXHAUSTION.get(this.level().getScoreboard());
				if (this.foodData.exhaustionLevel == component.getExhaustion()) {
					component.setExhaustion(Math.min(component.getExhaustion() + amount, 40.0F));
				}
			}

		}
	}

}
