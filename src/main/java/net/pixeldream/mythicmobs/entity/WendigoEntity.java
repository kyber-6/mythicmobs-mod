package net.pixeldream.mythicmobs.entity;

import mod.azure.azurelib.core.animation.AnimationController;
import mod.azure.azurelib.core.object.PlayState;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.pixeldream.mythicmobs.config.MythicMobsConfigs;
import net.pixeldream.mythicmobs.goal.DelayedAttackGoal;
import net.pixeldream.mythicmobs.goal.RoarGoal;
import org.jetbrains.annotations.Nullable;

public class WendigoEntity extends BossEntity implements IAnimatable, Monster {
    private AnimationFactory factory = GeckoLibUtil.createFactory(this);
    private AnimationBuilder IDLE;
    private AnimationBuilder WALK;
    private final AnimationBuilder RUN = new AnimationBuilder().addAnimation("run", ILoopType.EDefaultLoopTypes.LOOP);
    private final AnimationBuilder ATTACK = new AnimationBuilder().addAnimation("attack", ILoopType.EDefaultLoopTypes.PLAY_ONCE);
    private final AnimationBuilder ROAR = new AnimationBuilder().addAnimation("thing", ILoopType.EDefaultLoopTypes.PLAY_ONCE);
    private long ticksUntilAttackFinish = 0;
    private int roarTimer = 0;
    private long age = 0;
    private static final TrackedData<Integer> DATA_ID_TYPE_VARIANT = DataTracker.registerData(WendigoEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> HAS_ROARED = DataTracker.registerData(WendigoEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> ROARING = DataTracker.registerData(WendigoEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> HAS_ATTACKED = DataTracker.registerData(WendigoEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> WAS_STANDING = DataTracker.registerData(WendigoEntity.class, TrackedDataHandlerRegistry.BOOLEAN);


    public WendigoEntity(EntityType<? extends WendigoEntity> entityType, World world) {
        super(entityType, world, BossBar.Color.PURPLE);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(DATA_ID_TYPE_VARIANT, 0);
        this.dataTracker.startTracking(HAS_ROARED, Boolean.FALSE);
        this.dataTracker.startTracking(ROARING, Boolean.TRUE);
        this.dataTracker.startTracking(HAS_ATTACKED, Boolean.FALSE);
        this.dataTracker.startTracking(WAS_STANDING, Boolean.FALSE);
    }

    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        WendigoVariant variant = Util.getRandom(WendigoVariant.values(), this.random);
        setVariant(variant);
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("Variant", this.getTypeVariant());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.dataTracker.set(DATA_ID_TYPE_VARIANT, nbt.getInt("Variant"));
    }

    public WendigoVariant getVariant() {
        return WendigoVariant.byId(this.getTypeVariant() & 255);
    }

    private int getTypeVariant() {
        return this.dataTracker.get(DATA_ID_TYPE_VARIANT);
    }

    private void setVariant(WendigoVariant variant) {
        this.dataTracker.set(DATA_ID_TYPE_VARIANT, variant.getId() & 255);
    }

    private void setAnim() {
        if (getVariant().equals(WendigoVariant.WENDIGO)) {
            IDLE = new AnimationBuilder().addAnimation("idle", ILoopType.EDefaultLoopTypes.LOOP);
            WALK = new AnimationBuilder().addAnimation("walk", ILoopType.EDefaultLoopTypes.LOOP);
        } else {
            this.dataTracker.set(WAS_STANDING, Boolean.TRUE);
            IDLE = new AnimationBuilder().addAnimation("idle_stand_up", ILoopType.EDefaultLoopTypes.LOOP);
            WALK = new AnimationBuilder().addAnimation("two_legs_walk", ILoopType.EDefaultLoopTypes.LOOP);
        }
    }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return HostileEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, MythicMobsConfigs.wendigoHealth).add(EntityAttributes.GENERIC_ARMOR, MythicMobsConfigs.wendigoArmor).add(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, 10).add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 5).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, MythicMobsConfigs.wendigoAttackDamage).add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 1.5f).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3);
    }

    @Override
    protected void initGoals() {
        goalSelector.add(0, new SwimGoal(this));
        goalSelector.add(1, new RoarGoal(this));
        goalSelector.add(2, new DelayedAttackGoal(this, 1.5f, true));
        goalSelector.add(3, new WanderAroundFarGoal(this, 0.75f));
        goalSelector.add(4, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        goalSelector.add(5, new LookAroundGoal(this));
        targetSelector.add(2, new ActiveTargetGoal<>(this, CowEntity.class, true));
        targetSelector.add(3, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    public boolean getRoaring() {
        return this.dataTracker.get(ROARING);
    }

    public void setRoaring(boolean roaring) {
        this.dataTracker.set(ROARING, roaring);
    }

    @Override
    public void registerControllers(AnimationData animationData) {
        setAnim();
        animationData.addAnimationController(new AnimationController(this, "controller", 2.5f, animationEvent -> {
//            if (getRoaring()) {
//                animationEvent.getController().setAnimation(ROAR);
//                MythicMobs.LOGGER.info("ROARING CURRENTLY");
//                return PlayState.CONTINUE;
//            } else
                if (animationEvent.isMoving() && !handSwinging) {
                if (isAttacking() && !handSwinging) {
                    animationEvent.getController().setAnimation(RUN);
                    return PlayState.CONTINUE;
                }
                animationEvent.getController().setAnimation(WALK);
                return PlayState.CONTINUE;
            } else if (isAttacking()) {
                animationEvent.getController().setAnimation(ATTACK);
                ticksUntilAttackFinish++;
                if (ticksUntilAttackFinish > 17 * 6) {
                    handSwinging = false;
                    ticksUntilAttackFinish = 0;
                }
                return PlayState.CONTINUE;
            }
            animationEvent.getController().setAnimation(IDLE);
            return PlayState.CONTINUE;
        }));
    }

    @Override
    public void tick() {
        super.tick();
        age++;
        if (!world.isClient) {
            if (age == 1) {
                setAnim();
            }

            if (!getRoaring()) {
                roarTimer++;
                if (roarTimer > 6000) {
                    setRoaring(true);
                    roarTimer = 0;
                }
            }

            if (this.getVariant().equals(WendigoVariant.WENDIGO_STANDING) && this.isAttacking()) {
                this.setVariant(WendigoVariant.WENDIGO);
            }

            if (this.dataTracker.get(WAS_STANDING).equals(Boolean.TRUE) && !this.isAttacking() && getVariant().equals(WendigoVariant.WENDIGO)) {
                this.setVariant(WendigoVariant.WENDIGO_STANDING);
            }
        }
    }

    protected void produceParticles(ParticleEffect parameters) {
        for (int i = 0; i < 5; ++i) {
            double d = this.random.nextGaussian() * 0.02;
            double e = this.random.nextGaussian() * 0.02;
            double f = this.random.nextGaussian() * 0.02;
            this.world.addParticle(parameters, this.getParticleX(1.0), this.getRandomBodyY() + 1.0, this.getParticleZ(1.0), d, e, f);
        }
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        produceParticles(ParticleTypes.POOF);
        super.onDeath(damageSource);
    }

    @Override
    public void updatePostDeath() {
        ++deathTime;
        if (deathTime == 30) {
            produceParticles(ParticleTypes.POOF);
            this.remove(Entity.RemovalReason.KILLED);
            this.dropXp();
        }
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        playSound(SoundEvents.ENTITY_HUSK_AMBIENT, 1.0f, 3.0f);
        return null;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        playSound(SoundEvents.ENTITY_HUSK_HURT, 1.0f, 3.0f);
        return null;
    }

    @Override
    protected SoundEvent getDeathSound() {
        playSound(SoundEvents.ENTITY_HUSK_DEATH, 1.0f, 3.0f);
        return null;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        playSound(SoundEvents.ENTITY_HUSK_STEP, 0.5f, 1.0f);
    }

//    public class WendigoAttackGoal extends Goal {
//        protected final WendigoEntity mob;
//        private final double speed;
//        private final boolean pauseWhenMobIdle;
//        private Path path;
//        private double targetX;
//        private double targetY;
//        private double targetZ;
//        private int updateCountdownTicks;
//        private int cooldown;
//        private final int attackIntervalTicks = 20;
//        private long lastUpdateTime;
//        private static final long MAX_ATTACK_TIME = 20L;
//
//        public WendigoAttackGoal(WendigoEntity mob, double speed, boolean pauseWhenMobIdle) {
//            this.mob = mob;
//            this.speed = speed;
//            this.pauseWhenMobIdle = pauseWhenMobIdle;
//            this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
//        }
//
//        @Override
//        public boolean canStart() {
//            long l = this.mob.world.getTime();
//            if (l - this.lastUpdateTime < 20L) {
//                return false;
//            }
//            this.lastUpdateTime = l;
//            LivingEntity livingEntity = this.mob.getTarget();
//            if (livingEntity == null) {
//                return false;
//            }
//            if (!livingEntity.isAlive()) {
//                return false;
//            }
//            this.path = this.mob.getNavigation().findPathTo(livingEntity, 0);
//            if (this.path != null) {
//                return true;
//            }
//            return this.getSquaredMaxAttackDistance(livingEntity) >= this.mob.squaredDistanceTo(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ());
//        }
//
//        @Override
//        public boolean shouldContinue() {
//            LivingEntity livingEntity = this.mob.getTarget();
//            if (livingEntity == null) {
//                return false;
//            }
//            if (!livingEntity.isAlive()) {
//                return false;
//            }
//            if (!this.pauseWhenMobIdle) {
//                return !this.mob.getNavigation().isIdle();
//            }
//            if (!this.mob.isInWalkTargetRange(livingEntity.getBlockPos())) {
//                return false;
//            }
//            return !(livingEntity instanceof PlayerEntity) || !livingEntity.isSpectator() && !((PlayerEntity)livingEntity).isCreative();
//        }
//
//        @Override
//        public void start() {
//            this.mob.getNavigation().startMovingAlong(this.path, this.speed);
//            this.mob.setAttacking(true);
//            this.updateCountdownTicks = 0;
//            this.cooldown = 40;
//        }
//
//        @Override
//        public void stop() {
//            LivingEntity livingEntity = this.mob.getTarget();
//            if (!EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR.test(livingEntity)) {
//                this.mob.setTarget(null);
//            }
//            this.mob.setAttacking(false);
//            this.mob.getNavigation().stop();
//        }
//
//        @Override
//        public boolean shouldRunEveryTick() {
//            return true;
//        }
//
//        @Override
//        public void tick() {
//            LivingEntity livingEntity = this.mob.getTarget();
//            if (livingEntity == null) {
//                return;
//            }
//            this.mob.getLookControl().lookAt(livingEntity, 30.0f, 30.0f);
//            double d = this.mob.squaredDistanceTo(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ());
//            this.updateCountdownTicks = Math.max(this.updateCountdownTicks - 1, 0);
//            if ((this.pauseWhenMobIdle || this.mob.getVisibilityCache().canSee(livingEntity)) && this.updateCountdownTicks <= 0 && (this.targetX == 0.0 && this.targetY == 0.0 && this.targetZ == 0.0 || livingEntity.squaredDistanceTo(this.targetX, this.targetY, this.targetZ) >= 1.0 || this.mob.getRandom().nextFloat() < 0.05f)) {
//                this.targetX = livingEntity.getX();
//                this.targetY = livingEntity.getY();
//                this.targetZ = livingEntity.getZ();
//                this.updateCountdownTicks = 4 + this.mob.getRandom().nextInt(7);
//                if (d > 1024.0) {
//                    this.updateCountdownTicks += 10;
//                } else if (d > 256.0) {
//                    this.updateCountdownTicks += 5;
//                }
//                if (!this.mob.getNavigation().startMovingTo(livingEntity, this.speed)) {
//                    this.updateCountdownTicks += 15;
//                }
//                this.updateCountdownTicks = this.getTickCount(this.updateCountdownTicks);
//            }
//            this.cooldown--;
//            this.attack(livingEntity, d);
//        }
//
//        protected void attack(LivingEntity target, double squaredDistance) {
//            double d = this.getSquaredMaxAttackDistance(target);
//            if (squaredDistance <= d && this.cooldown <= 0) {
//                this.resetCooldown();
//                this.mob.swingHand(Hand.MAIN_HAND);
//                this.mob.tryAttack(target);
//            }
//        }
//
//        protected void resetCooldown() {
//            this.cooldown = 40;
//        }
//
//        protected boolean isCooledDown() {
//            return this.cooldown <= 0;
//        }
//
//        protected int getCooldown() {
//            return this.cooldown;
//        }
//
//        protected int getMaxCooldown() {
//            return this.getTickCount(20);
//        }
//
//        protected double getSquaredMaxAttackDistance(LivingEntity entity) {
//            return this.mob.getWidth() * 2.0f * (this.mob.getWidth() * 2.0f) + entity.getWidth();
//        }
//    }

}
