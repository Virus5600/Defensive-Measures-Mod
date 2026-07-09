package com.virus5600.defensive_measures.recipe.annotations;

import com.virus5600.defensive_measures.recipe.BaseCraftingRecipe;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A simple annotation to determine if a {@link BaseCraftingRecipe} is a shaped recipe.
 *
 * @since 1.2.0-beta
 * @author <a href="https://github.com/Virus5600">Virus5600</a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Shaped {}
