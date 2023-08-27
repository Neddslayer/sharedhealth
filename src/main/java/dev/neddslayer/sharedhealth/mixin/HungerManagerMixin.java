package dev.neddslayer.sharedhealth.mixin;

import dev.neddslayer.sharedhealth.components.SharedHungerComponent;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

import static dev.neddslayer.sharedhealth.components.SharedHealthComponentInitializer.SHARED_HUNGER;

@Mixin(HungerManager.class)
public class HungerManagerMixin {

    @Shadow private int foodLevel;

    @Inject(method = "update", at=@At(value = "FIELD", target = "Lnet/minecraft/entity/player/HungerManager;foodLevel:I", ordinal = 1, shift = At.Shift.BEFORE))
    public void decrease(PlayerEntity player, CallbackInfo ci) {
        if (!player.getWorld().isClient) {
            SharedHungerComponent component = SHARED_HUNGER.get(Objects.requireNonNull(player.getServer()).getScoreboard());
            int hunger = component.getHunger();
            if (this.foodLevel == hunger) {
                component.setHunger(Math.max(this.foodLevel - 1, 0));
            }
        }
    }

}
