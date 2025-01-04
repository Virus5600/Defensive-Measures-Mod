package com.virus5600.defensive_measures.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.virus5600.defensive_measures.DefensiveMeasures;

import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Optional;

public class TurretItemRetrievedCriterion extends AbstractCriterion<TurretItemRetrievedCriterion.Conditions> {
	@Override
	public Codec<TurretItemRetrievedCriterion.Conditions> getConditionsCodec() {
		return TurretItemRetrievedCriterion.Conditions.CODEC;
	}

    public void trigger(ServerPlayerEntity player, ItemStack stack) {
        super.trigger(player, conditions -> conditions.matches(stack));
    }

	public record Conditions(Optional<LootContextPredicate> player, Optional<ItemPredicate> item) implements AbstractCriterion.Conditions {
        public static final Codec<TurretItemRetrievedCriterion.Conditions> CODEC = RecordCodecBuilder.create(
				instance -> instance.group(
					EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC
						.optionalFieldOf("player")
						.forGetter(TurretItemRetrievedCriterion.Conditions::player),
					ItemPredicate.CODEC
						.optionalFieldOf("item")
						.forGetter(TurretItemRetrievedCriterion.Conditions::item)
				)
				.apply(instance, TurretItemRetrievedCriterion.Conditions::new)
		);

		public static AdvancementCriterion<Conditions> create(ItemPredicate.Builder item) {
			return ModCriterion.TURRET_ITEM_RETRIEVED_CRITERION
				.create(
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
