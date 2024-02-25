package dev.neddslayer.sharedhealth.mixin;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {

	@Inject(method = "render", at = @At("TAIL"))
	public void debugRender(DrawContext context, float tickDelta, CallbackInfo ci) {
		// This code is used for debugging synced values. It's not accessible during normal play.
		// To properly debug, it's best to install the AppleSkin mod as that properly syncs your saturation & exhaustion levels.
		if (MinecraftClient.getInstance().player != null && FabricLoader.getInstance().isDevelopmentEnvironment()) {
			context.drawText(MinecraftClient.getInstance().textRenderer, "Health: " + MinecraftClient.getInstance().player.getHealth(), 10, 10, 0xffffff, true);
			context.drawText(MinecraftClient.getInstance().textRenderer, "Hunger: " + MinecraftClient.getInstance().player.getHungerManager().getFoodLevel(), 10, 30, 0xffffff, true);
			context.drawText(MinecraftClient.getInstance().textRenderer, "Saturation: " + MinecraftClient.getInstance().player.getHungerManager().getSaturationLevel(), 10, 50, 0xffffff, true);
			context.drawText(MinecraftClient.getInstance().textRenderer, "Exhaustion: " + MinecraftClient.getInstance().player.getHungerManager().getExhaustion(), 10, 70, 0xffffff, true);
		}
	}

}
