package com.mmodding.redstone_sculk.init;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.event.GameEvent;

public class GameEvents {

	public static GameEvent REDSTONE_SCULK_SENSOR_ACTIVATE;

	public static void registerGameEvents() {
		REDSTONE_SCULK_SENSOR_ACTIVATE = register("redstone_sculk_sensor_activate");
	}

	private static GameEvent register(String id) {
		return Registry.register(Registry.GAME_EVENT, id, new GameEvent(id, 16));
	}
}
