package com.mmodding.redstone_sculk;

import net.minecraft.util.Identifier;

public class Utils {

	public static final String modIdentifier = "redstone_sculk";

	public static Identifier newIdentifier(String path) {
		return new Identifier(Utils.modIdentifier, path);
	}

}
