package com.mmodding.redstone_sculk;

import com.mmodding.mmodding_lib.library.base.MModdingModContainer;
import com.mmodding.mmodding_lib.library.base.MModdingModInitializer;
import com.mmodding.mmodding_lib.library.initializers.ElementsInitializer;
import com.mmodding.mmodding_lib.library.utils.GameEventUtils;
import com.mmodding.redstone_sculk.init.BlockEntities;
import com.mmodding.redstone_sculk.init.Blocks;
import com.mmodding.redstone_sculk.init.GameEvents;
import org.quiltmc.loader.api.ModContainer;

import java.util.ArrayList;
import java.util.List;

public class RedstoneSculk implements MModdingModInitializer {

	public static MModdingModContainer mod;

	@Override
	public List<ElementsInitializer> getElementsInitializers() {
		List<ElementsInitializer> initializers = new ArrayList<>();
		initializers.add(new Blocks());
		initializers.add(new BlockEntities());
		return initializers;
	}

	@Override
	public void onInitialize(ModContainer mod) {
		MModdingModInitializer.super.onInitialize(mod);
		RedstoneSculk.mod = MModdingModContainer.from(mod);

		// GameEvents
		GameEvents.registerGameEvents();

		GameEventUtils.putGameEventInFrequencies(GameEvents.REDSTONE_SCULK_SENSOR_ACTIVATE, 15);
	}
}
