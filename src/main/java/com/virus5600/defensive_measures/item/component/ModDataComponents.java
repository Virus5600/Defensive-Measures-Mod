package com.virus5600.defensive_measures.item.component;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.util.ExtraCodecs;

import com.virus5600.defensive_measures.DefensiveMeasures;
import com.virus5600.defensive_measures._helper.RegistryHelper;

import java.util.function.UnaryOperator;

/**
 * Registers custom data components used within this mod.
 *
 * @since 1.2.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class ModDataComponents extends DataComponents {
	public static final DataComponentType<Integer> TICK_COUNT = register("tick_count", b -> b.persistent(ExtraCodecs.POSITIVE_INT).networkSynchronized(ByteBufCodecs.VAR_INT));
	public static final DataComponentType<Integer> DETECTION_RANGE = register("detection_range", b -> b.persistent(ExtraCodecs.POSITIVE_INT).networkSynchronized(ByteBufCodecs.VAR_INT));

	public static void init() {
		DefensiveMeasures.LOGGER.info("REGISTERING ITEM COMPONENTS...");
	}

	protected static <T> DataComponentType<T> register(String id, UnaryOperator<DataComponentType.Builder<T>> builder) {
		return RegistryHelper.registerComponent(id, builder);
	}
}
