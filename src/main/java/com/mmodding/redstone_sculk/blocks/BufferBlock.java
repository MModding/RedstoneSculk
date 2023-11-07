package com.mmodding.redstone_sculk.blocks;

import com.mmodding.mmodding_lib.library.blocks.BlockRegistrable;
import com.mmodding.mmodding_lib.library.blocks.BlockWithItem;
import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.TickPriority;
import net.minecraft.world.World;
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class BufferBlock extends RepeaterBlock implements BlockRegistrable, BlockWithItem {

	private final AtomicBoolean registered = new AtomicBoolean(false);

	private BlockItem item = null;

	private int power;

	public BufferBlock(AbstractBlock.Settings settings, boolean hasItem, ItemGroup itemGroup) {
		this(settings, hasItem, itemGroup != null ? new QuiltItemSettings().group(itemGroup) : new QuiltItemSettings());
	}

	public BufferBlock(Settings settings, boolean hasItem, Item.Settings itemSettings) {
		super(settings);
		if (hasItem) this.item = new BlockItem(this, itemSettings);
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		this.update(world, pos, state);
		if (!this.isLocked(world, pos, state)) {
			boolean bl = state.get(POWERED);
			boolean bl2 = this.hasPower(world, pos, state);
			if (bl && !bl2) {
				world.setBlockState(pos, state.with(POWERED, Boolean.FALSE), 2);
			} else if (!bl) {
				world.setBlockState(pos, state.with(POWERED, Boolean.TRUE), 2);
				if (!bl2) {
					world.scheduleBlockTick(pos, this, this.getUpdateDelayInternal(state), TickPriority.VERY_HIGH);
				}
			}

		}
	}

	private void update(World world, BlockPos pos, BlockState state) {
		this.power = this.getPower(world, pos, state);
	}

	@Override
	protected int getOutputLevel(BlockView world, BlockPos pos, BlockState state) {
		if (this.isValidInput(state)) {
			if (state.isOf(Blocks.REDSTONE_BLOCK)) {
				return 15;
			} else {
				return state.isOf(Blocks.REDSTONE_BLOCK) ? state.get(RedstoneWireBlock.POWER) : this.power;
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
