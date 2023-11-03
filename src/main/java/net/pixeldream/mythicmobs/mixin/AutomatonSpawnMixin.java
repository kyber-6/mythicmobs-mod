package net.pixeldream.mythicmobs.mixin;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CarvedPumpkinBlock;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.BlockPatternBuilder;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.predicate.block.BlockStatePredicate;
import net.minecraft.server.network.ServerPlayerEntity;
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

import java.util.Iterator;
import java.util.function.Predicate;

@Mixin(CarvedPumpkinBlock.class)
public class AutomatonSpawnMixin {
    @Shadow
    @Final
    private static Predicate<BlockState> IS_GOLEM_HEAD_PREDICATE;
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
        ServerPlayerEntity serverPlayerEntity2;
        if (result2 != null) {
            for(int i = 0; i < this.getAutomatonPattern().getWidth(); ++i) {
                for(int k = 0; k < this.getAutomatonPattern().getHeight(); ++k) {
                    CachedBlockPosition cachedBlockPosition3 = result2.translate(i, k, 0);
                    world.setBlockState(cachedBlockPosition3.getBlockPos(), Blocks.AIR.getDefaultState(), 2);
                    world.syncWorldEvent(2001, cachedBlockPosition3.getBlockPos(), Block.getRawIdFromState(cachedBlockPosition3.getBlockState()));
                }
            }

            BlockPos blockPos2 = result2.translate(1, 2, 0).getBlockPos();
            automatonEntity = (AutomatonEntity) EntityRegistry.AUTOMATON_ENTITY.create(world);
            automatonEntity.refreshPositionAndAngles((double)blockPos2.getX() + 0.5, (double)blockPos2.getY() + 0.05, (double)blockPos2.getZ() + 0.5, 0.0F, 0.0F);
            world.spawnEntity(automatonEntity);
            Iterator var7 = world.getNonSpectatingEntities(ServerPlayerEntity.class, automatonEntity.getBoundingBox().expand(5.0)).iterator();

            while(var7.hasNext()) {
                serverPlayerEntity2 = (ServerPlayerEntity)var7.next();
                Criteria.SUMMONED_ENTITY.trigger(serverPlayerEntity2, automatonEntity);
            }

            for(int j = 0; j < this.getAutomatonPattern().getWidth(); ++j) {
                for(int l = 0; l < this.getAutomatonPattern().getHeight(); ++l) {
                    CachedBlockPosition cachedBlockPosition4 = result2.translate(j, l, 0);
                    world.updateNeighbors(cachedBlockPosition4.getBlockPos(), Blocks.AIR);
                }
            }
        }
    }
}