package com.mmodding.redstone_sculk.init;

import com.mmodding.mmodding_lib.library.blockentities.CustomBlockEntityType;
import com.mmodding.redstone_sculk.blocks.entities.RedstoneSculkSensorBlockEntity;
import net.minecraft.util.Identifier;

public class BlockEntities {

	public static final CustomBlockEntityType<RedstoneSculkSensorBlockEntity> REDSTONE_SCULK_SENSOR_BLOCK_ENTITY = new
			CustomBlockEntityType<>(RedstoneSculkSensorBlockEntity::new, Blocks.REDSTONE_SCULK_SENSOR)
			.createAndRegister(new Identifier("redstone_sculk_sensor"));
}
