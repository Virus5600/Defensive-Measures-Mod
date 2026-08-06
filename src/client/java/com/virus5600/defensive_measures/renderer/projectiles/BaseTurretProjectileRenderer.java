package com.virus5600.defensive_measures.renderer.projectiles;

import net.minecraft.client.renderer.entity.EntityRendererProvider;

import com.virus5600.defensive_measures.entity.projectiles.TurretProjectileEntity;
import com.virus5600.defensive_measures.model.projectiles.BaseProjectileModel;
import com.virus5600.defensive_measures.renderer.projectiles.state.BaseProjectileRenderState;

import org.jspecify.annotations.NonNull;

import java.util.function.Supplier;

/**
 * The base turret projectile renderer, centralizing the handling of common logics and rendering
 * for all projectile types. This design also allows for unique projectiles to handle their own
 * unique custom logic while just calling methods from this class to implement the common logic
 * across all turret projectiles.
 *
 * @param <T> The projectile entity for this renderer. Must be a subclass of {@link TurretProjectileEntity}.
 * @param <S> The render state for this renderer.
 * @param <M> The model for this renderer.
 *
 * @since 1.0.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public abstract class BaseTurretProjectileRenderer<
	T extends TurretProjectileEntity,
	S extends BaseProjectileRenderState,
	M extends BaseProjectileModel<S>
	> extends BaseProjectileRenderer<T, S, M> {

	public BaseTurretProjectileRenderer(
            EntityRendererProvider.Context context,
            M entityModel,
            float shadowRadius,
            Supplier<S> renderStateFactory
	) {
		super(context, entityModel, shadowRadius, renderStateFactory);
	}

	@Override
	public void extractRenderState(@NonNull T entity, @NonNull S state, float tickProgress) {
		super.extractRenderState(entity, state, tickProgress);

		state.loopAnimationState.copyFrom(entity.getLoopAnimationState());
	}
}
