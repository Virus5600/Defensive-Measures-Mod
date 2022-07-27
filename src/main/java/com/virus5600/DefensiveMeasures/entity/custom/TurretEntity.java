package com.virus5600.DefensiveMeasures.entity.custom;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.control.BodyControl;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.entity.passive.AxolotlEntity;
import net.minecraft.entity.passive.PassiveEntity.PassiveData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;

public class TurretEntity extends MobEntity implements Itemable {
	private static final TrackedData<Integer> LEVEL = DataTracker.registerData(AxolotlEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Byte> FROM_ITEM = DataTracker.registerData(TurretEntity.class, TrackedDataHandlerRegistry.BYTE);
	@Nullable
    private BlockPos prevAttachedBlock;
	
	protected static final TrackedData<Direction> ATTACHED_FACE = DataTracker.registerData(TurretEntity.class, TrackedDataHandlerRegistry.FACING);;
	protected Class<?> projectile;
	protected int level;
	protected final Random random;
	
	static final Vec3f SOUTH_VECTOR;
	
	// CONSTRUCTORS //
	public TurretEntity(EntityType<? extends MobEntity> entityType, World world, Class<?> projectile) {
		this(entityType, world);
		this.projectile = projectile;
		this.level = 0;
	}
	
	// METHODS //
	// PRIVATE
	private boolean isInvalidPosition(BlockPos pos) {
        BlockState blockState = this.world.getBlockState(pos);
        if (blockState.isAir()) {
            return false;
        }
        boolean bl = blockState.isOf(Blocks.MOVING_PISTON) && pos.equals(this.getBlockPos());
        return !bl;
    }
	
	private void tryAttachOrFall() {
        Direction direction = this.findAttachSide(this.getBlockPos());
        if (direction != null) {
            this.setAttachedFace(direction);
        } else {
            this.tryFalling();
        }
    }
	
	private void setAttachedFace(Direction face) {
        this.dataTracker.set(ATTACHED_FACE, face);
    }
	
	// PROTECTED
	protected void setLevel(int level) {
		this.dataTracker.set(LEVEL, level);
	}
	
	protected TurretEntity(EntityType<? extends MobEntity> entityType, World world) {
		super(entityType, world);
		this.random = Random.create();
	}
	
	protected boolean canStay(BlockPos pos, Direction direction) {
        if (this.isInvalidPosition(pos)) {
            return false;
        }
        
        Direction direction2 = direction.getOpposite();
        if (!this.world.isDirectionSolid(pos.offset(direction), this, direction2)) {
            return false;
        }
        
        Box box = ShulkerEntity.calculateBoundingBox(direction2, 1.0f).offset(pos).contract(1.0E-6);
        return this.world.isSpaceEmpty(this, box);
    }
	
	@Nullable
    protected Direction findAttachSide(BlockPos pos) {
        for (Direction direction : Direction.values()) {
            if (!this.canStay(pos, direction)) continue;
            return direction;
        }
        return null;
    }
	
	protected boolean tryFalling() {
		if (this.hasNoGravity())
			return false;
		return true;
		
//		BlockPos blockPos = this.getBlockPos();
//        for (int i = 0; i < 5; ++i) {
//            Direction direction;
//            BlockPos blockPos2 = blockPos.add(MathHelper.nextBetween(this.random, -8, 8), MathHelper.nextBetween(this.random, -8, 8), MathHelper.nextBetween(this.random, -8, 8));
//            
//            if (blockPos2.getY() <= this.world.getBottomY() ||
//            	!this.world.isAir(blockPos2) ||
//            	!this.world.getWorldBorder().contains(blockPos2) ||
//            	!this.world.isSpaceEmpty(this, new Box(blockPos2).contract(1.0E-6)) ||
//            	(direction = this.findAttachSide(blockPos2)) == null) {
//            		continue;
//            }
//            
//            this.detach();
//            this.setAttachedFace(direction);
//            this.playSound(SoundEvents.BLOCK_ANVIL_FALL, 1.0f, 1.0f);
//            this.setPosition((double)blockPos2.getX() + 0.5, blockPos2.getY(), (double)blockPos2.getZ() + 0.5);
//            this.world.emitGameEvent(GameEvent.HIT_GROUND, blockPos, GameEvent.Emitter.of(this));
//            this.setTarget(null);
//            return true;
//        }
//		return true;
	}
	
	@Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(ATTACHED_FACE, Direction.DOWN);
        this.dataTracker.startTracking(LEVEL, this.level);
        this.dataTracker.startTracking(FROM_ITEM, (byte) 1);
    }
	
	@Override
    protected BodyControl createBodyControl() {
        return new TurretBodyControl(this);
    }
	
	// PUBLIC
	public int getMaxLookPitchChange() {
		return 30;
	}

	public int getMaxHeadRotation() {
		return 360;
	}
	
	public Direction getAttachedFace() {
		return (Direction)this.dataTracker.get(ATTACHED_FACE);
	}
	
	public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
		return false;
	}
	
	public int getMinAmbientSoundDelay() {
		return 120;
	}

	public boolean canImmediatelyDespawn(double distanceSquared) {
		return false;
	}
	
	public int getLevel() {
		return this.dataTracker.get(LEVEL);
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putInt("Level", this.getLevel());
		nbt.putByte("FromItem", this.isFromItem());
		nbt.putByte("AttachFace", (byte)this.getAttachedFace().getId());
	}
	
	@Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setLevel(nbt.getInt("Level"));
        this.setFromItem(nbt.getByte("FromItem"));
        this.setAttachedFace(Direction.byId(nbt.getByte("AttachFace")));
    }
	
	@Override
    public boolean canBeLeashedBy(PlayerEntity player) {
        return false;
    }
	
	@Override
    public boolean startRiding(Entity entity, boolean force) {
        if (this.world.isClient()) {
            this.prevAttachedBlock = null;
        }

        this.setAttachedFace(Direction.DOWN);
        return super.startRiding(entity, force);
    }
	
	@Override
    public void stopRiding() {
        super.stopRiding();
        if (this.world.isClient) {
            this.prevAttachedBlock = this.getBlockPos();
        }
        this.prevBodyYaw = 0.0f;
        this.bodyYaw = 0.0f;
    }
	
	@Override
    @Nullable
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
		if (entityData == null)
            entityData = new PassiveData(false);
		
		this.setYaw(0.0f);
		this.headYaw = this.getYaw();
		this.resetPosition();
		
		if (entityNbt != null) {
			if (spawnReason == SpawnReason.SPAWN_EGG && entityNbt.contains("FromItem")) {
				this.setHealth(entityNbt.getFloat("Health"));
				return entityData;
			}
		}
		
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }
	
	@Override
    public void setPosition(double x, double y, double z) {
        BlockPos blockPos = this.getBlockPos();
        if (this.hasVehicle()) {
            super.setPosition(x, y, z);
        } else {
            super.setPosition((double)MathHelper.floor(x) + 0.5, MathHelper.floor(y + 0.5), (double)MathHelper.floor(z) + 0.5);
        }
        if (this.age == 0) {
            return;
        }
        BlockPos blockPos2 = this.getBlockPos();
        if (!blockPos2.equals(blockPos)) {
            this.velocityDirty = true;
            if (this.world.isClient && !this.hasVehicle() && !blockPos2.equals(this.prevAttachedBlock)) {
                this.prevAttachedBlock = blockPos;
                this.lastRenderX = this.getX();
                this.lastRenderY = this.getY();
                this.lastRenderZ = this.getZ();
            }
        }
    }
	
	@Override
    public void updateTrackedPositionAndAngles(double x, double y, double z, float yaw, float pitch, int interpolationSteps, boolean interpolate) {
        this.bodyTrackingIncrements = 0;
        this.setPosition(x, y, z);
        this.setRotation(yaw, pitch);
    }
	
	@Override
    public boolean isCollidable() {
        return this.isAlive();
    }
	
	@Override
    public SoundCategory getSoundCategory() {
        return SoundCategory.BLOCKS;
    }

	@Override
	public byte isFromItem() {
        return this.dataTracker.get(FROM_ITEM);
    }

	@Override
	public void setFromItem(byte fromItem) {
		this.dataTracker.set(FROM_ITEM, fromItem);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void copyDataToStack(ItemStack stack) {
		Itemable.copyDataToStack(this, stack);
		NbtCompound nbtCompound = stack.getOrCreateNbt();
		
		nbtCompound.putInt("Level", this.getLevel());
		nbtCompound.putByte("FromItem", (byte) 1);
		nbtCompound.putFloat("Health", this.getHealth());
		
		Brain<?> brain = this.getBrain();
		if (brain.hasMemoryModule(MemoryModuleType.ATTACK_COOLING_DOWN)) {
			nbtCompound.putLong("AttackCoolingDown", brain.getMemory(MemoryModuleType.ATTACK_COOLING_DOWN));
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void copyDataFromNbt(NbtCompound nbt) {
		Itemable.copyDataFromNbt(this, nbt);
		
		if (nbt.contains("Level"))
			this.setLevel(nbt.getInt("Level"));
        if (nbt.contains("FromItem"))
            this.setFromItem(nbt.getByte("FromItem"));
        if (nbt.contains("Health"))
        	this.setHealth(nbt.getFloat("Health"));
        if (nbt.contains("AttackCoolingDown"))
            this.getBrain().remember(MemoryModuleType.ATTACK_COOLING_DOWN, true, nbt.getLong("AttackCoolingDown"));
	}

	@Override
	public ItemStack getEntityItem() {
		return null;
	}

	@Override
	public SoundEvent getTurretRemoveSound() {
		return null;
	}
	
	@Override
	public void tick() {
		super.tick();
		if (!(this.world.isClient || this.hasVehicle() || this.canStay(this.getBlockPos(), this.getAttachedFace()))) {
			this.tryAttachOrFall();
        }
        if (this.world.isClient) {
        	this.prevAttachedBlock = null;
        }
	}
	
	static {
		SOUTH_VECTOR = (Vec3f)Util.make(() -> {
			Vec3i vec3i = Direction.SOUTH.getVector();
			return new Vec3f((float)vec3i.getX(), (float)vec3i.getY(), (float)vec3i.getZ());
		});
	}
	
	static class TurretBodyControl extends BodyControl {
        public TurretBodyControl(MobEntity mobEntity) {
            super(mobEntity);
        }

        @Override
        public void tick() {
        }
    }
}