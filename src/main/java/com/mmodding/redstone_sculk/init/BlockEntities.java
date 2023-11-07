package com.mmodding.redstone_sculk.init;

import com.mmodding.mmodding_lib.library.blockentities.CustomBlockEntityType;
import com.mmodding.mmodding_lib.library.initializers.ElementsInitializer;
import com.mmodding.redstone_sculk.RedstoneSculk;
import com.mmodding.redstone_sculk.blocks.entities.RedstoneSculkSensorBlockEntity;

public class BlockEntities implements ElementsInitializer {

	public static final CustomBlockEntityType<RedstoneSculkSensorBlockEntity> REDSTONE_SCULK_SENSOR = CustomBlockEntityType.create(
		RedstoneSculkSensorBlockEntity::new, null, Blocks.REDSTONE_SCULK_SENSOR
	);

	@Override
	public void register() {
		RedstoneSculk.createId("redstone_sculk_sensor");
	}
}
