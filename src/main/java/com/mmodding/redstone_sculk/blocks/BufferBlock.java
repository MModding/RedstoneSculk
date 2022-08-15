package com.mmodding.redstone_sculk.blocks;

import com.mmodding.mmodding_lib.library.blocks.BlockRegistrable;
import com.mmodding.mmodding_lib.library.blocks.BlockWithItem;
import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;

import java.util.concurrent.atomic.AtomicBoolean;

public class BufferBlock extends RepeaterBlock implements BlockRegistrable, BlockWithItem {

	private final AtomicBoolean registered = new AtomicBoolean(false);

	private BlockItem item = null;

	public BufferBlock(AbstractBlock.Settings settings, boolean hasItem, ItemGroup itemGroup) {
		this(settings, hasItem, itemGroup != null ? new QuiltItemSettings().group(itemGroup) : new QuiltItemSettings());
	}

	public BufferBlock(Settings settings, boolean hasItem, Item.Settings itemSettings) {
		super(settings);
		if (hasItem) this.item = new BlockItem(this, itemSettings);
	}

	@Override
	public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		if (!state.get(POWERED)) {
			return 0;
		} else {
			return state.get(FACING) == direction ? getInputRedstone(state, world, pos, direction) : 0;
		}
	}

	private int getInputRedstone(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		if (this.isValidInput(state)) {
			if (state.isOf(Blocks.REDSTONE_BLOCK)) {
				return 15;
			} else {
				return state.isOf(Blocks.REDSTONE_WIRE) ? state.get(RedstoneWireBlock.POWER) : this.getStrongRedstonePower(state, world, pos, direction);
			}
		} else {
			return 0;
		}
	}

	@Override
	public BlockItem getItem() {
		return this.item;
	}

	@Override
	public boolean isNotRegistered() {
		return !this.registered.get();
	}

	@Override
	public void setRegistered() {
		this.registered.set(true);
	}
}
