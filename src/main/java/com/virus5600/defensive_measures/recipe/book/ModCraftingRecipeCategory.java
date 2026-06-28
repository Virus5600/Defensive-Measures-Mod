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
	DEFENSE("defenses", 3),
	EQUIPMENT("equipment", 4),
	MISC("misc", 5);

	public static final Codec<ModCraftingRecipeCategory> CODEC = StringRepresentable.fromEnum(ModCraftingRecipeCategory::values);
	public static final IntFunction<ModCraftingRecipeCategory> BY_ID = ByIdMap.continuous(ModCraftingRecipeCategory::id, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
	public static final StreamCodec<ByteBuf, ModCraftingRecipeCategory> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, ModCraftingRecipeCategory::id);

	private final String name;
	private final int id;

	ModCraftingRecipeCategory(final String name, final int id) {
		this.name = name;
		this.id = id;
	}

	@NonNull
	public String getSerializedName() {
		return this.name;
	}

	private int id() {
		return this.id;
	}
}
