package com.mmodding.redstone_sculk.init;

import com.mmodding.mmodding_lib.library.blockentities.CustomBlockEntityType;
import com.mmodding.mmodding_lib.library.initializers.ElementsInitializer;
import com.mmodding.redstone_sculk.Utils;
import com.mmodding.redstone_sculk.blocks.entities.RedstoneSculkSensorBlockEntity;

public class BlockEntities implements ElementsInitializer {

	public static final CustomBlockEntityType<RedstoneSculkSensorBlockEntity> REDSTONE_SCULK_SENSOR_BLOCK_ENTITY = new
			CustomBlockEntityType<>(RedstoneSculkSensorBlockEntity::new, Blocks.REDSTONE_SCULK_SENSOR)
			.createAndRegister(Utils.newIdentifier("redstone_sculk_sensor"));

	@Override
	public void register() {}
}
