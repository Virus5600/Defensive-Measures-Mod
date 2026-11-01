package com.virus5600.defensive_measures._helper;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;
import java.util.List;

public final class DelayedTasks {
	private static final List<Entry> QUEUE = new ArrayList<>();

	public static void init() {
		ServerTickEvents.END_SERVER_TICK.register(DelayedTasks::tick);
		ServerLifecycleEvents.SERVER_STOPPED.register(server -> QUEUE.clear());
	}

	public static void schedule(int delayTicks, Runnable task) {
		QUEUE.add(new Entry(delayTicks, task));
	}

	private static void tick(MinecraftServer server) {
		long now = server.getTickCount();
		List<Runnable> due = new ArrayList<>();

		QUEUE.removeIf(e -> {
			if (e.dueTicks() <= now) {
				due.add(e.task());
				return true;
			}

			return false;
		});

		due.forEach(Runnable::run);
	}

	// /////////////// //
	// INTERNAL RECORD //
	// /////////////// //
	private record Entry(long dueTicks, Runnable task) {}
}
