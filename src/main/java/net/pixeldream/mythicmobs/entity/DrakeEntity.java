package net.pixeldream.mythicmobs.entity;

import mod.azure.azurelib.core.animation.AnimationController;
import mod.azure.azurelib.core.object.PlayState;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.pixeldream.mythicmobs.config.MythicMobsConfigs;
import net.pixeldream.mythicmobs.registry.EntityRegistry;
import net.pixeldream.mythicmobs.registry.ItemRegistry;
import net.pixeldream.mythicmobs.registry.SoundRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class DrakeEntity extends TameableEntity implements IAnimatable, RangedAttackMob {
    private AnimationFactory factory = GeckoLibUtil.createFactory(this);
    public static final AnimationBuilder SIT = new AnimationBuilder().addAnimation("sit", ILoopType.EDefaultLoopTypes.LOOP);
    public static final AnimationBuilder IDLE = new AnimationBuilder().addAnimation("idle", ILoopType.EDefaultLoopTypes.LOOP);
    public static final AnimationBuilder WALK = new AnimationBuilder().addAnimation("walk", ILoopType.EDefaultLoopTypes.LOOP);
    public static final AnimationBuilder RUN = new AnimationBuilder().addAnimation("run", ILoopType.EDefaultLoopTypes.LOOP);
    public static final AnimationBuilder ATTACK = new AnimationBuilder().addAnimation("attack", ILoopType.EDefaultLoopTypes.PLAY_ONCE);
    public static final AnimationBuilder FIRE = new AnimationBuilder().addAnimation("fire", ILoopType.EDefaultLoopTypes.PLAY_ONCE);
    private boolean firing = false;
    protected static final TrackedData<Integer> DATA_ID_TYPE_VARIANT = DataTracker.registerData(DrakeEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> SITTING = DataTracker.registerData(DrakeEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> HAS_EGG;
    private static final Predicate<LivingEntity> UNTAMED_DIET;
    private long ticksUntilAttackFinish = 0;

    public DrakeEntity(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
        this.experiencePoints = 5;
    }

    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        DrakeVariant variant = Util.getRandom(DrakeVariant.values(), this.random);
        setVariant(variant);
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }

    @Override
    public boolean isFireImmune() {
        return true;
    }

    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
//        DrakeEntity baby = EntityRegistry.DRAKE_ENTITY.create(world);

//        world.setBlockState(this.getBlockPos(), BlockRegistry.DRAKE_EGG_BLOCK.getDefaultState());
//        this.getPos().add(1, 0, 0);
//        entity.getPos().add(-1, 0, 0);

//        DrakeVariant variant = Util.getRandom(DrakeVariant.values(), this.random);
//        baby.setVariant(variant);
        DrakeEntity baby = EntityRegistry.DRAKE_ENTITY.create(world);
        baby.setVariant(Util.getRandom(DrakeVariant.values(), this.random));
        return baby;
    }

//    @Override
//    public boolean isBreedingItem(@NotNull ItemStack stack) {
//        return stack.getItem() == Items.GOLDEN_APPLE;
//    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(SITTING, false);
        this.dataTracker.startTracking(DATA_ID_TYPE_VARIANT, 0);
        this.dataTracker.startTracking(HAS_EGG, false);
    }

    public DrakeVariant getVariant() {
        return DrakeVariant.byId(this.getTypeVariant() & 255);
    }

    private void setVariant(DrakeVariant variant) {
        this.dataTracker.set(DATA_ID_TYPE_VARIANT, variant.getId() & 255);
    }

    private int getTypeVariant() {
        return this.dataTracker.get(DATA_ID_TYPE_VARIANT);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putBoolean("IsSitting", this.dataTracker.get(SITTING));
        nbt.putInt("Variant", this.getTypeVariant());
        nbt.putBoolean("HasEgg", this.hasEgg());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.dataTracker.set(SITTING, nbt.getBoolean("IsSitting"));
        this.dataTracker.set(DATA_ID_TYPE_VARIANT, nbt.getInt("Variant"));
        this.setHasEgg(nbt.getBoolean("HasEgg"));
    }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return HostileEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, MythicMobsConfigs.drakeHealth).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, MythicMobsConfigs.drakeAttackDamage).add(EntityAttributes.GENERIC_ATTACK_SPEED, 2).add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 1).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new SitGoal(this));
        this.goalSelector.add(2, new MeleeAttackGoal(this, 1.25f, true));
//        this.goalSelector.add(2, new ProjectileAttackGoal(this, 0.0f, 40, 100, 20f));
        this.goalSelector.add(3, new FollowOwnerGoal(this, 1.2f, 10.0F, 2.0F, false));
//        this.goalSelector.add(4, new MateGoal(this, 1.0));
//        this.goalSelector.add(4, new LayEggGoal(this, 1.0));
        this.goalSelector.add(5, new WanderAroundGoal(this, 0.75f));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 6.0f));
        this.goalSelector.add(7, new LookAroundGoal(this));
        this.targetSelector.add(1, new TrackOwnerAttackerGoal(this));
        this.targetSelector.add(2, new AttackWithOwnerGoal(this));
        this.targetSelector.add(3, (new RevengeGoal(this, new Class[0])).setGroupRevenge(new Class[0]));
        this.targetSelector.add(4, new ActiveTargetGoal(this, ChupacabraEntity.class, false));
        this.targetSelector.add(5, new ActiveTargetGoal(this, ChickenEntity.class, false));
        this.targetSelector.add(6, new UntamedActiveTargetGoal(this, AnimalEntity.class, false, UNTAMED_DIET));
        this.targetSelector.add(7, new UniversalAngerGoal(this, true));
    }

    static {
        UNTAMED_DIET = (entity) -> {
            EntityType<?> entityType = entity.getType();
            return entityType == EntityType.SHEEP || entityType == EntityType.COW || entityType == EntityType.HORSE || entityType == EntityType.DONKEY || entityType == EntityType.PIG || entityType == EntityType.CHICKEN || entityType == EntityType.RABBIT;
        };
        HAS_EGG = DataTracker.registerData(DrakeEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public void registerControllers(AnimationData animationData) {
        animationData.addAnimationController(new AnimationController(this, "controller", 2.5f, animationEvent -> {
            if (isSitting()) {
                animationEvent.getController().setAnimation(SIT);
                return PlayState.CONTINUE;
            } else if (firing) {
                animationEvent.getController().setAnimation(FIRE);
                return PlayState.CONTINUE;
            } else if (animationEvent.isMoving() && !handSwinging) {
                if (isAttacking() && !handSwinging) {
                    animationEvent.getController().setAnimation(RUN);
                    return PlayState.CONTINUE;
                }
                animationEvent.getController().setAnimation(WALK);
                return PlayState.CONTINUE;
            } else if (handSwinging) {
                animationEvent.getController().setAnimation(ATTACK);
                ticksUntilAttackFinish++;
                if (ticksUntilAttackFinish > 25 * 2) {
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
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemstack = player.getStackInHand(hand);
        Item item = itemstack.getItem();
        Item tameItem = ItemRegistry.CHUPACABRA_COOKED_MEAT_SKEWER;
        boolean b1 = random.nextInt(3) == 2;
        if (isBreedingItem(itemstack)) {
            return super.interactMob(player, hand);
        }
        if (item == tameItem && !isTamed()) {
            if (this.world.isClient()) {
                if (!b1) {
                    produceParticles(ParticleTypes.SMOKE);
                }
                return ActionResult.CONSUME;
            } else {
                if (!player.getAbilities().creativeMode) {
                    itemstack.decrement(1);
                }
                if (!this.world.isClient() && b1) {
                    super.setOwner(player);
                    this.navigation.recalculatePath();
                    this.setTarget(null);
                    this.world.sendEntityStatus(this, (byte) 7);
                    setSit(true);
                }
                return ActionResult.SUCCESS;
            }
        }
        if (isTamed() && !this.world.isClient() && hand == Hand.MAIN_HAND) {
            setSit(!isSitting());
            return ActionResult.SUCCESS;
        }
        if (itemstack.getItem() == tameItem) {
            return ActionResult.PASS;
        }
        return super.interactMob(player, hand);
    }

    public void setSit(boolean sitting) {
        this.dataTracker.set(SITTING, sitting);
        super.setSitting(sitting);
    }

    public boolean isSitting() {
        return this.dataTracker.get(SITTING);
    }

    @Override
    public void setTamed(boolean tamed) {
        double health = EntityAttributes.GENERIC_MAX_HEALTH.getDefaultValue();
        double damage = EntityAttributes.GENERIC_ATTACK_DAMAGE.getDefaultValue();
        double speed = EntityAttributes.GENERIC_MOVEMENT_SPEED.getDefaultValue();
        super.setTamed(tamed);
        if (tamed) {
            getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(Math.ceil(health + health * 0.25));
            getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(Math.ceil(damage + damage * 0.25));
        } else {
            getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(health);
            getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(damage);
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
        if (deathTime == 15) {
            this.remove(Entity.RemovalReason.KILLED);
            this.dropXp();
        }
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        this.playSound(SoundRegistry.DRAKE_ROAR, 0.5f, 1.0f);
        return null;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        this.playSound(SoundRegistry.DRAKE_ROAR, 0.75f, 1.0f);
        return null;
    }

    @Override
    protected SoundEvent getDeathSound() {
        this.playSound(SoundRegistry.DRAKE_DEATH, 0.75f, 1.0f);
        return null;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.ENTITY_WOLF_STEP, 0.5f, 1.0f);
    }

    public boolean hasEgg() {
        return (Boolean) this.dataTracker.get(HAS_EGG);
    }

    void setHasEgg(boolean hasEgg) {
        this.dataTracker.set(HAS_EGG, hasEgg);
    }

    @Override
    public void attack(LivingEntity target, float pullProgress) {
        firing = true;
        this.shootFireAt(target);
    }

    private double getHeadX() {
        float f = (this.bodyYaw + (float) (180)) * 0.017453292F;
        float g = MathHelper.cos(f);
        return this.getX() + (double) g * 1.3;

    }

    private double getHeadY() {
        return this.getY() + 1.25;
    }

    private double getHeadZ() {
        float f = (this.bodyYaw + (float) (180)) * 0.017453292F;
        float g = MathHelper.sin(f);
        return this.getZ() + (double) g * 1.3;

    }

    private void shootFireAt(LivingEntity target) {
        this.shootFireAt(target.getX(), target.getY() + (double) target.getStandingEyeHeight() * 0.5, target.getZ(), this.random.nextFloat() < 0.001F);
    }

    private void shootFireAt(double targetX, double targetY, double targetZ, boolean charged) {
        double d = this.getHeadX();
        double e = this.getHeadY();
        double f = this.getHeadZ();
        double g = targetX - d;
        double h = targetY - e;
        double i = targetZ - f;
        SmallFireballEntity smallFireballEntity = new SmallFireballEntity(this.world, this, g, h, i);
        smallFireballEntity.setOwner(this);
//        if (charged) {
//            fireballEntity.setCharged(true);
//        }
        smallFireballEntity.setPos(d, e, f);
        world.playSound((PlayerEntity) null, this.getBlockPos(), SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.BLOCKS, 1.0f, 1.0f);
        this.world.spawnEntity(smallFireballEntity);
        for (int j = 0; j < 2000; j++) {
        }
        firing = false;
    }

//    private static class LayEggGoal extends MoveToTargetPosGoal {
//        private final DrakeEntity drake;
//
//        LayEggGoal(DrakeEntity drake, double speed) {
//            super(drake, speed, 16);
//            this.drake = drake;
//        }
//
//        public boolean canStart() {
//            return this.drake.hasEgg() ? super.canStart() : false;
//        }
//
//        public boolean shouldContinue() {
//            return super.shouldContinue() && this.drake.hasEgg();
//        }
//
//        public void tick() {
//            super.tick();
//            BlockPos blockPos = this.drake.getBlockPos();
//            if (!this.drake.isTouchingWater() && this.hasReached()) {
//                World world = this.drake.world;
//                world.playSound((PlayerEntity) null, blockPos, SoundEvents.ENTITY_TURTLE_LAY_EGG, SoundCategory.BLOCKS, 1.0f, 1.25f);
//                world.setBlockState(this.targetPos.up(), (BlockState) BlockRegistry.DRAKE_EGG_BLOCK.getDefaultState());
//                this.drake.setHasEgg(false);
//                this.drake.setLoveTicks(600);
//            }
//
//        }
//
//        protected boolean isTargetPos(WorldView world, BlockPos pos) {
//            return !world.isAir(pos.up());
//        }
//    }

//    private static class MateGoal extends AnimalMateGoal {
//        private final DrakeEntity drake;
//
//        MateGoal(DrakeEntity drake, double speed) {
//            super(drake, speed);
//            this.drake = drake;
//        }
//
//        public boolean canStart() {
//            return super.canStart() && !this.drake.hasEgg();
//        }
//
//        protected void breed() {
//            ServerPlayerEntity serverPlayerEntity = this.animal.getLovingPlayer();
//            if (serverPlayerEntity == null && this.mate.getLovingPlayer() != null) {
//                serverPlayerEntity = this.mate.getLovingPlayer();
//            }
//
//            if (serverPlayerEntity != null) {
//                serverPlayerEntity.incrementStat(Stats.ANIMALS_BRED);
//                Criteria.BRED_ANIMALS.trigger(serverPlayerEntity, this.animal, this.mate, (PassiveEntity) null);
//            }
//
//            this.drake.setHasEgg(true);
//            this.animal.resetLoveTicks();
//            this.mate.resetLoveTicks();
//            Random random = this.animal.getRandom();
//            if (this.world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT)) {
//                this.world.spawnEntity(new ExperienceOrbEntity(this.world, this.animal.getX(), this.animal.getY(), this.animal.getZ(), random.nextInt(7) + 1));
//            }
//
//        }
//    }
}
