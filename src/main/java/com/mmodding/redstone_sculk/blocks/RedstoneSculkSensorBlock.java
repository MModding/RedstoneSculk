package com.mmodding.redstone_sculk.blocks;

import com.mmodding.mmodding_lib.library.blocks.BlockRegistrable;
import com.mmodding.mmodding_lib.library.blocks.BlockWithItem;
import com.mmodding.redstone_sculk.blockentities.RedstoneSculkSensorBlockEntity;
import com.mmodding.redstone_sculk.init.BlockEntities;
import com.mmodding.redstone_sculk.init.Blocks;
import com.mmodding.redstone_sculk.init.GameEvents;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SculkSensorBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.SculkSensorPhase;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.listener.GameEventListener;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;

import java.util.concurrent.atomic.AtomicBoolean;

public class RedstoneSculkSensorBlock extends SculkSensorBlock implements BlockRegistrable, BlockWithItem {

	private final AtomicBoolean registered = new AtomicBoolean(false);

	private BlockItem item = null;

	public static final Object2IntMap<GameEvent> FREQUENCIES = Object2IntMaps.unmodifiable(Util.make(new Object2IntOpenHashMap<>(), map ->
			map.put(GameEvents.REDSTONE_SCULK_SENSOR_ACTIVATE, 15)));

	public RedstoneSculkSensorBlock(AbstractBlock.Settings settings, boolean hasItem, ItemGroup itemGroup, int i) {
		this(settings, hasItem, itemGroup != null ? new QuiltItemSettings().group(itemGroup) : new QuiltItemSettings(), i);
	}

	public RedstoneSculkSensorBlock(Settings settings, boolean hasItem, Item.Settings itemSettings, int i) {
		super(settings, i);
		if (hasItem) this.item = new BlockItem(this, itemSettings);
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
		if (!world.isClient) {
			boolean bl = getPhase(state) == SculkSensorPhase.ACTIVE;
			if (bl != world.isReceivingRedstonePower(pos)) {
				if (bl) {
					world.scheduleBlockTick(pos, this, 4);
				} else {
					setActive(world, pos, state, world.getReceivedRedstonePower(pos));
				}
			}
		}
	}

	private static void updateNeighbors(World world, BlockPos pos) {
		world.updateNeighborsAlways(pos, Blocks.REDSTONE_SCULK_SENSOR);
		world.updateNeighborsAlways(pos.offset(Direction.UP.getOpposite()), Blocks.REDSTONE_SCULK_SENSOR);
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

	public static void setActive(World world, BlockPos pos, BlockState state, int power) {
		world.setBlockState(pos, state.with(SCULK_SENSOR_PHASE, SculkSensorPhase.ACTIVE).with(POWER, power), 3);
		world.emitGameEvent(GameEvents.REDSTONE_SCULK_SENSOR_ACTIVATE, pos);
		world.scheduleBlockTick(new BlockPos(pos), state.getBlock(), ACTIVE_TICKS);
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
	public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof RedstoneSculkSensorBlockEntity sculkSensorBlockEntity) {
			return getPhase(state) == SculkSensorPhase.ACTIVE ? sculkSensorBlockEntity.getLastVibrationFrequency() : 0;
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
