package com.mmodding.redstone_sculk.blocks.entities;

import com.mmodding.redstone_sculk.blocks.RedstoneSculkSensorBlock;
import com.mmodding.redstone_sculk.init.BlockEntities;
import com.mmodding.redstone_sculk.world.listener.RedstoneSculkSensorListener;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.event.BlockPositionSource;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.listener.GameEventListener;

import javax.annotation.Nullable;

public class RedstoneSculkSensorBlockEntity extends BlockEntity implements RedstoneSculkSensorListener.Callback {

	private final RedstoneSculkSensorListener listener;
	private int lastVibrationFrequency;

	public RedstoneSculkSensorBlockEntity(BlockPos blockPos, BlockState blockState) {
		super(BlockEntities.REDSTONE_SCULK_SENSOR_BLOCK_ENTITY.getBlockEntityTypeIfCreated(), blockPos, blockState);
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

	public RedstoneSculkSensorListener getListener() {
		return this.listener;
	}

	public int getLastVibrationFrequency() {
		return this.lastVibrationFrequency;
	}

	@Override
	public boolean accepts(World world, GameEventListener listener, BlockPos pos, GameEvent event, @Nullable Entity entity) {
		boolean bl = event == GameEvent.BLOCK_DESTROY && pos.equals(this.getPos());
		boolean bl2 = event == GameEvent.BLOCK_PLACE && pos.equals(this.getPos());
		return !bl && !bl2 && RedstoneSculkSensorBlock.isInactive(this.getCachedState());
	}

	@Override
	public void accept(World world, GameEventListener listener, GameEvent event, int distance) {
		BlockState blockState = this.getCachedState();
		if (!world.isClient() && RedstoneSculkSensorBlock.isInactive(blockState)) {
			this.lastVibrationFrequency = 0;
			RedstoneSculkSensorBlock.setActive(world, this.pos, blockState, getPower(distance, listener.getRange()));
		}
	}

	public static int getPower(int distance, int range) {
		double d = (double) distance / (double) range;
		return Math.max(1, 15 - MathHelper.floor(d * 15.0));
	}
}
