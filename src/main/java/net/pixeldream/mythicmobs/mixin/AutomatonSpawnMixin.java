package net.pixeldream.mythicmobs.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.CarvedPumpkinBlock;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.BlockPatternBuilder;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.Entity;
import net.minecraft.predicate.block.BlockStatePredicate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.pixeldream.mythicmobs.entity.AutomatonEntity;
import net.pixeldream.mythicmobs.registry.BlockRegistry;
import net.pixeldream.mythicmobs.registry.EntityRegistry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;

@Mixin(CarvedPumpkinBlock.class)
public class AutomatonSpawnMixin {
    @Shadow
    @Final
    private static Predicate<BlockState> IS_GOLEM_HEAD_PREDICATE;
    @Shadow
    private static void spawnEntity(World world, BlockPattern.Result patternResult, Entity entity, BlockPos pos) {}
    private BlockPattern automatonPattern;

    private BlockPattern getAutomatonPattern() {
        if (this.automatonPattern == null) {
            this.automatonPattern = BlockPatternBuilder.start().aisle("~^~", "###", "~#~")
                    .where('^', CachedBlockPosition.matchesBlockState(IS_GOLEM_HEAD_PREDICATE))
                    .where('#', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(BlockRegistry.BRONZE_BLOCK)))
                    .where('~', pos -> pos.getBlockState().isAir()).build();
        }
        return this.automatonPattern;
    }

    @Inject(at = @At("TAIL"), method = "Lnet/minecraft/block/CarvedPumpkinBlock;trySpawnEntity(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V")
    private void checkAutomatonSpawn(World world, BlockPos pos, CallbackInfo ci) {
        AutomatonEntity automatonEntity;
        BlockPattern.Result result2 = this.getAutomatonPattern().searchAround(world, pos);
        if (result2 != null && (automatonEntity = EntityRegistry.AUTOMATON_ENTITY.create(world)) != null) {
            spawnEntity(world, result2, automatonEntity, result2.translate(1, 2, 0).getBlockPos());
        }
    }
}