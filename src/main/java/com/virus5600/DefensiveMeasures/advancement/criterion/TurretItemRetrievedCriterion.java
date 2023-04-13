package com.virus5600.DefensiveMeasures.advancement.criterion;

import com.google.gson.JsonObject;

import com.virus5600.DefensiveMeasures.DefensiveMeasures;
import com.virus5600.DefensiveMeasures.advancement.criterion.TurretItemRetrievedCriterion.Conditions;

import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.EntityPredicate.Extended;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class TurretItemRetrievedCriterion extends AbstractCriterion<Conditions> {
	static final Identifier ID = new Identifier(DefensiveMeasures.MOD_ID, "filled_bucket");

	@Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public Conditions conditionsFromJson(final JsonObject jsonObject, final EntityPredicate.Extended extended, final AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer) {
        ItemPredicate itemPredicate = ItemPredicate.fromJson(jsonObject.get("item"));
        return new Conditions(extended, itemPredicate);
    }

    public void trigger(final ServerPlayerEntity player, final ItemStack stack) {
        this.trigger(player, (Conditions conditions) -> conditions.matches(stack));
    }

	public static class Conditions extends AbstractCriterionConditions {
        private final ItemPredicate item;

        public Conditions(final Extended player, final ItemPredicate item) {
            super(ID, player);
            this.item = item;
        }

        public static Conditions create(final ItemPredicate item) {
            return new Conditions(EntityPredicate.Extended.EMPTY, item);
        }

        public boolean matches(final ItemStack stack) {
            return this.item.test(stack);
        }

        @Override
        public JsonObject toJson(final AdvancementEntityPredicateSerializer predicateSerializer) {
            JsonObject jsonObject = super.toJson(predicateSerializer);
            jsonObject.add("item", this.item.toJson());
            return jsonObject;
        }
    }
}
