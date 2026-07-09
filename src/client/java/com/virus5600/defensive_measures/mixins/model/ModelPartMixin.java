package com.virus5600.defensive_measures.mixins.model;

import net.minecraft.client.model.geom.ModelPart;

import com.virus5600.defensive_measures._helper.accessor.model.ModelPartExtensions;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.Map;

/**
 * @since 1.2.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
@Mixin(ModelPart.class)
public abstract class ModelPartMixin implements ModelPartExtensions {
	@Shadow @Final private Map<String, ModelPart> children;

	public List<String> dm$getChildrenNames() {
		if (this.children == null ||  this.children.isEmpty()) {
			return List.of();
		}

		return this.children.keySet().stream().toList();
	}
}
