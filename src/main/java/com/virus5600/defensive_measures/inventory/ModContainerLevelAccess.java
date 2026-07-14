package com.virus5600.defensive_measures.inventory;

import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;

import java.util.Optional;
import java.util.function.BiFunction;

public interface ModContainerLevelAccess extends ContainerLevelAccess {
	ModContainerLevelAccess NULL = new ModContainerLevelAccess() {
		@Override
		public BlockPos getPos() {
			return BlockPos.ZERO;
		}

		@Override
		public <T> Optional<T> evaluate(BiFunction<Level, BlockPos, T> action) {
			return Optional.empty();
		}
	};

	static ModContainerLevelAccess create(final Level level, final BlockPos pos) {
		return new ModContainerLevelAccess() {
			@Override
			public BlockPos getPos() {
				return pos;
			}

			@Override
			public <T> Optional<T> evaluate(BiFunction<Level, BlockPos, T> action) {
				return Optional.of(action.apply(level, pos));
			}
		};
	}

	BlockPos getPos();
}
