package com.virus5600.defensive_measures.util;

import net.minecraft.item.Item;
import org.jetbrains.annotations.Nullable;

/**
 * Contains all utility methods that can be used for the {@link net.minecraft.item.Item Item class}
 * such as identifying if it matches a superclass or interface. This is to supplement some
 * shortcomings of the primary vanilla {@code Item} class and create flexibility towards applying
 * features and modifications.
 *
 * @see net.minecraft.item.Item Item
 *
 * @since 1.0.0
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 * @version 1.0.0
 */
public class ItemUtil {
	/**
	 * Identifies whether the provided {@code item} is a subclass of the provided {@code class}
	 * @param type Target subclass
	 * @param item Item in question
	 * @return boolean
	 */
	public static boolean isTypeMatch(Class<?> type, Item item) {
		return type.isAssignableFrom(item.getClass());
	}

	/**
	 * Retrieves the Class instance provided by the {@code type} of this item.
	 * @param type Target subclass
	 * @param item Item in question
	 * @return An instance provided on the parameter {@code type}
	 */
	@Nullable
	public static <T> T getObjectInstance(Class<? extends T> type, Item item) {
		if (ItemUtil.isTypeMatch(type, item))
			return type.cast(item);
		return null;
	}
}
