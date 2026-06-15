package com.virus5600.defensive_measures.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.criterion.ContextAwarePredicate;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.advancements.criterion.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import org.jspecify.annotations.NonNull;

import java.util.Optional;

public class TurretItemRetrievedCriterion extends SimpleCriterionTrigger<TurretItemRetrievedCriterion.Conditions> {
	@Override @NonNull
	public Codec<TurretItemRetrievedCriterion.Conditions> codec() {
		return TurretItemRetrievedCriterion.Conditions.CODEC;
	}

    public void trigger(ServerPlayer player, ItemStack stack) {
        super.trigger(player, conditions -> conditions.matches(stack));
    }

	public record Conditions(Optional<ContextAwarePredicate> player, Optional<ItemPredicate> item) implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<TurretItemRetrievedCriterion.Conditions> CODEC = RecordCodecBuilder.create(
				instance -> instance.group(
					EntityPredicate.ADVANCEMENT_CODEC
						.optionalFieldOf("player")
						.forGetter(TurretItemRetrievedCriterion.Conditions::player),
					ItemPredicate.CODEC
						.optionalFieldOf("item")
						.forGetter(TurretItemRetrievedCriterion.Conditions::item)
				)
				.apply(instance, TurretItemRetrievedCriterion.Conditions::new)
		);

		public static Criterion<com.virus5600.defensive_measures.advancement.criterion.TurretItemRetrievedCriterion.Conditions> create(ItemPredicate.Builder item) {
			return ModCriterion.TURRET_ITEM_RETRIEVED_CRITERION
				.createCriterion(
					new TurretItemRetrievedCriterion.Conditions(
						Optional.empty(),
						Optional.of(item.build())
					)
				);
		}

		public boolean matches(ItemStack stack) {
			return this.item.isEmpty() || this.item.get().test(stack);
		}
    }
}
