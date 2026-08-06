package com.virus5600.defensive_measures.renderer;

import com.virus5600.defensive_measures.DefensiveMeasures;

/**
 * Registers custom renderers used by this mod.
 *
 * @since 1.2.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class ModCustomRenderers {
	public static void registerCustomRenderers() {
		DefensiveMeasures.LOGGER.info("REGISTERING CUSTOM RENDERERS FOR {}...", DefensiveMeasures.MOD_NAME);

		BlockHighlightRenderer.init();
	}
}
