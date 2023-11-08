package com.mmodding.redstone_sculk.blocks;

import com.mmodding.mmodding_lib.library.blocks.BlockRegistrable;
import com.mmodding.mmodding_lib.library.blocks.BlockWithItem;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FacingBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.random.RandomGenerator;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;

import java.util.concurrent.atomic.AtomicBoolean;

public class DetectorBlock extends FacingBlock implements BlockRegistrable, BlockWithItem {

	private final AtomicBoolean registered = new AtomicBoolean(false);
	public static final IntProperty DETECTION = IntProperty.of("detection", 0, 2);

	private BlockItem item = null;

	public DetectorBlock(AbstractBlock.Settings settings, boolean hasItem, ItemGroup itemGroup) {
		this(settings, hasItem, itemGroup != null ? new QuiltItemSettings().group(itemGroup) : new QuiltItemSettings());
	}

	public DetectorBlock(Settings settings, boolean hasItem, Item.Settings itemSettings) {
		super(settings);
		if (hasItem) this.item = new BlockItem(this, itemSettings);
		this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.SOUTH).with(DETECTION, 0));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING, DETECTION);
	}

	@Override
	public BlockState rotate(BlockState state, BlockRotation rotation) {
		return state.with(FACING, rotation.rotate(state.get(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, BlockMirror mirror) {
		return state.rotate(mirror.getRotation(state.get(FACING)));
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return this.getDefaultState().with(FACING, ctx.getPlayerLookDirection().getOpposite().getOpposite());
	}

	private Pair<Pair<Direction, Direction>, Pair<Direction, Direction>> getSidePos(BlockState state) {
		return switch (state.get(FACING)) {
			case DOWN, UP ->
				new Pair<>(new Pair<>(Direction.NORTH, Direction.SOUTH), new Pair<>(Direction.WEST, Direction.EAST));
			case NORTH, SOUTH ->
				new Pair<>(new Pair<>(Direction.WEST, Direction.EAST), new Pair<>(Direction.DOWN, Direction.UP));
			case WEST, EAST ->
				new Pair<>(new Pair<>(Direction.NORTH, Direction.SOUTH), new Pair<>(Direction.DOWN, Direction.UP));
		};
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, RandomGenerator random) {
		BlockState sourceState = world.getBlockState(pos.offset(state.get(FACING)));
		BlockState detectState = world.getBlockState(pos.offset(state.get(FACING).getOpposite()));
		int detection;
		if (sourceState == detectState) {
			detection = 2;
		} else if (sourceState.getBlock() == detectState.getBlock()) {
			detection = 1;
		} else {
			detection = 0;
		}
		world.setBlockState(pos, state.with(DETECTION, detection), Block.NOTIFY_LISTENERS);
		world.scheduleBlockTick(pos, this, 2);
		this.updateNeighbors(world, pos, state);
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
		if (!world.isClient() && !world.getBlockTickScheduler().isQueued(pos, this)) {
			world.scheduleBlockTick(pos, this, 2);
		}
		return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	protected void updateNeighbors(World world, BlockPos pos, BlockState state) {
		BlockPos side1Pos = pos.offset(getSidePos(state).getLeft().getLeft());
		BlockPos side2Pos = pos.offset(getSidePos(state).getLeft().getRight());
		BlockPos side3Pos = pos.offset(getSidePos(state).getRight().getLeft());
		BlockPos side4Pos = pos.offset(getSidePos(state).getRight().getRight());
		world.method_8492(side1Pos, this, pos);
		world.method_8492(side2Pos, this, pos);
		world.method_8492(side3Pos, this, pos);
		world.method_8492(side4Pos, this, pos);
		world.updateNeighborsExcept(pos.offset(state.get(FACING).getOpposite()), this, state.get(FACING));
	}

	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
		if (!state.isOf(oldState.getBlock())) {
			if (!world.isClient() && !world.getBlockTickScheduler().isQueued(pos, this)) {
				BlockState blockState = state.with(DETECTION, 0);
				world.setBlockState(pos, blockState, Block.NOTIFY_LISTENERS | Block.FORCE_STATE);
				this.updateNeighbors(world, pos, blockState);
			}
		}
	}

	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (!state.isOf(newState.getBlock())) {
			if (!world.isClient && world.getBlockTickScheduler().isQueued(pos, this)) {
				this.updateNeighbors(world, pos, state.with(DETECTION, 0));
			}
		}
	}

	@Override
	public boolean emitsRedstonePower(BlockState state) {
		return true;
	}

	@Override
	public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		return state.getWeakRedstonePower(world, pos, direction);
	}

	@Override
	public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		if ((state.get(FACING) != getSidePos(state).getLeft().getLeft() && state.get(FACING) != getSidePos(state).getLeft().getRight() &&
			state.get(FACING) != getSidePos(state).getRight().getLeft() && state.get(FACING) != getSidePos(state).getRight().getRight()) ||
			state.get(DETECTION) == 0) return 0;
		return state.get(DETECTION) > 1 ? 15 : 8;
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
