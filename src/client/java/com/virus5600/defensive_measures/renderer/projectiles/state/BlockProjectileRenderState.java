package com.virus5600.defensive_measures.renderer.projectiles.state;

import net.minecraft.client.renderer.block.MovingBlockRenderState;

import com.virus5600.defensive_measures.renderer.projectiles.BlockProjectileRenderer;

/**
 * A special render state for the {@link BlockProjectileRenderer}, allowing the tracking of the
 * block state of the projectile, which is used to render the block projectile in the world.
 *
 * @since 1.2.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public class BlockProjectileRenderState extends BaseProjectileRenderState {
	public final MovingBlockRenderState movingBlockRenderState = new MovingBlockRenderState();
}
