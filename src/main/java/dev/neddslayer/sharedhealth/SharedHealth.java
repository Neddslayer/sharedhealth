package dev.neddslayer.sharedhealth;

import dev.neddslayer.sharedhealth.components.SharedExhaustionComponent;
import dev.neddslayer.sharedhealth.components.SharedHealthComponent;
import dev.neddslayer.sharedhealth.components.SharedHungerComponent;
import dev.neddslayer.sharedhealth.components.SharedSaturationComponent;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleBuilder;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.rule.GameRule;
import net.minecraft.world.rule.GameRuleCategory;

import static dev.neddslayer.sharedhealth.components.SharedComponentsInitializer.*;

public class SharedHealth implements ModInitializer {
    public static final String MOD_ID = "sharedhealth";

    public static final GameRule<Boolean> SYNC_HEALTH = GameRuleBuilder
        .forBoolean(true)
        .category(GameRuleCategory.PLAYER)
        .buildAndRegister(Identifier.of(MOD_ID, "shareHealth"));

    public static final GameRule<Boolean> SYNC_HUNGER = GameRuleBuilder
        .forBoolean(true)
        .category(GameRuleCategory.PLAYER)
        .buildAndRegister(Identifier.of(MOD_ID, "shareHunger"));

    public static final GameRule<Boolean> LIMIT_HEALTH = GameRuleBuilder
        .forBoolean(true)
        .category(GameRuleCategory.PLAYER)
        .buildAndRegister(Identifier.of(MOD_ID, "limitHealth"));

    private static boolean lastHealthValue = true;
    private static boolean lastHungerValue = true;

    /**
     * Runs the mod initializer.
     */
    @Override
    public void onInitialize() {
        ServerTickEvents.END_WORLD_TICK.register((world -> {
            boolean currentHealthValue = world.getGameRules().getValue(SYNC_HEALTH);
            boolean currentHungerValue = world.getGameRules().getValue(SYNC_HUNGER);
            boolean limitHealthValue = world.getGameRules().getValue(LIMIT_HEALTH);
            if (currentHealthValue != lastHealthValue && currentHealthValue) {
                world.getServer().getPlayerManager().getPlayerList().forEach(player -> player.sendMessageToClient(Text.translatable("gamerule.sharedhealth.shareHealth.enabled").formatted(Formatting.GREEN, Formatting.BOLD), false));
                lastHealthValue = true;
            }
            else if (currentHealthValue != lastHealthValue) {
                world.getServer().getPlayerManager().getPlayerList().forEach(player -> player.sendMessageToClient(Text.translatable("gamerule.sharedhealth.shareHealth.disabled").formatted(Formatting.RED, Formatting.BOLD), false));
                lastHealthValue = false;
            }
            if (currentHungerValue != lastHungerValue && currentHungerValue) {
                world.getServer().getPlayerManager().getPlayerList().forEach(player -> player.sendMessageToClient(Text.translatable("gamerule.sharedhealth.shareHunger.enabled").formatted(Formatting.GREEN, Formatting.BOLD), false));
                lastHungerValue = true;
            }
            else if (currentHungerValue != lastHungerValue) {
                world.getServer().getPlayerManager().getPlayerList().forEach(player -> player.sendMessageToClient(Text.translatable("gamerule.sharedhealth.shareHunger.disabled").formatted(Formatting.RED, Formatting.BOLD), false));
                lastHungerValue = false;
            }
            if (world.getGameRules().getValue(SYNC_HEALTH)) {
                SharedHealthComponent component = SHARED_HEALTH.get(world.getScoreboard());
                if (component.getHealth() > 20 && limitHealthValue) component.setHealth(20);
                float finalKnownHealth = component.getHealth();
                world.getPlayers().forEach(playerEntity -> {
                    try {
                        float currentHealth = playerEntity.getHealth();

                        if (currentHealth > finalKnownHealth) {
                            playerEntity.damage(world, world.getDamageSources().genericKill(), currentHealth - finalKnownHealth);
                        } else if (currentHealth < finalKnownHealth) {
                            playerEntity.heal(finalKnownHealth - currentHealth);
                        }
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                    }
                });
            }
            if (world.getGameRules().getValue(SYNC_HUNGER)) {
                SharedHungerComponent component = SHARED_HUNGER.get(world.getScoreboard());
	            SharedSaturationComponent saturationComponent = SHARED_SATURATION.get(world.getScoreboard());
	            SharedExhaustionComponent exhaustionComponent = SHARED_EXHAUSTION.get(world.getScoreboard());
                if (component.getHunger() > 20) component.setHunger(20);
				if (saturationComponent.getSaturation() > 20) saturationComponent.setSaturation(20.0f);
                int finalKnownHunger = component.getHunger();
				float finalKnownSaturation = saturationComponent.getSaturation();
				float finalKnownExhaustion = exhaustionComponent.getExhaustion();
                world.getPlayers().forEach(playerEntity -> {
                    try {
                        float currentHunger = playerEntity.getHungerManager().getFoodLevel();
						float currentSaturation = playerEntity.getHungerManager().getSaturationLevel();
						float currentExhaustion = playerEntity.getHungerManager().exhaustion;

                        if (currentHunger != finalKnownHunger) {
                            playerEntity.getHungerManager().setFoodLevel(finalKnownHunger);
                        }
						if (currentSaturation != finalKnownSaturation) {
							playerEntity.getHungerManager().setSaturationLevel(finalKnownSaturation);
						}
						if (currentExhaustion != finalKnownExhaustion) {
							playerEntity.getHungerManager().exhaustion = finalKnownExhaustion;
						}
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                });
            }
        }));

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> handler.player.setHealth(SHARED_HEALTH.get(handler.player.getEntityWorld().getScoreboard()).getHealth()));

        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> newPlayer.setHealth(SHARED_HEALTH.get(newPlayer.getEntityWorld().getScoreboard()).getHealth()));
    }
}
