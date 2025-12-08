package dev.neddslayer.sharedhealth.mixin;

import com.mojang.authlib.GameProfile;
import dev.neddslayer.sharedhealth.components.SharedHealthComponent;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static dev.neddslayer.sharedhealth.components.SharedComponentsInitializer.*;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {
    @Shadow public abstract ServerWorld getEntityWorld();

    public ServerPlayerEntityMixin(World world, GameProfile profile) {
        super(world, profile);
    }

    @Inject(method = "damage", at = @At("RETURN"))
    public void damageListener(ServerWorld world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
		// ensure that damage is only taken if the damage listener is handled; you shouldn't be able to punch invulnerable players, etc.
		if (cir.getReturnValue() && this.isAlive()) {
			float currentHealth = this.getHealth();
			SharedHealthComponent component = SHARED_HEALTH.get(this.getEntityWorld().getScoreboard());
			float knownHealth = component.getHealth();
			if (currentHealth != knownHealth) {
				component.setHealth(currentHealth);
			}
		}
    }

    @Inject(method = "onDeath", at = @At("TAIL"))
    public void killEveryoneOnDeath(DamageSource damageSource, CallbackInfo ci) {
        this.getEntityWorld().getServer().getPlayerManager().getPlayerList().forEach(p -> p.kill(this.getEntityWorld()));
        SHARED_HEALTH.get(this.getEntityWorld().getScoreboard()).setHealth(20.0f);
        SHARED_HUNGER.get(this.getEntityWorld().getScoreboard()).setHunger(20);
		SHARED_SATURATION.get(this.getEntityWorld().getScoreboard()).setSaturation(20.0f);
    }
}
