package com.mmodding.redstone_skulk;

import net.minecraft.util.Identifier;

public class Utils {

	public static final String modIdentifier = "redstone_skulk";

	public static Identifier newIdentifier(String path) {
		return new Identifier(Utils.modIdentifier, path);
	}

}
