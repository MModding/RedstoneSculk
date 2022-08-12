package com.mmodding.redstone_skulk.init;

import com.mmodding.redstone_skulk.Utils;
import com.mmodding.mmodding_lib.library.blocks.CustomBlock;
import com.mmodding.mmodding_lib.library.blocks.CustomStairsBlock;
import com.mmodding.mmodding_lib.library.initializers.ClientElementsInitializer;
import com.mmodding.mmodding_lib.library.initializers.ElementsInitializer;
import net.minecraft.block.Material;
import org.quiltmc.qsl.block.extensions.api.QuiltBlockSettings;

public class Blocks implements ElementsInitializer, ClientElementsInitializer {

	public static final CustomStairsBlock REINFORCED_GRAVITY_STAIRS = new CustomStairsBlock(
			REINFORCED_GRAVITY_BLOCK.getDefaultState(),
			QuiltBlockSettings.of(Material.METAL)
					.strength(-1, 3600000)
					.luminance(5)
					.nonOpaque(),
			true,
			Tabs.ECHOES_OF_GRAVITY_CHAPTER_I
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
