package dev.neddslayer.sharedhealth.mixin;

import dev.neddslayer.sharedhealth.components.SharedHungerComponent;
import dev.neddslayer.sharedhealth.components.SharedSaturationComponent;
import net.minecraft.component.type.ConsumableComponent;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

import static dev.neddslayer.sharedhealth.components.SharedComponentsInitializer.SHARED_HUNGER;
import static dev.neddslayer.sharedhealth.components.SharedComponentsInitializer.SHARED_SATURATION;

@Mixin(FoodComponent.class)
public abstract class FoodComponentMixin {

    @Shadow public abstract int nutrition();

    @Shadow public abstract float saturation();

    @Inject(method = "onConsume", at = @At("HEAD"))
    public void eatFood(World world, LivingEntity user, ItemStack stack, ConsumableComponent consumable, CallbackInfo ci) {
        if (user instanceof PlayerEntity player && !world.isClient()) {
            SharedHungerComponent hungerComponent = SHARED_HUNGER.get(Objects.requireNonNull(world.getServer()).getScoreboard());
            SharedSaturationComponent saturationComponent = SHARED_SATURATION.get(Objects.requireNonNull(world.getServer()).getScoreboard());
            int hunger = hungerComponent.getHunger();
            float saturation = saturationComponent.getSaturation();
            if (player.getHungerManager().getFoodLevel() == hunger) {
                hungerComponent.setHunger(Math.max(player.getHungerManager().getFoodLevel() + this.nutrition(), 0));
            }
            if (player.getHungerManager().getSaturationLevel() == saturation) {
                saturationComponent.setSaturation(Math.clamp(saturation + this.saturation(), 0.0f, (float)hungerComponent.getHunger()));
            }
        }
    }

}
