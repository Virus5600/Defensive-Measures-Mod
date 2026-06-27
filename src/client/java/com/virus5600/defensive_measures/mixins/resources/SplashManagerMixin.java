package com.virus5600.defensive_measures.mixins.resources;

import net.minecraft.client.resources.SplashManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.virus5600.defensive_measures.DefensiveMeasures;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Mixin(SplashManager.class)
public class SplashManagerMixin {
	@Unique
	private static final Identifier CUSTOM_SPLASHES_LOCATION;

	@Inject(method = "prepare", at = @At("RETURN"), cancellable = true)
	protected void injectPrepare(ResourceManager manager, ProfilerFiller profiler, CallbackInfoReturnable<List<MutableComponent>> cir) {
		List<MutableComponent> originalSplashes = cir.getReturnValue();
		List<MutableComponent> modifiedSplashes = new ArrayList<>(originalSplashes);

		try (BufferedReader reader = manager.openAsReader(CUSTOM_SPLASHES_LOCATION)) {
			List<MutableComponent> customSplashes = reader.lines()
					.map(String::trim)
					.filter(line -> !line.isEmpty())
					.map((txt) -> Component.literal(txt).setStyle(Style.EMPTY.withColor(-256)))
					.toList();

			modifiedSplashes.addAll(customSplashes);
			cir.setReturnValue(modifiedSplashes);
		} catch (IOException e) {
			cir.setReturnValue(modifiedSplashes);
			DefensiveMeasures.LOGGER.error("Failed to load custom splashes from {}", CUSTOM_SPLASHES_LOCATION.toShortString(), e);
		}
	}

	static {
		CUSTOM_SPLASHES_LOCATION = Identifier.fromNamespaceAndPath(DefensiveMeasures.MOD_ID, "texts/splashes.txt");
	}
}
