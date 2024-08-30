package dev.neddslayer.sharedhealth.mixin;

import dev.neddslayer.sharedhealth.components.SharedExhaustionComponent;
import dev.neddslayer.sharedhealth.components.SharedHungerComponent;
import dev.neddslayer.sharedhealth.components.SharedSaturationComponent;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

import static dev.neddslayer.sharedhealth.components.SharedComponentsInitializer.*;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {
	@Shadow public abstract HungerManager getHungerManager();

	@Shadow @Final private PlayerAbilities abilities;

	@Shadow protected HungerManager hungerManager;

	protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
		super(entityType, world);
	}

	@Inject(method = "eatFood", at = @At("HEAD"))
	public void eatFood(World world, ItemStack stack, FoodComponent foodComponent, CallbackInfoReturnable<ItemStack> cir) {
		if (!world.isClient && stack.getItem().getComponents().contains(DataComponentTypes.FOOD)) {
			SharedHungerComponent hungerComponent = SHARED_HUNGER.get(Objects.requireNonNull(this.getServer()).getScoreboard());
			SharedSaturationComponent saturationComponent = SHARED_SATURATION.get(Objects.requireNonNull(this.getServer()).getScoreboard());
			int hunger = hungerComponent.getHunger();
			float saturation = saturationComponent.getSaturation();
			if (this.getHungerManager().getFoodLevel() == hunger && foodComponent != null) {
				hungerComponent.setHunger(Math.max(this.getHungerManager().getFoodLevel() + foodComponent.nutrition(), 0));
			}
			if (this.getHungerManager().getSaturationLevel() == saturation && foodComponent != null) {
				saturationComponent.setSaturation(Math.min(saturation + (float)foodComponent.nutrition() * foodComponent.saturation() * 2.0F, (float)hungerComponent.getHunger()));
			}
		}
	}

	@Inject(method = "addExhaustion", at = @At("HEAD"))
	public void syncExhaustion(float exhaustion, CallbackInfo ci) {
		if (!this.abilities.invulnerable) {
			if (!this.getWorld().isClient) {
				SharedExhaustionComponent component = SHARED_EXHAUSTION.get(Objects.requireNonNull(this.getServer()).getScoreboard());
				if (this.hungerManager.getExhaustion() == component.getExhaustion()) {
					component.setExhaustion(Math.min(component.getExhaustion() + exhaustion, 40.0F));
				}
			}

		}
	}
}
