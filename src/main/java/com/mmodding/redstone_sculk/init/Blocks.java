package com.mmodding.redstone_sculk.init;

import com.mmodding.mmodding_lib.library.initializers.ClientElementsInitializer;
import com.mmodding.mmodding_lib.library.initializers.ElementsInitializer;
import com.mmodding.redstone_sculk.RedstoneSculk;
import com.mmodding.redstone_sculk.blocks.BufferBlock;
import com.mmodding.redstone_sculk.blocks.DetectorBlock;
import com.mmodding.redstone_sculk.blocks.RedstoneSculkSensorBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.block.enums.SculkSensorPhase;
import net.minecraft.item.ItemGroup;
import net.minecraft.sound.BlockSoundGroup;

public class Blocks implements ElementsInitializer, ClientElementsInitializer {

	public static final RedstoneSculkSensorBlock REDSTONE_SCULK_SENSOR = new RedstoneSculkSensorBlock(
		AbstractBlock.Settings.of(Material.SCULK, MapColor.CYAN)
			.strength(1.5F)
			.sounds(BlockSoundGroup.SCULK_SENSOR)
			.luminance(state -> 1)
			.emissiveLighting((state, world, pos) -> RedstoneSculkSensorBlock.getPhase(state) == SculkSensorPhase.ACTIVE),
		true,
		ItemGroup.BUILDING_BLOCKS,
		8
	);

	public static final BufferBlock BUFFER = new BufferBlock(
		AbstractBlock.Settings.of(Material.DECORATION)
			.breakInstantly()
			.sounds(BlockSoundGroup.WOOD),
		true,
		ItemGroup.REDSTONE
	);

	public static final DetectorBlock DETECTOR = new DetectorBlock(
		AbstractBlock.Settings.of(Material.STONE)
			.strength(3.0F)
			.requiresTool()
			.solidBlock((state, world, pos) -> false),
		true,
		ItemGroup.REDSTONE
	);

	@Override
	public void register() {
		REDSTONE_SCULK_SENSOR.register(RedstoneSculk.createId("redstone_sculk_sensor"));
		BUFFER.register(RedstoneSculk.createId("buffer"));
		DETECTOR.register(RedstoneSculk.createId("detector"));
	}

	@Override
	public void registerClient() {
		REDSTONE_SCULK_SENSOR.cutout();
		BUFFER.cutout();

		REDSTONE_SCULK_SENSOR.translucent();
	}
}
