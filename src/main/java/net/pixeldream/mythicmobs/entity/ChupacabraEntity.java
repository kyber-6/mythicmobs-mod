package net.pixeldream.mythicmobs.entity;

import mod.azure.azurelib.core.animation.AnimationController;
import mod.azure.azurelib.core.object.PlayState;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.pixeldream.mythicmobs.config.MythicMobsConfigs;
import net.pixeldream.mythicmobs.registry.ItemRegistry;
import net.pixeldream.mythicmobs.registry.ParticleRegistry;

import java.util.function.Predicate;

public class ChupacabraEntity extends HostileEntity implements IAnimatable, Monster {
    private AnimationFactory factory = GeckoLibUtil.createFactory(this);
    public static final AnimationBuilder IDLE = new AnimationBuilder().addAnimation("idle", ILoopType.EDefaultLoopTypes.LOOP);
    public static final AnimationBuilder WALK = new AnimationBuilder().addAnimation("walk", ILoopType.EDefaultLoopTypes.LOOP);
    public static final AnimationBuilder RUN = new AnimationBuilder().addAnimation("run", ILoopType.EDefaultLoopTypes.LOOP);
    public static final AnimationBuilder ATTACK = new AnimationBuilder().addAnimation("attack", ILoopType.EDefaultLoopTypes.PLAY_ONCE);
    private static final Predicate<LivingEntity> DIET;
    private long ticksUntilAttackFinish = 0;

    public ChupacabraEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.experiencePoints = NORMAL_MONSTER_XP;
    }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return HostileEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, MythicMobsConfigs.chupacabraHealth).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, MythicMobsConfigs.chupacabraAttackDamage).add(EntityAttributes.GENERIC_ATTACK_SPEED, 1.25f).add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 1).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new MeleeAttackGoal(this, 1.5f, true));
        this.goalSelector.add(2, new WanderAroundFarGoal(this, 0.75f));
        this.goalSelector.add(3, new LookAtEntityGoal(this, PlayerEntity.class, 6.0f));
        this.goalSelector.add(4, new LookAroundGoal(this));
        this.targetSelector.add(1, new RevengeGoal(this));
        this.targetSelector.add(4, new ActiveTargetGoal(this, AnimalEntity.class, false, DIET));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    static {
        DIET = (entity) -> {
            EntityType<?> entityType = entity.getType();
            return entityType == EntityType.SHEEP || entityType == EntityType.COW || entityType == EntityType.PIG || entityType == EntityType.CHICKEN || entityType == EntityType.RABBIT;
        };
    }

    @Override
    public void tick() {
        super.tick();
        if (handSwinging) {
            this.world.addParticle(ParticleRegistry.BLOOD_PARTICLE, getX(), getY(), getZ(), 0.0, 0.0, 0.0);
            this.world.addParticle(ParticleRegistry.BLOOD_PARTICLE, getX(), getY(), getZ(), 0.0, 0.0, 0.0);
            this.world.addParticle(ParticleRegistry.BLOOD_PARTICLE, getX(), getY(), getZ(), 0.0, 0.0, 0.0);
            if (getHealth() < getMaxHealth()) {
                this.heal(2.5f);
                produceParticles(ParticleTypes.HAPPY_VILLAGER);
            }
        }
    }

    @Override
    public void registerControllers(AnimationData animationData) {
        animationData.addAnimationController(new AnimationController(this, "controller", 2.5f, animationEvent -> {
            if (animationEvent.isMoving() && !handSwinging) {
                if (isAttacking() && !handSwinging) {
                    animationEvent.getController().setAnimation(RUN);
                    return PlayState.CONTINUE;
                }
                animationEvent.getController().setAnimation(WALK);
                return PlayState.CONTINUE;
            } else if (handSwinging) {
                animationEvent.getController().setAnimation(ATTACK);
                ticksUntilAttackFinish++;
                if (ticksUntilAttackFinish > 20 * 2) {
                    handSwinging = false;
                    ticksUntilAttackFinish = 0;
                }
                return PlayState.CONTINUE;
            }
            animationEvent.getController().setAnimation(IDLE);
            return PlayState.CONTINUE;
        }));
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
            this.dropStack(new ItemStack(ItemRegistry.CHUPACABRA_RAW_MEAT, random.nextInt(2)));
        }
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        this.playSound(SoundEvents.ENTITY_WOLF_AMBIENT, 1.0f, 0.25f);
        return null;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        this.playSound(SoundEvents.ENTITY_WOLF_HURT, 1.0f, 0.25f);
        return null;
    }

    @Override
    protected SoundEvent getDeathSound() {
        this.playSound(SoundEvents.ENTITY_WOLF_DEATH, 1.0f, 0.25f);
        return null;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.ENTITY_WOLF_STEP, 0.5f, 1.0f);
    }
}
