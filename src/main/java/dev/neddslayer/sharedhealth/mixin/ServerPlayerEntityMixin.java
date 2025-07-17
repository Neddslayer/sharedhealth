package dev.neddslayer.sharedhealth.mixin;

import com.mojang.authlib.GameProfile;
import dev.neddslayer.sharedhealth.components.SharedHealthComponent;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
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
    @Shadow public abstract ServerWorld getWorld();

    public ServerPlayerEntityMixin(World world, GameProfile profile) {
        super(world, profile);
    }

    @Inject(method = "damage", at = @At("RETURN"))
    public void damageListener(ServerWorld world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
		// ensure that damage is only taken if the damage listener is handled; you shouldn't be able to punch invulnerable players, etc.
		if (cir.getReturnValue() && this.isAlive()) {
			float currentHealth = this.getHealth();
			SharedHealthComponent component = SHARED_HEALTH.get(this.getScoreboard());
			float knownHealth = component.getHealth();
			if (currentHealth != knownHealth) {
				component.setHealth(currentHealth);
			}
		}
    }

    @Inject(method = "onDeath", at = @At("TAIL"))
    public void killEveryoneOnDeath(DamageSource damageSource, CallbackInfo ci) {
        this.getServer().getPlayerManager().getPlayerList().forEach(p -> p.kill(this.getWorld()));
        SHARED_HEALTH.get(this.getScoreboard()).setHealth(20.0f);
        SHARED_HUNGER.get(this.getScoreboard()).setHunger(20);
		SHARED_SATURATION.get(this.getScoreboard()).setSaturation(20.0f);
    }
}
