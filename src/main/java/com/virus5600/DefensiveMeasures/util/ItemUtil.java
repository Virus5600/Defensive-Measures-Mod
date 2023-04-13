package com.virus5600.DefensiveMeasures.util;

import net.minecraft.item.Item;
import net.minecraft.item.ToolItem;

/**
 * Contains all utility methods that can be used for the {@link net.minecraft.item.Item Item class} such as identifying if it is a {@link net.minecraft.item.ToolItem ToolItem} instance or other.
 * This is to supplement some shortcomings of the primary vanilla {@code Item} class and create flexibility towards applying features and modifications.
 * @author Virus5600
 * @since 1.0.0
 * @see net.minecraft.item.Item Item
 * @see net.minecraft.item.ToolItem ToolItem
 * @see net.minecraft.item.SwordItem SwordItem
 */
public final class ItemUtil {
	private ItemUtil() { }

	/**
	 * Identifies whether the provided {@code item} is a subclass of the provided {@code class}.
	 *
	 * @param type Target subclass
	 * @param item Item in question
	 * @return boolean
	 */
	public static boolean isTypeMatch(final Class<?> type, final Item item) {
		return type.isAssignableFrom(item.getClass());
	}

	/**
	 * Identifies whether the provided {@code item} is a subclass of {@link ToolItem}.
	 *
	 * @param item Item in question
	 * @return boolean
	 * @see ToolItem
	 */
	public static boolean isToolItem(final Item item) {
		return ToolItem.class.isAssignableFrom(item.getClass());
	}

	/**
	 * Retrieves the Class instance provided by the {@code type} of this item.
	 * @param T Target subclass
	 * @param type Target subclass
	 * @param item Item in question
	 * @return An instance provided on the parameter {@code type}
	 */
	public static <T> T getObjectInstance(final Class<? extends T> type, final Item item) {
		if (ItemUtil.isTypeMatch(type, item)) return type.cast(item);
		return null;
	}

	/**
	 * Retrieves the {@link ToolItem} instance of this item.
	 * @param item Item in question
	 * @return ToolItem
	 * @see ToolItem
	 */
	public static ToolItem getToolItem(final Item item) {
		if (ItemUtil.isToolItem(item)) return (ToolItem) item;
		return null;
	}
}
