package dev.neddslayer.sharedhealth;

import dev.neddslayer.sharedhealth.components.SharedHealthComponent;
import dev.neddslayer.sharedhealth.components.SharedHungerComponent;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.GameRules;

import static dev.neddslayer.sharedhealth.components.SharedHealthComponentInitializer.SHARED_HEALTH;
import static dev.neddslayer.sharedhealth.components.SharedHealthComponentInitializer.SHARED_HUNGER;

public class SharedHealth implements ModInitializer {

    public static final GameRules.Key<GameRules.BooleanRule> SYNC_HEALTH =
            GameRuleRegistry.register("shareHealth", GameRules.Category.PLAYER, GameRuleFactory.createBooleanRule(true));
    public static final GameRules.Key<GameRules.BooleanRule> SYNC_HUNGER =
            GameRuleRegistry.register("shareHunger", GameRules.Category.PLAYER, GameRuleFactory.createBooleanRule(true));
    private static boolean lastHealthValue = true;
    private static boolean lastHungerValue = true;

    /**
     * Runs the mod initializer.
     */
    @Override
    public void onInitialize() {
        ServerTickEvents.END_WORLD_TICK.register((world -> {
            boolean currentHealthValue = world.getGameRules().getBoolean(SYNC_HEALTH);
            boolean currentHungerValue = world.getGameRules().getBoolean(SYNC_HUNGER);
            if (currentHealthValue != lastHealthValue && currentHealthValue) {
                world.getPlayers().forEach(player -> player.sendMessageToClient(Text.translatable("gamerule.shareHealth.enabled").formatted(Formatting.GREEN, Formatting.BOLD), false));
                lastHealthValue = true;
            }
            else if (currentHealthValue != lastHealthValue) {
                world.getPlayers().forEach(player -> player.sendMessageToClient(Text.translatable("gamerule.shareHealth.disabled").formatted(Formatting.RED, Formatting.BOLD), false));
                lastHealthValue = false;
            }
            if (currentHungerValue != lastHungerValue && currentHungerValue) {
                world.getPlayers().forEach(player -> player.sendMessageToClient(Text.translatable("gamerule.shareHunger.enabled").formatted(Formatting.GREEN, Formatting.BOLD), false));
                lastHungerValue = true;
            }
            else if (currentHungerValue != lastHungerValue) {
                world.getPlayers().forEach(player -> player.sendMessageToClient(Text.translatable("gamerule.shareHunger.disabled").formatted(Formatting.RED, Formatting.BOLD), false));
                lastHungerValue = false;
            }
            if (world.getGameRules().getBoolean(SYNC_HEALTH)) {
                SharedHealthComponent component = SHARED_HEALTH.get(world.getScoreboard());
                if (component.getHealth() > 20) component.setHealth(20);
                float finalKnownHealth = component.getHealth();
                world.getPlayers().forEach(playerEntity -> {
                    try {
                        float currentHealth = playerEntity.getHealth();

                        if (currentHealth > finalKnownHealth) {
                            playerEntity.damage(world.getDamageSources().generic(), currentHealth - finalKnownHealth);
                        } else if (currentHealth < finalKnownHealth) {
                            playerEntity.heal(finalKnownHealth - currentHealth);
                        }
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                });
            }
            if (world.getGameRules().getBoolean(SYNC_HUNGER)) {
                SharedHungerComponent component = SHARED_HUNGER.get(world.getScoreboard());
                if (component.getHunger() > 20) component.setHunger(20);
                int finalKnownHunger = component.getHunger();
                world.getPlayers().forEach(playerEntity -> {
                    try {
                        float currentHunger = playerEntity.getHungerManager().getFoodLevel();

                        if (currentHunger != finalKnownHunger) {
                            playerEntity.getHungerManager().setFoodLevel(finalKnownHunger);
                        }
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                });
            }
        }));

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> handler.player.setHealth(SHARED_HEALTH.get(handler.player.getWorld().getScoreboard()).getHealth()));

        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> newPlayer.setHealth(SHARED_HEALTH.get(newPlayer.getWorld().getScoreboard()).getHealth()));
    }
}
