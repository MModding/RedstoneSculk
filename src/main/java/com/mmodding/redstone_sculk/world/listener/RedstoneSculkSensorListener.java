package com.mmodding.redstone_sculk.world.listener;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockStateRaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.PositionSource;
import net.minecraft.world.event.listener.SculkSensorListener;

import java.util.Optional;

public class RedstoneSculkSensorListener extends SculkSensorListener {
	protected BlockPos originSource;

	public RedstoneSculkSensorListener(PositionSource positionSource, int range, SculkSensorListener.Callback callback) {
		super(positionSource, range, callback, null, 0.0F, 0);
	}

	public BlockPos getOriginSource() {
		return this.originSource;
	}

	@Override
	public boolean listen(ServerWorld world, GameEvent.Message eventMessage) {
		if (this.event != null) {
			return false;
		} else {
			GameEvent gameEvent = eventMessage.getEvent();
			GameEvent.Context context = eventMessage.getContext();
			if (!this.callback.isValid(gameEvent, context)) {
				return false;
			} else {
				Optional<Vec3d> optional = this.positionSource.getPos(world);
				if (optional.isEmpty()) {
					return false;
				} else {
					Vec3d vec3d = eventMessage.getSourcePos();
					this.originSource = new BlockPos(vec3d);
					Vec3d vec3d2 = optional.get();
					if (!this.callback.accepts(world, this, new BlockPos(vec3d), gameEvent, context)) {
						return false;
					} else if (this.isOccluded(world, vec3d, vec3d2)) {
						return false;
					} else {
						this.listen(world, gameEvent, context, vec3d, vec3d2);
						return true;
					}
				}
			}
		}
	}

	private boolean isOccluded(World world, Vec3d posA, Vec3d posB) {
		BlockPos pos = new BlockPos(posA);
		BlockPos sourcePos = new BlockPos(posB);
		return world.raycast(new BlockStateRaycastContext(Vec3d.ofCenter(pos), Vec3d.ofCenter(sourcePos), state -> state.isIn(BlockTags.OCCLUDES_VIBRATION_SIGNALS)))
			.getType()
			== HitResult.Type.BLOCK;
	}
}
