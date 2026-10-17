package net.huwng.vietnamdelight.world.feature;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.huwng.vietnamdelight.init.VietnamDelightModBlocks;

import java.util.ArrayList;
import java.util.List;

public class BanyanTreeFeature extends Feature<NoneFeatureConfiguration> {
    
    public BanyanTreeFeature() {
        super(NoneFeatureConfiguration.CODEC);
    }
    
    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel world = context.level();
        BlockPos pos = context.origin();
        RandomSource random = context.random();
        if (!canPlaceTree(world, pos)) {
            return false;
        }
        int trunkHeight = 7 + random.nextInt(5);
        int branchStartHeight = 3 + random.nextInt(2);
        generateTrunk(world, pos, trunkHeight, random);
        generateBranches(world, pos.above(branchStartHeight), trunkHeight - branchStartHeight, random);
        return true;
    }
    
    private boolean canPlaceTree(WorldGenLevel world, BlockPos pos) {
        BlockState belowState = world.getBlockState(pos.below());
        if (!belowState.is(Blocks.GRASS_BLOCK) && !belowState.is(Blocks.DIRT)) {
            return false;
        }
        for (int y = 0; y < 5; y++) {
            BlockPos checkPos = pos.above(y);
            if (!world.getBlockState(checkPos).isAir() && 
                !world.getBlockState(checkPos).is(Blocks.GRASS) &&
                !world.getBlockState(checkPos).is(Blocks.TALL_GRASS)) {
                return false;
            }
        }
        return true;
    }
    
    private void generateTrunk(WorldGenLevel world, BlockPos pos, int height, RandomSource random) {
        BlockState log = Blocks.MANGROVE_LOG.defaultBlockState();
        
        for (int y = 0; y < height; y++) {
            BlockPos trunkPos = pos.above(y);
            world.setBlock(trunkPos, log, 3);
            if (y == 0) {
                for (Direction dir : Direction.Plane.HORIZONTAL) {
                    BlockPos woodPos = trunkPos.relative(dir);
                    if (world.getBlockState(woodPos).isAir()) {
                        world.setBlock(woodPos, Blocks.MANGROVE_WOOD.defaultBlockState(), 3);
                        BlockPos belowWoodPos = woodPos.below();
                        if (world.getBlockState(belowWoodPos).isAir()) {
                            world.setBlock(belowWoodPos, Blocks.MANGROVE_WOOD.defaultBlockState(), 3);
                        }
                    }
                }
            }
            if (y < 4 && random.nextFloat() < 0.5) {
                Direction dir = Direction.Plane.HORIZONTAL.getRandomDirection(random);
                BlockPos rootPos = trunkPos.relative(dir);
                if (world.getBlockState(rootPos).isAir()) {
                    placeRootColumn(world, rootPos, random, 1 + random.nextInt(6));
                }
            }
        }
    }
    
    private void generateBranches(WorldGenLevel world, BlockPos startPos, int branchHeight, RandomSource random) {
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
                world.setBlock(currentPos, Blocks.MANGROVE_WOOD.defaultBlockState(), 3);
                if (j > 2 && random.nextFloat() < 0.3) {
                    BlockPos rootPos = currentPos.below();
                    if (world.getBlockState(rootPos).isAir()) {
                        placeRootColumn(world, rootPos, random, 1 + random.nextInt(6));
                    }
                }
                if (j >= branchLength - 2) {
                    branchPoints.add(currentPos);
                }
            }
        }
        for (BlockPos branchPoint : branchPoints) {
            generateLeavesCluster(world, branchPoint, random);
        }
    }
    
    private void generateLeavesCluster(WorldGenLevel world, BlockPos center, RandomSource random) {
        int radius = 3 + random.nextInt(2);
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    double distance = Math.sqrt(x * x + y * y + z * z);
                    if (distance <= radius + random.nextFloat() * 0.5) {
                        BlockPos leafPos = center.offset(x, y, z);
                        if (world.getBlockState(leafPos).isAir()) {
                            BlockState leafBlock = random.nextFloat() < 0.7 
                                ? VietnamDelightModBlocks.BANYAN_LEAVES.get().defaultBlockState()
                                : VietnamDelightModBlocks.FLOWERING_BANYAN_LEAVES.get().defaultBlockState();
                            if (leafBlock.hasProperty(BlockStateProperties.PERSISTENT)) {
                                leafBlock = leafBlock.setValue(BlockStateProperties.PERSISTENT, false);
                            }
                            if (leafBlock.hasProperty(BlockStateProperties.DISTANCE)) {
                                leafBlock = leafBlock.setValue(BlockStateProperties.DISTANCE, 4);
                            }
                            world.setBlock(leafPos, leafBlock, 3);
                            if (y == -radius || (y <= 0 && random.nextFloat() < 0.4)) {
                                generateHangingFeatures(world, leafPos.below(), random);
                            }
                        }
                    }
                }
            }
        }
        for (int i = 0; i < 4 + random.nextInt(4); i++) {
            Direction dir = Direction.Plane.HORIZONTAL.getRandomDirection(random);
            BlockPos droopPos = center.relative(dir, radius).below();
            if (world.getBlockState(droopPos).isAir()) {
                BlockState leafBlock = random.nextFloat() < 0.8
                    ? VietnamDelightModBlocks.BANYAN_LEAVES.get().defaultBlockState()
                    : VietnamDelightModBlocks.FLOWERING_BANYAN_LEAVES.get().defaultBlockState();
                if (leafBlock.hasProperty(BlockStateProperties.PERSISTENT)) {
                    leafBlock = leafBlock.setValue(BlockStateProperties.PERSISTENT, false);
                }
                if (leafBlock.hasProperty(BlockStateProperties.DISTANCE)) {
                    leafBlock = leafBlock.setValue(BlockStateProperties.DISTANCE, 4);
                }
                world.setBlock(droopPos, leafBlock, 3);
                generateHangingFeatures(world, droopPos.below(), random);
            }
        }
    }
    
    private void generateHangingFeatures(WorldGenLevel world, BlockPos startPos, RandomSource random) {
        if (!world.getBlockState(startPos).isAir()) {
            return;
        }
        if (random.nextFloat() < 0.3) {
            world.setBlock(startPos, VietnamDelightModBlocks.BANYAN_VINE.get().defaultBlockState(), 3);
        } else {
            int rootLength = 1 + random.nextInt(6);
            placeRootColumn(world, startPos, random, rootLength);
        }
    }
    
    private void placeRootColumn(WorldGenLevel world, BlockPos startPos, RandomSource random, int maxLength) {
        BlockPos currentPos = startPos;
        int actualLength = 0;
        for (int i = 0; i < maxLength; i++) {
            if (!world.getBlockState(currentPos.below(i)).isAir()) {
                actualLength = i;
                break;
            }
            actualLength = i + 1;
        }
        if (actualLength == 0) return;
        for (int i = 0; i < actualLength; i++) {
            BlockPos placePos = startPos.below(i);
            
            if (i == actualLength - 1) {
                world.setBlock(placePos, VietnamDelightModBlocks.BANYAN_ROOTS.get().defaultBlockState(), 3);
            } else {
                world.setBlock(placePos, VietnamDelightModBlocks.BANYAN_ROOTS_PLANT.get().defaultBlockState(), 3);
            }
        }
    }
}