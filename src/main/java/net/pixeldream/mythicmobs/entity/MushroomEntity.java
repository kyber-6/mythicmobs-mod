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
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerTask;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.pixeldream.mythicmobs.config.MythicMobsConfigs;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class MushroomEntity extends PathAwareEntity implements IAnimatable {
    private AnimationFactory factory = GeckoLibUtil.createFactory(this);
    public static final AnimationBuilder IDLE = new AnimationBuilder().addAnimation("idle", ILoopType.EDefaultLoopTypes.LOOP);
    public static final AnimationBuilder WALK = new AnimationBuilder().addAnimation("walk", ILoopType.EDefaultLoopTypes.LOOP);
    public static final AnimationBuilder BOUNCE = new AnimationBuilder().addAnimation("bounce", ILoopType.EDefaultLoopTypes.PLAY_ONCE);
    protected static final TrackedData<Integer> DATA_ID_TYPE_VARIANT = DataTracker.registerData(MushroomEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private Text currentLine;
    private List<String> lines;
    private List<String> greetings;
    private int lineCooldown = 60;
    private boolean touched = false;

    private boolean startCountdown = false;
    private boolean talk = true;
    private int pitch;
    private SoundEvent interactSound;

    public MushroomEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
        this.experiencePoints = 1;
    }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return HostileEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, MythicMobsConfigs.mushroomHealth).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3);
    }

    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        MushroomVariant variant = Util.getRandom(MushroomVariant.values(), this.random);
        setVariant(variant);
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(DATA_ID_TYPE_VARIANT, 0);
    }

    public MushroomVariant getVariant() {
        return MushroomVariant.byId(this.getTypeVariant() & 255);
    }

    private void setVariant(MushroomVariant variant) {
        this.dataTracker.set(DATA_ID_TYPE_VARIANT, variant.getId() & 255);
    }

    private int getTypeVariant() {
        return this.dataTracker.get(DATA_ID_TYPE_VARIANT);
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

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new EscapeDangerGoal(this, 0.80f));
        this.goalSelector.add(2, new WanderAroundGoal(this, 0.75f));
        this.goalSelector.add(3, new LookAtEntityGoal(this, PlayerEntity.class, 6.0f));
        this.goalSelector.add(4, new LookAroundGoal(this));
    }

    @Override
    public void tick() {
        super.tick();
        if (startCountdown) {
            lineCooldown--;
            if (lineCooldown <= 0) {
                startCountdown = false;
                talk = true;
                lineCooldown = 60;
            }
        }
    }

    @Override
    public void registerControllers(AnimationData animationData) {
        animationData.addAnimationController(new AnimationController(this, "controller", 2.5f, animationEvent -> {
            if (animationEvent.isMoving()) {
                animationEvent.getController().setAnimation(WALK);
                return PlayState.CONTINUE;
            }
            animationEvent.getController().setAnimation(IDLE);
            return PlayState.CONTINUE;
        }));
        animationData.addAnimationController(new AnimationController(this, "bounceController", 0, animationEvent -> {
            if (touched) {
                animationEvent.getController().markNeedsReload();
                animationEvent.getController().setAnimation(BOUNCE);
                touched = false;
            }
            return PlayState.CONTINUE;
        }));
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        if (getVariant().equals(MushroomVariant.RED)) {
            pitch = 15;
            interactSound = SoundEvents.ENTITY_VILLAGER_YES;
            lines = MythicMobsConfigs.redMushroomLines;
            greetings = Arrays.asList(
                    "Hello there, ",
                    "Hey there, ",
                    "Howdy, ",
                    "Howdy-do, ",
                    "Salutations, ",
                    "Hiya, ",
                    "Godspeed, "
            );
        } else {
            pitch = 10;
            interactSound = SoundEvents.ENTITY_VILLAGER_NO;
            lines = MythicMobsConfigs.brownMushroomLines;
        }
        currentLine = Text.literal(lines.get(random.nextInt(lines.size())));
        if (talk) {
            touched = true;
            talk = false;
            startCountdown = true;
            Text previousLine = currentLine;
            do {
                currentLine = Text.literal(lines.get(random.nextInt(lines.size())));
                if (currentLine.equals(Text.literal("playerGreeting"))) {
                    currentLine = Text.literal(greetings.get(random.nextInt(greetings.size())) + player.getEntityName() + '!');
                }
            } while (currentLine.equals(previousLine));
            MinecraftServer server = player.getServer();
            if (server != null) {
                this.playSound(interactSound, 1.0f, pitch);
                server.send(new ServerTask(0, () -> player.sendMessage(currentLine, true)));
            }
        }
        return super.interactMob(player, hand);
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
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
    public void updatePostDeath() {
        ++deathTime;
        if (deathTime == 15) {
            if (this.getVariant().equals(MushroomVariant.RED)) {
                this.dropStack(new ItemStack(Items.RED_MUSHROOM));
            } else {
                this.dropStack(new ItemStack(Items.BROWN_MUSHROOM));
            }
            this.remove(Entity.RemovalReason.KILLED);
            this.dropXp();
        }
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        produceParticles(ParticleTypes.POOF);
        super.onDeath(damageSource);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        this.playSound(SoundEvents.ENTITY_VILLAGER_AMBIENT, 1.0f, pitch);
        return null;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        this.playSound(SoundEvents.ENTITY_VILLAGER_HURT, 1.0f, pitch);
        return null;
    }

    @Override
    protected SoundEvent getDeathSound() {
        this.playSound(SoundEvents.ENTITY_VILLAGER_DEATH, 1.0f, pitch);
        return null;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.ENTITY_WOLF_STEP, 0.25f, pitch);
    }
}
