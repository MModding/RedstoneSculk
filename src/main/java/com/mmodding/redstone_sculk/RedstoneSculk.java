package com.mmodding.redstone_sculk;

import com.mmodding.mmodding_lib.library.base.AdvancedModContainer;
import com.mmodding.mmodding_lib.library.base.MModdingModInitializer;
import com.mmodding.mmodding_lib.library.config.Config;
import com.mmodding.mmodding_lib.library.initializers.ElementsInitializer;
import com.mmodding.mmodding_lib.library.utils.GameEventUtils;
import com.mmodding.redstone_sculk.init.BlockEntities;
import com.mmodding.redstone_sculk.init.Blocks;
import com.mmodding.redstone_sculk.init.GameEvents;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class RedstoneSculk implements MModdingModInitializer {

	@Override
	public List<ElementsInitializer> getElementsInitializers() {
		List<ElementsInitializer> initializers = new ArrayList<>();
		initializers.add(new Blocks());
		initializers.add(new GameEvents());
		initializers.add(new BlockEntities());
		return initializers;
	}

	@Nullable
	@Override
	public Config getConfig() {
		return null;
	}

	@Override
	public void onInitialize(AdvancedModContainer mod) {
		GameEventUtils.putGameEventInFrequencies(GameEvents.REDSTONE_SCULK_SENSOR_ACTIVATE, 20);
	}
}
