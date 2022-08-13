package com.mmodding.redstone_sculk.blocks;

import com.mmodding.mmodding_lib.library.blocks.CustomBlockWithEntity;
import com.mmodding.redstone_sculk.blocks.entities.RedstoneSculkSensorBlockEntity;
import com.mmodding.redstone_sculk.init.BlockEntities;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.SculkSensorPhase;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.DustColorTransitionParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.event.listener.GameEventListener;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class RedstoneSculkSensorBlock extends CustomBlockWithEntity implements Waterloggable {

	public static final int ACTIVE_TICKS = 40;
	public static final int COOLDOWN_TICKS = 1;
	public static final EnumProperty<SculkSensorPhase> REDSTONE_SCULK_SENSOR_PHASE = EnumProperty.of("redstone_sculk_sensor_phase", SculkSensorPhase.class);
	public static final IntProperty POWER = Properties.POWER;
	public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
	protected static final VoxelShape OUTLINE_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);
	private final int range;

	public RedstoneSculkSensorBlock(AbstractBlock.Settings settings, int i) {
		super(settings);
		this.setDefaultState(
				this.stateManager
						.getDefaultState()
						.with(REDSTONE_SCULK_SENSOR_PHASE, SculkSensorPhase.INACTIVE)
						.with(POWER, 0)
						.with(WATERLOGGED, Boolean.FALSE)
		);
		this.range = i;
	}

	public int getRange() {
		return this.range;
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		BlockPos blockPos = ctx.getBlockPos();
		FluidState fluidState = ctx.getWorld().getFluidState(blockPos);
		return this.getDefaultState().with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (getPhase(state) != SculkSensorPhase.ACTIVE) {
			if (getPhase(state) == SculkSensorPhase.COOLDOWN) {
				world.setBlockState(pos, state.with(REDSTONE_SCULK_SENSOR_PHASE, SculkSensorPhase.INACTIVE), 3);
			}

		} else {
			setCooldown(world, pos, state);
		}
	}

	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
		if (!world.isClient() && !state.isOf(oldState.getBlock())) {
			if (state.get(POWER) > 0 && !world.getBlockTickScheduler().isQueued(pos, this)) {
				world.setBlockState(pos, state.with(POWER, 0), 18);
			}

			world.scheduleBlockTick(new BlockPos(pos), state.getBlock(), 1);
		}
	}

	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (!state.isOf(newState.getBlock())) {
			if (getPhase(state) == SculkSensorPhase.ACTIVE) {
				updateNeighbors(world, pos);
			}

			super.onStateReplaced(state, world, pos, newState, moved);
		}
	}

	@Override
	public BlockState getStateForNeighborUpdate(
			BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos
	) {
		if (state.get(WATERLOGGED)) {
			world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
		}

		return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	private static void updateNeighbors(World world, BlockPos pos) {
		world.updateNeighborsAlways(pos, Blocks.SCULK_SENSOR);
		world.updateNeighborsAlways(pos.offset(Direction.UP.getOpposite()), Blocks.SCULK_SENSOR);
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new RedstoneSculkSensorBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> GameEventListener getGameEventListener(World world, T blockEntity) {
		return blockEntity instanceof RedstoneSculkSensorBlockEntity ? ((RedstoneSculkSensorBlockEntity) blockEntity).getEventListener() : null;
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
		return !world.isClient
				? checkType(type, BlockEntities.REDSTONE_SCULK_SENSOR_BLOCK_ENTITY.getBlockEntityTypeIfCreated(), (worldx, pos, statex, blockEntity) -> blockEntity.getEventListener().tick(worldx))
				: null;
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return OUTLINE_SHAPE;
	}

	@Override
	public boolean emitsRedstonePower(BlockState state) {
		return true;
	}

	@Override
	public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		return state.get(POWER);
	}

	public static SculkSensorPhase getPhase(BlockState state) {
		return state.get(REDSTONE_SCULK_SENSOR_PHASE);
	}

	public static boolean isInactive(BlockState state) {
		return getPhase(state) == SculkSensorPhase.INACTIVE;
	}

	public static void setCooldown(World world, BlockPos pos, BlockState state) {
		world.setBlockState(pos, state.with(REDSTONE_SCULK_SENSOR_PHASE, SculkSensorPhase.COOLDOWN).with(POWER, 0), 3);
		world.scheduleBlockTick(new BlockPos(pos), state.getBlock(), 1);
		if (!state.get(WATERLOGGED)) {
			world.playSound(null, pos, SoundEvents.BLOCK_SCULK_SENSOR_CLICKING_STOP, SoundCategory.BLOCKS, 1.0F, world.random.nextFloat() * 0.2F + 0.8F);
		}

		updateNeighbors(world, pos);
	}

	public static void setActive(World world, BlockPos pos, BlockState state, int power) {
		world.setBlockState(pos, state.with(REDSTONE_SCULK_SENSOR_PHASE, SculkSensorPhase.ACTIVE).with(POWER, power), 3);
		world.scheduleBlockTick(new BlockPos(pos), state.getBlock(), 40);
		updateNeighbors(world, pos);
		if (!state.get(WATERLOGGED)) {
			world.playSound(
					null,
					(double) pos.getX() + 0.5,
					(double) pos.getY() + 0.5,
					(double) pos.getZ() + 0.5,
					SoundEvents.BLOCK_SCULK_SENSOR_CLICKING,
					SoundCategory.BLOCKS,
					1.0F,
					world.random.nextFloat() * 0.2F + 0.8F
			);
		}

	}

	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		if (getPhase(state) == SculkSensorPhase.ACTIVE) {
			Direction direction = Direction.random(random);
			if (direction != Direction.UP && direction != Direction.DOWN) {
				double d = (double) pos.getX() + 0.5 + (direction.getOffsetX() == 0 ? 0.5 - random.nextDouble() : (double) direction.getOffsetX() * 0.6);
				double e = (double) pos.getY() + 0.25;
				double f = (double) pos.getZ() + 0.5 + (direction.getOffsetZ() == 0 ? 0.5 - random.nextDouble() : (double) direction.getOffsetZ() * 0.6);
				double g = (double) random.nextFloat() * 0.04;
				world.addParticle(DustColorTransitionParticleEffect.DEFAULT, d, e, f, 0.0, g, 0.0);
			}
		}
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(REDSTONE_SCULK_SENSOR_PHASE, POWER, WATERLOGGED);
	}

	@Override
	public boolean hasComparatorOutput(BlockState state) {
		return true;
	}

	@Override
	public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof RedstoneSculkSensorBlockEntity sculkSensorBlockEntity) {
			return getPhase(state) == SculkSensorPhase.ACTIVE ? sculkSensorBlockEntity.getLastVibrationFrequency() : 0;
		} else {
			return 0;
		}
	}

	@Override
	public boolean canPathfindThrough(BlockState state, BlockView blockView, BlockPos pos, NavigationType type) {
		return false;
	}

	@Override
	public boolean hasSidedTransparency(BlockState state) {
		return true;
	}
}
