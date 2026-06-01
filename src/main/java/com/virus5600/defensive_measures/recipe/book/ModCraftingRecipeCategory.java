package com.virus5600.defensive_measures.recipe.book;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;

import java.util.function.IntFunction;

public enum ModCraftingRecipeCategory implements StringIdentifiable {
	TURRETS("turrets", 0),
	PARTS("parts", 1),
	TRAPS("traps", 2),
	DEFENSE("defense", 3),
	EQUIPMENTS("equipments", 4),
	MISC("misc", 5);

	public static final Codec<ModCraftingRecipeCategory> CODEC = StringIdentifiable.createCodec(ModCraftingRecipeCategory::values);
	public static final IntFunction<ModCraftingRecipeCategory> INDEX_TO_VALUE = ValueLists.createIndexToValueFunction(ModCraftingRecipeCategory::getIndex, values(), ValueLists.OutOfBoundsHandling.ZERO);
	public static final PacketCodec<ByteBuf, ModCraftingRecipeCategory> PACKET_CODEC = PacketCodecs.indexed(INDEX_TO_VALUE, ModCraftingRecipeCategory::getIndex);

	private final String id;
	private final int index;

	private ModCraftingRecipeCategory(final String id, final int index) {
		this.id = id;
		this.index = index;
	}

	public String asString() {
		return this.id;
	}

	private int getIndex() {
		return this.index;
	}
}
