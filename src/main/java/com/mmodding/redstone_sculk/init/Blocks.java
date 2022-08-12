package com.mmodding.redstone_sculk.init;

import com.mmodding.mmodding_lib.library.blocks.CustomStairsBlock;
import com.mmodding.mmodding_lib.library.initializers.ClientElementsInitializer;
import com.mmodding.mmodding_lib.library.initializers.ElementsInitializer;
import com.mmodding.redstone_sculk.Utils;
import net.minecraft.block.Material;
import net.minecraft.item.ItemGroup;
import org.quiltmc.qsl.block.extensions.api.QuiltBlockSettings;

public class Blocks implements ElementsInitializer, ClientElementsInitializer {

	public static final CustomStairsBlock REINFORCED_GRAVITY_STAIRS = new CustomStairsBlock(
			net.minecraft.block.Blocks.STONE.getDefaultState(),
			QuiltBlockSettings.of(Material.METAL)
					.strength(-1, 3600000)
					.luminance(5)
					.nonOpaque(),
			true,
			ItemGroup.BUILDING_BLOCKS
	);

	@Override
	public void register() {
		REINFORCED_GRAVITY_STAIRS.register(Utils.newIdentifier("reinforced_gravity_stairs"));
	}

	@Override
	public void registerClient() {
		REINFORCED_GRAVITY_STAIRS.cutout();

		REINFORCED_GRAVITY_STAIRS.translucent();
	}
}
