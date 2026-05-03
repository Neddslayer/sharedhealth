package dev.neddslayer.sharedhealth.mixin;

import dev.neddslayer.sharedhealth.components.SharedHungerComponent;
import dev.neddslayer.sharedhealth.components.SharedSaturationComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

import static dev.neddslayer.sharedhealth.components.SharedComponentsInitializer.SHARED_HUNGER;
import static dev.neddslayer.sharedhealth.components.SharedComponentsInitializer.SHARED_SATURATION;

@Mixin(FoodProperties.class)
public abstract class FoodComponentMixin {

    @Shadow
    public abstract int nutrition();

    @Inject(method = "onConsume", at = @At("HEAD"))
    public void eatFood(final Level level, final LivingEntity user, final ItemStack stack, final Consumable consumable, CallbackInfo ci) {
        if (user instanceof Player player && !level.isClientSide()) {
            SharedHungerComponent hungerComponent = SHARED_HUNGER.get(Objects.requireNonNull(level.getServer()).getScoreboard());
            SharedSaturationComponent saturationComponent = SHARED_SATURATION.get(Objects.requireNonNull(level.getServer()).getScoreboard());
            int hunger = hungerComponent.getHunger();
            float saturation = saturationComponent.getSaturation();
            if (player.getFoodData().getFoodLevel() == hunger) {
                hungerComponent.setHunger(Math.max(player.getFoodData().getFoodLevel() + this.nutrition(), 0));
            }
            if (player.getFoodData().getSaturationLevel() == saturation) {
                saturationComponent.setSaturation(Math.clamp(saturation + this.nutrition(), 0.0f, (float)hungerComponent.getHunger()));
            }
        }
    }

}
