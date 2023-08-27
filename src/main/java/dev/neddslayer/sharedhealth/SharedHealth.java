package dev.neddslayer.sharedhealth;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.neddslayer.sharedhealth.components.SharedHealthComponent;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerLoginConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameRules;

import java.rmi.server.ExportException;

import static dev.neddslayer.sharedhealth.components.SharedHealthComponentInitializer.SHARED_HEALTH;

public class SharedHealth implements ModInitializer {

    public static final GameRules.Key<GameRules.BooleanRule> SYNC_HEALTH =
            GameRuleRegistry.register("syncHealth", GameRules.Category.MOBS, GameRuleFactory.createBooleanRule(true));
    private static boolean lastValue = true;

    /**
     * Runs the mod initializer.
     */
    @Override
    public void onInitialize() {
        ServerTickEvents.END_WORLD_TICK.register((world -> {
            boolean currentValue = world.getGameRules().getBoolean(SYNC_HEALTH);
            if (currentValue != lastValue && currentValue) {
                world.getPlayers().forEach(player -> {
                    player.sendMessageToClient(Text.translatable("sync.health.enabled").formatted(Formatting.GREEN, Formatting.BOLD), false);
                });
                lastValue = true;
            }
            else if (currentValue != lastValue) {
                world.getPlayers().forEach(player -> {
                    player.sendMessageToClient(Text.translatable("sync.health.disabled").formatted(Formatting.RED, Formatting.BOLD), false);
                });
                lastValue = false;
            }
            if (world.getGameRules().getBoolean(SYNC_HEALTH)) {
                SharedHealthComponent component = SHARED_HEALTH.get(world.getScoreboard());
                if (component.getHealth() > 20) component.setHealth(20);
                float finalKnownHealth = component.getHealth();
                world.getPlayers().forEach(playerEntity -> {
                    try {
                        float currentHealth = playerEntity.getHealth();

                        if (currentHealth > finalKnownHealth) {
                            System.out.println("whuh? " + (currentHealth - finalKnownHealth));
                            playerEntity.damage(world.getDamageSources().generic(), currentHealth - finalKnownHealth);
                        } else if (currentHealth < finalKnownHealth) {
                            playerEntity.heal(finalKnownHealth - currentHealth);
                        }
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                });
            }
        }));

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            handler.player.setHealth(SHARED_HEALTH.get(handler.player.getWorld().getScoreboard()).getHealth());
        });

        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            newPlayer.setHealth(SHARED_HEALTH.get(newPlayer.getWorld().getScoreboard()).getHealth());
        });

    }
}
