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
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRuleCategory;

import static dev.neddslayer.sharedhealth.components.SharedComponentsInitializer.*;

public class SharedHealth implements ModInitializer {
    public static final GameRule<Boolean> SYNC_HEALTH = GameRuleBuilder
            .forBoolean(true).category(GameRuleCategory.PLAYER).buildAndRegister(Identifier.fromNamespaceAndPath("sharedhealth", "share_health"));
    public static final GameRule<Boolean> SYNC_HUNGER = GameRuleBuilder
            .forBoolean(true).category(GameRuleCategory.PLAYER).buildAndRegister(Identifier.fromNamespaceAndPath("sharedhealth", "share_hunger"));
    public static final GameRule<Boolean> LIMIT_HEALTH = GameRuleBuilder
            .forBoolean(true).category(GameRuleCategory.PLAYER).buildAndRegister(Identifier.fromNamespaceAndPath("sharedhealth", "limit_health"));
    private static boolean lastHealthValue = true;
    private static boolean lastHungerValue = true;

    /**
     * Runs the mod initializer.
     */
    @Override
    public void onInitialize() {
        ServerTickEvents.END_LEVEL_TICK.register((world -> {
            boolean currentHealthValue = world.getGameRules().get(SYNC_HEALTH);
            boolean currentHungerValue = world.getGameRules().get(SYNC_HUNGER);
            boolean limitHealthValue = world.getGameRules().get(LIMIT_HEALTH);
            if (currentHealthValue != lastHealthValue && currentHealthValue) {
                world.getServer().getPlayerList().getPlayers().forEach(player -> player.sendSystemMessage(Component.translatable("gamerule.sharedhealth.share_health.enabled").withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD), false));
                lastHealthValue = true;
            }
            else if (currentHealthValue != lastHealthValue) {
                world.getServer().getPlayerList().getPlayers().forEach(player -> player.sendSystemMessage(Component.translatable("gamerule.sharedhealth.share_health.disabled").withStyle(ChatFormatting.RED, ChatFormatting.BOLD), false));
                lastHealthValue = false;
            }
            if (currentHungerValue != lastHungerValue && currentHungerValue) {
                world.getServer().getPlayerList().getPlayers().forEach(player -> player.sendSystemMessage(Component.translatable("gamerule.sharedhealth.share_hunger.enabled").withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD), false));
                lastHungerValue = true;
            }
            else if (currentHungerValue != lastHungerValue) {
                world.getServer().getPlayerList().getPlayers().forEach(player -> player.sendSystemMessage(Component.translatable("gamerule.sharedhealth.share_hunger.disabled").withStyle(ChatFormatting.RED, ChatFormatting.BOLD), false));
                lastHungerValue = false;
            }
            if (world.getGameRules().get(SYNC_HEALTH)) {
                SharedHealthComponent component = SHARED_HEALTH.get(world.getScoreboard());
                if (component.getHealth() > 20 && limitHealthValue) component.setHealth(20);
                float finalKnownHealth = component.getHealth();
                world.players().forEach(playerEntity -> {
                    try {
                        float currentHealth = playerEntity.getHealth();

                        if (currentHealth > finalKnownHealth) {
                            playerEntity.hurtServer(world, world.damageSources().genericKill(), currentHealth - finalKnownHealth);
                        } else if (currentHealth < finalKnownHealth) {
                            playerEntity.heal(finalKnownHealth - currentHealth);
                        }
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                    }
                });
            }
            if (world.getGameRules().get(SYNC_HUNGER)) {
                SharedHungerComponent component = SHARED_HUNGER.get(world.getScoreboard());
	            SharedSaturationComponent saturationComponent = SHARED_SATURATION.get(world.getScoreboard());
	            SharedExhaustionComponent exhaustionComponent = SHARED_EXHAUSTION.get(world.getScoreboard());
                if (component.getHunger() > 20) component.setHunger(20);
				if (saturationComponent.getSaturation() > 20) saturationComponent.setSaturation(20.0f);
                int finalKnownHunger = component.getHunger();
				float finalKnownSaturation = saturationComponent.getSaturation();
				float finalKnownExhaustion = exhaustionComponent.getExhaustion();
                world.players().forEach(playerEntity -> {
                    try {
                        float currentHunger = playerEntity.getFoodData().getFoodLevel();
						float currentSaturation = playerEntity.getFoodData().getSaturationLevel();
						float currentExhaustion = playerEntity.getFoodData().exhaustionLevel;

                        if (currentHunger != finalKnownHunger) {
                            playerEntity.getFoodData().setFoodLevel(finalKnownHunger);
                        }
						if (currentSaturation != finalKnownSaturation) {
							playerEntity.getFoodData().setSaturation(finalKnownSaturation);
						}
						if (currentExhaustion != finalKnownExhaustion) {
							playerEntity.getFoodData().exhaustionLevel = finalKnownExhaustion;
						}
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                });
            }
        }));

        ServerPlayConnectionEvents.JOIN.register((handler, _, _) -> handler.player.setHealth(SHARED_HEALTH.get(handler.player.level().getScoreboard()).getHealth()));

        ServerPlayerEvents.AFTER_RESPAWN.register((_, newPlayer, _) -> newPlayer.setHealth(SHARED_HEALTH.get(newPlayer.level().getScoreboard()).getHealth()));
    }
}
