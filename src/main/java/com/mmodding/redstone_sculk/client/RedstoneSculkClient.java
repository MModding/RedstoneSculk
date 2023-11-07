package com.mmodding.redstone_sculk.client;

import com.mmodding.mmodding_lib.library.base.AdvancedModContainer;
import com.mmodding.mmodding_lib.library.base.MModdingClientModInitializer;
import com.mmodding.mmodding_lib.library.config.Config;
import com.mmodding.mmodding_lib.library.initializers.ClientElementsInitializer;
import com.mmodding.redstone_sculk.init.Blocks;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.loader.api.minecraft.ClientOnly;

import java.util.ArrayList;
import java.util.List;

@ClientOnly
public class RedstoneSculkClient implements MModdingClientModInitializer {

	@Override
	public List<ClientElementsInitializer> getClientElementsInitializers() {
		List<ClientElementsInitializer> clientInitializers = new ArrayList<>();
		clientInitializers.add(new Blocks());
		return clientInitializers;
	}

	@Nullable
	@Override
	public Config getClientConfig() {
		return null;
	}

	@Override
	public void onInitializeClient(AdvancedModContainer modContainer) {}
}
