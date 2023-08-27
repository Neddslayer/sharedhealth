package dev.neddslayer.sharedhealth.mixin;

import dev.neddslayer.sharedhealth.components.SharedHungerComponent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

import static dev.neddslayer.sharedhealth.components.SharedHealthComponentInitializer.SHARED_HUNGER;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {
    @Shadow public abstract HungerManager getHungerManager();

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "eatFood", at = @At("HEAD"))
    public void eatFood(World world, ItemStack stack, CallbackInfoReturnable<ItemStack> cir) {
        if (!world.isClient && stack.getItem().isFood()) {
            FoodComponent foodComponent = stack.getItem().getFoodComponent();
            SharedHungerComponent component = SHARED_HUNGER.get(Objects.requireNonNull(this.getServer()).getScoreboard());
            int hunger = component.getHunger();
            if (this.getHungerManager().getFoodLevel() == hunger && foodComponent != null) {
                component.setHunger(Math.max(this.getHungerManager().getFoodLevel() + foodComponent.getHunger(), 0));
            }
        }
    }
}
