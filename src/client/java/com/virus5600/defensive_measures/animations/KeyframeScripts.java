package com.virus5600.defensive_measures.animations;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ExplosionParticleInfo;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;

import com.virus5600.defensive_measures.entity.damage.ModDamageSources;
import com.virus5600.defensive_measures.entity.damage.ModDamageTypes;
import com.virus5600.defensive_measures.particle.ModParticles;

/**
 * This class contains scripts for keyframe actions that can be used in animations. These scripts
 * define specific behaviors that occur at certain keyframes during an animation sequence.
 * <br><br>
 * Pre-made scripts are defined here, allowing reusability of certain scripts.
 *
 * @see KeyframeAction
 *
 * @since 1.1.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class KeyframeScripts {
	/** An explosion effect for VFX. Does not deal damage nor destroy environment. */
	public static KeyframeAction EXPLODE_SCRIPT;

	static {
		EXPLODE_SCRIPT = (animState, state, pos) -> {
			ClientLevel level = Minecraft.getInstance().level;

			if (level == null) { return; }

			Entity entity = level.getEntity(state.id);
			DamageSource src = ModDamageSources.create(level, ModDamageTypes.SECONDARY_EXPLOSION, entity, null);

			EntityDimensions dim = EntityDimensions.fixed(1, 1);

			if (entity != null) {
				dim = entity.getDimensions(entity.getPose());
			}


			level.explode(
				entity, src, new ExplosionDamageCalculator(),
				pos.x(), pos.y(), pos.z(),
				dim.width() + 0.5f, false,
				Level.ExplosionInteraction.NONE,
				ModParticles.FLAK_EXPLOSION,
				ModParticles.FLAK_EXPLOSION,
				WeightedList.<ExplosionParticleInfo>builder().build(),
				SoundEvents.GENERIC_EXPLODE
			);
		};
	}
}
