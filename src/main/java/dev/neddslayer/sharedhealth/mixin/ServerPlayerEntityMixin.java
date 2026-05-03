package dev.neddslayer.sharedhealth.mixin;

import com.mojang.authlib.GameProfile;
import dev.neddslayer.sharedhealth.components.SharedHealthComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static dev.neddslayer.sharedhealth.components.SharedComponentsInitializer.*;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerEntityMixin extends Player {

    @Shadow
    public abstract ServerLevel level();

    public ServerPlayerEntityMixin(Level world, GameProfile profile) {
        super(world, profile);
    }

    @Inject(method = "hurtServer", at = @At("RETURN"))
    public void damageListener(ServerLevel world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
		// ensure that damage is only taken if the damage listener is handled; you shouldn't be able to punch invulnerable players, etc.
		if (cir.getReturnValue() && this.isAlive()) {
			float currentHealth = this.getHealth();
			SharedHealthComponent component = SHARED_HEALTH.get(this.level().getScoreboard());
			float knownHealth = component.getHealth();
			if (currentHealth != knownHealth) {
				component.setHealth(currentHealth);
			}
		}

    }

    @Inject(method = "die", at = @At("TAIL"))
    public void killEveryoneOnDeath(DamageSource damageSource, CallbackInfo ci) {
        this.level().getServer().getPlayerList().getPlayers().forEach(p -> p.kill(this.level()));
        SHARED_HEALTH.get(this.level().getScoreboard()).setHealth(20.0f);
        SHARED_HUNGER.get(this.level().getScoreboard()).setHunger(20);
		SHARED_SATURATION.get(this.level().getScoreboard()).setSaturation(20.0f);
    }
}
