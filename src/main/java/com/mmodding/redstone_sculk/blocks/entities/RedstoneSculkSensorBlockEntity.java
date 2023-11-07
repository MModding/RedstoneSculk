package com.mmodding.redstone_sculk.blocks.entities;

import com.mmodding.redstone_sculk.blocks.RedstoneSculkSensorBlock;
import com.mmodding.redstone_sculk.init.BlockEntities;
import com.mmodding.redstone_sculk.init.GameEvents;
import com.mmodding.redstone_sculk.world.listener.RedstoneSculkSensorListener;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.event.BlockPositionSource;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.listener.GameEventListener;
import net.minecraft.world.event.listener.SculkSensorListener;
import org.jetbrains.annotations.Nullable;

public class RedstoneSculkSensorBlockEntity extends BlockEntity implements SculkSensorListener.Callback {

	private final RedstoneSculkSensorListener listener;
	private int lastVibrationFrequency;

	public RedstoneSculkSensorBlockEntity(BlockPos blockPos, BlockState blockState) {
		super(BlockEntities.REDSTONE_SCULK_SENSOR, blockPos, blockState);
		this.listener = new RedstoneSculkSensorListener(new BlockPositionSource(this.pos),
			((RedstoneSculkSensorBlock) blockState.getBlock()).getRange(), this);
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);
		this.lastVibrationFrequency = nbt.getInt("last_vibration_frequency");
	}

	@Override
	protected void writeNbt(NbtCompound nbt) {
		super.writeNbt(nbt);
		nbt.putInt("last_vibration_frequency", this.lastVibrationFrequency);
	}

	public RedstoneSculkSensorListener getEventListener() {
		return this.listener;
	}

	public int getLastVibrationFrequency() {
		return this.lastVibrationFrequency;
	}

	@Override
	public boolean accepts(ServerWorld world, GameEventListener listener, BlockPos pos, GameEvent event, @Nullable GameEvent.Context context) {
		return RedstoneSculkSensorBlock.isInactive(this.getCachedState()) &&
			event == GameEvents.REDSTONE_SCULK_SENSOR_ACTIVATE;
	}

	@Override
	public void accept(ServerWorld world, GameEventListener listener, BlockPos pos, GameEvent event, @Nullable Entity entity, @Nullable Entity sourceEntity, float distance) {
		BlockState blockState = this.getCachedState();
		if (!world.isClient() && RedstoneSculkSensorBlock.isInactive(blockState)) {
			this.lastVibrationFrequency = RedstoneSculkSensorBlock.FREQUENCIES.getInt(event);
			RedstoneSculkSensorBlock.setActive(world, this.pos, blockState,
				world.getReceivedRedstonePower(getEventListener().getOriginSource()));
		}
	}
}
