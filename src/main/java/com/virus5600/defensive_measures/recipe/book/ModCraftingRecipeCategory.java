package com.virus5600.defensive_measures.recipe.book;

import com.mojang.serialization.Codec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

import org.jspecify.annotations.NonNull;

import io.netty.buffer.ByteBuf;

import java.util.function.IntFunction;

public enum ModCraftingRecipeCategory implements StringRepresentable {
	TURRETS("turrets", 0),
	PARTS("parts", 1),
	TRAPS("traps", 2),
	DEFENSE("defense", 3),
	EQUIPMENTS("equipments", 4),
	MISC("misc", 5);

	public static final Codec<ModCraftingRecipeCategory> CODEC = StringRepresentable.fromEnum(ModCraftingRecipeCategory::values);
	public static final IntFunction<ModCraftingRecipeCategory> INDEX_TO_VALUE = ByIdMap.continuous(ModCraftingRecipeCategory::getIndex, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
	public static final StreamCodec<ByteBuf, ModCraftingRecipeCategory> PACKET_CODEC = ByteBufCodecs.idMapper(INDEX_TO_VALUE, ModCraftingRecipeCategory::getIndex);

	private final String id;
	private final int index;

	ModCraftingRecipeCategory(final String id, final int index) {
		this.id = id;
		this.index = index;
	}

	@NonNull
	public String getSerializedName() {
		return this.id;
	}

	private int getIndex() {
		return this.index;
	}
}
