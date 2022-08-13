package com.mmodding.redstone_sculk.init;

import com.mmodding.mmodding_lib.library.blocks.CustomBlockWithEntity;
import com.mmodding.mmodding_lib.library.initializers.ClientElementsInitializer;
import com.mmodding.mmodding_lib.library.initializers.ElementsInitializer;
import com.mmodding.redstone_sculk.Utils;
import com.mmodding.redstone_sculk.blocks.RedstoneSculkSensorBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.block.enums.SculkSensorPhase;
import net.minecraft.sound.BlockSoundGroup;

public class Blocks implements ElementsInitializer, ClientElementsInitializer {

	public static final CustomBlockWithEntity REDSTONE_SCULK_SENSOR = new RedstoneSculkSensorBlock(
			AbstractBlock.Settings.of(Material.SCULK, MapColor.CYAN)
					.strength(1.5F)
					.sounds(BlockSoundGroup.SCULK_SENSOR)
					.luminance(state -> 1)
					.emissiveLighting((state, world, pos) -> RedstoneSculkSensorBlock.getPhase(state) == SculkSensorPhase.ACTIVE),
			8
	);

	@Override
	public void register() {
		REDSTONE_SCULK_SENSOR.register(Utils.newIdentifier("redstone_sculk_sensor"));
	}

	@Override
	public void registerClient() {
		REDSTONE_SCULK_SENSOR.cutout();

		REDSTONE_SCULK_SENSOR.translucent();
	}
}
