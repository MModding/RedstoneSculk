package com.mmodding.redstone_sculk.init;

import com.mmodding.mmodding_lib.library.initializers.ElementsInitializer;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.event.GameEvent;

public class GameEvents implements ElementsInitializer {

	public static GameEvent REDSTONE_SCULK_SENSOR_ACTIVATE;

	@Override
	public void register() {
		REDSTONE_SCULK_SENSOR_ACTIVATE = register("redstone_sculk:redstone_sculk_sensor_activate");
	}

	private static GameEvent register(String id) {
		return Registry.register(Registry.GAME_EVENT, id, new GameEvent(id, 16));
	}
}
