package com.virus5600.defensive_measures.color.items;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.ARGB;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import com.virus5600.defensive_measures.item.ModItems;
import com.virus5600.defensive_measures.item.equipments.tier2.MetalDetectorItem;

import org.jspecify.annotations.Nullable;

/**
 * A record that implements the {@link ItemTintSource} interface to provide tinting for
 * {@link MetalDetectorItem metal detector items}.
 * <br><br>
 * Specifically, it is used to apply tinting to the metal coil detector (the circular part on the
 * bottom end of the detector) to mirror the color of the material used on creating said detector.
 * <br><br>
 * The tinting is applied based on the item type, with the:
 * <ul>
 *     <li><b>IRON Metal Detector</b> using a {@link #IRON_TINT light gray tint}</li>
 *     <li><b>NETHERITE Metal Detector</b> using a {@link #NETHERITE_TINT dark gray tint}</li>
 * </ul>
 *
 * @since 1.2.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public record MetalDetectorTintSources(int color) implements ItemTintSource {
	/**	0xD8D8D8 */
	private static final int IRON_TINT = 14211288;
	/** 0x443A3B */
	private static final int NETHERITE_TINT = 4471355;

	public static final MapCodec<MetalDetectorTintSources> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
			ExtraCodecs.RGB_COLOR_CODEC
				.fieldOf("color")
				.forGetter(MetalDetectorTintSources::color)
		).apply(instance,  MetalDetectorTintSources::new)
	);

	// ///////////////// //
	// INTERFACE METHODS //
	// ///////////////// //

	@Override
	public int calculate(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity owner) {
		int tint = this.color;

		if (tint == 0) {
			if (stack.is(ModItems.NETHERITE_METAL_DETECTOR)) {
				tint = NETHERITE_TINT;
			}
			else if (stack.is(ModItems.IRON_METAL_DETECTOR)) {
				tint = IRON_TINT;
			}
			else {
				tint = IRON_TINT;
			}
		}

		return ARGB.opaque(tint);
	}

	@Override
	public MapCodec<? extends ItemTintSource> type() {
		return CODEC;
	}
}
