package com.virus5600.defensive_measures._helper.accessor.model;

import net.minecraft.client.model.geom.ModelPart;

import java.util.List;

/**
 * Exposes the children map from the {@link ModelPart} class, allowing access to the much needed
 * names of the children a model part has.
 *
 * @since 1.2.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
public interface ModelPartExtensions {
	/**
	 * Determines the names of the children of a model part, allowing for more dynamic animations
	 * and model manipulation.
	 *
	 * @return an array list of the names of the children of a model part.
	 */
	List<String> dm$getChildrenNames();
}
