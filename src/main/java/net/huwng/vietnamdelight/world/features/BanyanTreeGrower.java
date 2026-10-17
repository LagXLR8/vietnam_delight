package net.huwng.vietnamdelight.world.features;

import net.minecraft.util.RandomSource;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.Blocks;
import net.huwng.vietnamdelight.init.VietnamDelightModBlocks;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class BanyanTreeGrower extends AbstractTreeGrower {
    @Nullable
    @Override
    protected ResourceKey<ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource random, boolean bees) {
        return TreeFeatures.CHERRY;
    }
    
    @Override
    public boolean growTree(ServerLevel level, ChunkGenerator chunkGenerator, BlockPos pos, BlockState state, RandomSource random) {
        return generateCustomBanyanTree(level, pos, random);
    }
    
    private boolean generateCustomBanyanTree(ServerLevel level, BlockPos pos, RandomSource random) {
        if (!canPlaceTree(level, pos)) {
            return false;
        }
        int trunkHeight = 7 + random.nextInt(5);
        int branchStartHeight = 3 + random.nextInt(2);
        generateTrunk(level, pos, trunkHeight, random);
        generateBranches(level, pos.above(branchStartHeight), trunkHeight - branchStartHeight, random);
        return true;
    }
    
    private boolean canPlaceTree(ServerLevel level, BlockPos pos) {
        BlockState belowState = level.getBlockState(pos.below());
        if (!belowState.is(Blocks.GRASS_BLOCK) && !belowState.is(Blocks.DIRT)) {
            return false;
        }
       
        return true;
    }
    
    private void generateTrunk(ServerLevel level, BlockPos pos, int height, RandomSource random) {
        BlockState log = Blocks.MANGROVE_LOG.defaultBlockState();
        for (int y = 0; y < height; y++) {
            BlockPos trunkPos = pos.above(y);
            level.setBlock(trunkPos, log, 3);
            if (y == 0) {
                for (Direction dir : Direction.Plane.HORIZONTAL) {
                    BlockPos woodPos = trunkPos.relative(dir);
                    if (level.getBlockState(woodPos).isAir()) {
                        level.setBlock(woodPos, Blocks.MANGROVE_WOOD.defaultBlockState(), 3);
                        BlockPos belowWoodPos = woodPos.below();
                        if (level.getBlockState(belowWoodPos).isAir()) {
                            level.setBlock(belowWoodPos, Blocks.MANGROVE_WOOD.defaultBlockState(), 3);
                        }
                    }
                }
            }
            if (y < 4 && random.nextFloat() < 0.5) {
                Direction dir = Direction.Plane.HORIZONTAL.getRandomDirection(random);
                BlockPos rootPos = trunkPos.relative(dir);
                if (level.getBlockState(rootPos).isAir()) {
                    placeRootColumn(level, rootPos, random, 1 + random.nextInt(6));
                }
            }
        }
    }
    
    private void generateBranches(ServerLevel level, BlockPos startPos, int branchHeight, RandomSource random) {
        List<BlockPos> branchPoints = new ArrayList<>();
        branchPoints.add(startPos.above(branchHeight - 1));
        int numBranches = 2 + random.nextInt(3);
        for (int i = 0; i < numBranches; i++) {
            Direction dir = Direction.Plane.HORIZONTAL.getRandomDirection(random);
            int branchLength = 5 + random.nextInt(4);
            int branchY = random.nextInt(branchHeight);
            BlockPos branchStart = startPos.above(branchY);
            BlockPos currentPos = branchStart;
            for (int j = 0; j < branchLength; j++) {
                currentPos = currentPos.relative(dir);
                if (j > 1 && random.nextFloat() < 0.4) {
                    currentPos = currentPos.above();
                }
                level.setBlock(currentPos, Blocks.MANGROVE_WOOD.defaultBlockState(), 3);
                if (j > 2 && random.nextFloat() < 0.3) {
                    BlockPos rootPos = currentPos.below();
                    if (level.getBlockState(rootPos).isAir()) {
                        placeRootColumn(level, rootPos, random, 1 + random.nextInt(6));
                    }
                }
                if (j >= branchLength - 2) {
                    branchPoints.add(currentPos);
                }
            }
        }
        for (BlockPos branchPoint : branchPoints) {
            generateLeavesCluster(level, branchPoint, random);
        }
    }
    
    private void generateLeavesCluster(ServerLevel level, BlockPos center, RandomSource random) {
        int radius = 3 + random.nextInt(2);
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    double distance = Math.sqrt(x * x + y * y + z * z);
                    if (distance <= radius + random.nextFloat() * 0.5) {
                        BlockPos leafPos = center.offset(x, y, z);
                        if (level.getBlockState(leafPos).isAir()) {
                            BlockState leafBlock = getRandomBanyanLeaves(random);
                            if (leafBlock.hasProperty(BlockStateProperties.PERSISTENT)) {
                                leafBlock = leafBlock.setValue(BlockStateProperties.PERSISTENT, false);
                            }
                            if (leafBlock.hasProperty(BlockStateProperties.DISTANCE)) {
                                leafBlock = leafBlock.setValue(BlockStateProperties.DISTANCE, 4);
                            }
                            level.setBlock(leafPos, leafBlock, 3);
                            if (y == -radius || (y <= 0 && random.nextFloat() < 0.4)) {
                                generateHangingFeatures(level, leafPos.below(), random);
                            }
                        }
                    }
                }
            }
        }
        
        for (int i = 0; i < 4 + random.nextInt(4); i++) {
            Direction dir = Direction.Plane.HORIZONTAL.getRandomDirection(random);
            BlockPos droopPos = center.relative(dir, radius).below();
            
            if (level.getBlockState(droopPos).isAir()) {
                BlockState leafBlock = getRandomBanyanLeaves(random);
                if (leafBlock.hasProperty(BlockStateProperties.PERSISTENT)) {
                    leafBlock = leafBlock.setValue(BlockStateProperties.PERSISTENT, false);
                }
                if (leafBlock.hasProperty(BlockStateProperties.DISTANCE)) {
                    leafBlock = leafBlock.setValue(BlockStateProperties.DISTANCE, 4);
                }
                level.setBlock(droopPos, leafBlock, 3);
                generateHangingFeatures(level, droopPos.below(), random);
            }
        }
    }
    
    private void generateHangingFeatures(ServerLevel level, BlockPos startPos, RandomSource random) {
        if (!level.getBlockState(startPos).isAir()) {
            return;
        }
        
        // 30% chance for vine, 70% for roots (more roots)
        if (random.nextFloat() < 0.3) {
            // Place single banyan vine
            level.setBlock(startPos, getBanyanVineState(), 3);
        } else {
            // Place roots with random length 1-6 blocks
            int rootLength = 1 + random.nextInt(6);
            placeRootColumn(level, startPos, random, rootLength);
        }
    }
    
    private void placeRootColumn(ServerLevel level, BlockPos startPos, RandomSource random, int maxLength) {
        BlockPos currentPos = startPos;
        int actualLength = 0;
        
        // First, check how many blocks we can actually place
        for (int i = 0; i < maxLength; i++) {
            if (!level.getBlockState(currentPos.below(i)).isAir()) {
                actualLength = i;
                break;
            }
            actualLength = i + 1;
        }
        
        if (actualLength == 0) return;
        
        // Place the root column
        for (int i = 0; i < actualLength; i++) {
            BlockPos placePos = startPos.below(i);
            
            if (i == actualLength - 1) {
                // Last block is always banyan_roots (at the bottom)
                level.setBlock(placePos, getBanyanRootsState(), 3);
            } else {
                // All other blocks (including first block) are banyan_roots_plant
                level.setBlock(placePos, getBanyanRootsPlantState(), 3);
            }
        }
    }
    
    private BlockState getRandomBanyanLeaves(RandomSource random) {
        try {
            // 30% chance cho flowering leaves thay vì 50%
            if (random.nextFloat() < 0.3f && VietnamDelightModBlocks.FLOWERING_BANYAN_LEAVES.get() != null) {
                return VietnamDelightModBlocks.FLOWERING_BANYAN_LEAVES.get().defaultBlockState();
            } else if (VietnamDelightModBlocks.BANYAN_LEAVES.get() != null) {
                return VietnamDelightModBlocks.BANYAN_LEAVES.get().defaultBlockState();
            }
        } catch (Exception e) {
            // Silent fallback
        }
        return Blocks.OAK_LEAVES.defaultBlockState();
    }
    
    private BlockState getBanyanVineState() {
        try {
            if (VietnamDelightModBlocks.BANYAN_VINE.get() != null) {
                return VietnamDelightModBlocks.BANYAN_VINE.get().defaultBlockState();
            }
        } catch (Exception e) {
            // Silent fallback
        }
        return Blocks.VINE.defaultBlockState();
    }
    
    private BlockState getBanyanRootsState() {
        try {
            if (VietnamDelightModBlocks.BANYAN_ROOTS.get() != null) {
                return VietnamDelightModBlocks.BANYAN_ROOTS.get().defaultBlockState();
            }
        } catch (Exception e) {
            // Silent fallback
        }
        return getBanyanVineState();
    }
    
    private BlockState getBanyanRootsPlantState() {
        try {
            if (VietnamDelightModBlocks.BANYAN_ROOTS_PLANT.get() != null) {
                return VietnamDelightModBlocks.BANYAN_ROOTS_PLANT.get().defaultBlockState();
            }
        } catch (Exception e) {
            // Silent fallback
        }
        return getBanyanVineState();
    }
}