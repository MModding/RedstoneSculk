package com.mmodding.redstone_sculk.world.listener;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.particle.VibrationParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.GameEventTags;
import net.minecraft.tag.TagKey;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockStateRaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.PositionSource;
import net.minecraft.world.event.listener.GameEventListener;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class RedstoneSculkSensorListener implements GameEventListener {
	protected final PositionSource positionSource;
	protected BlockPos originSource;
	protected final int range;
	protected final RedstoneSculkSensorListener.Callback callback;
	@Nullable
	protected RedstoneSculkSensorListener.RecievedEvent event;
	protected float distance;
	protected int delay;

	public RedstoneSculkSensorListener(PositionSource positionSource, int range, RedstoneSculkSensorListener.Callback callback) {
		this.positionSource = positionSource;
		this.range = range;
		this.callback = callback;
		this.distance = 0.0F;
		this.delay = 0;
	}

	public void tick(World world) {
		if (world instanceof ServerWorld serverWorld) {
			--this.delay;
			if (this.delay <= 0) {
				this.delay = 0;
				this.callback.accept(serverWorld, this, new BlockPos(this.event.pos), this.event.gameEvent, this.event.getEntity(serverWorld).orElse(null), this.event.getProjectileOwner(serverWorld).orElse(null), this.distance);
				this.event = null;
			}
		}
	}

	@Override
	public PositionSource getPositionSource() {
		return this.positionSource;
	}

	public BlockPos getOriginSource() {
		return this.originSource;
	}

	@Override
	public int getRange() {
		return this.range;
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

	private void listen(ServerWorld world, GameEvent event, GameEvent.Context context, Vec3d start, Vec3d end) {
		this.distance = (float) start.distanceTo(end);
		this.event = new RecievedEvent(event, this.distance, start, context.sourceEntity());
		this.delay = MathHelper.floor(this.distance);
		world.spawnParticles(new VibrationParticleEffect(this.positionSource, this.delay), start.x, start.y, start.z, 1, 0.0, 0.0, 0.0, 0.0);
		this.callback.onListen();
	}

	private boolean isOccluded(World world, Vec3d posA, Vec3d posB) {
		BlockPos pos = new BlockPos(posA);
		BlockPos sourcePos = new BlockPos(posB);
		return world.raycast(new BlockStateRaycastContext(Vec3d.ofCenter(pos), Vec3d.ofCenter(sourcePos), state -> state.isIn(BlockTags.OCCLUDES_VIBRATION_SIGNALS)))
			.getType()
			== HitResult.Type.BLOCK;
	}

	public interface Callback {
		default TagKey<GameEvent> getTag() {
			return GameEventTags.VIBRATIONS;
		}

		default boolean triggersAvoidCriterion() {
			return false;
		}

		default boolean isValid(GameEvent event, GameEvent.Context context) {
			if (!event.isIn(this.getTag())) {
				return false;
			} else {
				Entity entity = context.sourceEntity();
				if (entity != null) {
					if (entity.isSpectator()) {
						return false;
					}

					if (entity.bypassesSteppingEffects() && event.isIn(GameEventTags.IGNORE_VIBRATIONS_SNEAKING)) {
						if (this.triggersAvoidCriterion() && entity instanceof ServerPlayerEntity) {
							ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) entity;
							Criteria.AVOID_VIBRATION.trigger(serverPlayerEntity);
						}

						return false;
					}

					if (entity.dampensVibrations()) {
						return false;
					}
				}

				if (context.affectedState() != null) {
					return !context.affectedState().isIn(BlockTags.DAMPENS_VIBRATIONS);
				} else {
					return true;
				}
			}
		}

		boolean accepts(ServerWorld world, GameEventListener listener, BlockPos pos, GameEvent event, GameEvent.Context context);

		void accept(ServerWorld world, GameEventListener listener, BlockPos pos, GameEvent event, @Nullable Entity entity, @Nullable Entity sourceEntity, float distance);

		default void onListen() {}
	}

	public record RecievedEvent(GameEvent gameEvent, float distance, Vec3d pos, @Nullable UUID source,
								@Nullable UUID projectileOwnerUuid, @Nullable Entity entity) {

		public RecievedEvent(GameEvent gameEvent, float distance, Vec3d pos, @Nullable Entity entity) {
			this(gameEvent, distance, pos, entity == null ? null : entity.getUuid(), getProjectileOwnerUuid(entity), entity);
		}

		@Nullable
		private static UUID getProjectileOwnerUuid(@Nullable Entity entity) {
			if (entity instanceof ProjectileEntity projectileEntity) {
				if (projectileEntity.getOwner() != null) {
					return projectileEntity.getOwner().getUuid();
				}
			}

			return null;
		}

		public Optional<Entity> getEntity(ServerWorld world) {
			return Optional.ofNullable(this.entity).or(() -> {
				Optional<UUID> var10000 = Optional.ofNullable(this.source);
				Objects.requireNonNull(world);
				return var10000.map(world::getEntity);
			});
		}

		public Optional<Entity> getProjectileOwner(ServerWorld world) {
			return this.getEntity(world).filter((entity) -> entity instanceof ProjectileEntity).map((entity) -> (ProjectileEntity) entity).map(ProjectileEntity::getOwner).or(() -> {
				Optional<UUID> var10000 = Optional.ofNullable(this.projectileOwnerUuid);
				Objects.requireNonNull(world);
				return var10000.map(world::getEntity);
			});
		}

		public GameEvent gameEvent() {
			return this.gameEvent;
		}

		public float distance() {
			return this.distance;
		}

		public Vec3d pos() {
			return this.pos;
		}

		@Nullable
		public UUID source() {
			return this.source;
		}

		@Nullable
		public UUID projectileOwnerUuid() {
			return this.projectileOwnerUuid;
		}

		@Nullable
		public Entity entity() {
			return this.entity;
		}
	}
}
