package com.mmodding.redstone_sculk.client;

import com.mmodding.mmodding_lib.library.base.MModdingClientModInitializer;
import com.mmodding.mmodding_lib.library.initializers.ClientElementsInitializer;
import com.mmodding.redstone_sculk.init.Blocks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.quiltmc.loader.api.ModContainer;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class RedstoneSculkClient implements MModdingClientModInitializer {

	@Override
	public List<ClientElementsInitializer> getClientElementsInitializers() {
		List<ClientElementsInitializer> clientInitializers = new ArrayList<>();
		clientInitializers.add(new Blocks());
		return clientInitializers;
	}

	@Override
	public void onInitializeClient(ModContainer modContainer) {
		MModdingClientModInitializer.super.onInitializeClient(modContainer);
	}
}
