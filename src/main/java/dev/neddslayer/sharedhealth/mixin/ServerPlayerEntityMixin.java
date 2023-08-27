package dev.neddslayer.sharedhealth.mixin;

import com.mojang.authlib.GameProfile;
import dev.neddslayer.sharedhealth.components.SharedHealthComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static dev.neddslayer.sharedhealth.components.SharedHealthComponentInitializer.SHARED_HEALTH;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {
    @Shadow @Final public MinecraftServer server;

    @Shadow public abstract boolean damage(DamageSource source, float amount);

    @Shadow public abstract ServerWorld getServerWorld();

    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Inject(method = "damage", at = @At("HEAD"))
    public void damageListener(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        float currentHealth = this.getHealth();
        SharedHealthComponent component = SHARED_HEALTH.get(this.getScoreboard());
        float knownHealth = component.getHealth();
        if (currentHealth == knownHealth) {
            component.setHealth(knownHealth - amount);
        }
    }

    @Inject(method = "onDeath", at = @At("HEAD"))
    public void killEveryoneOnDeath(DamageSource damageSource, CallbackInfo ci) {
        System.out.println("whuh?");
        this.getServerWorld().getPlayers().forEach(player -> player.damage(damageSource, Float.MAX_VALUE));
        SHARED_HEALTH.get(this.getScoreboard()).setHealth(20.0f);
    }
}
