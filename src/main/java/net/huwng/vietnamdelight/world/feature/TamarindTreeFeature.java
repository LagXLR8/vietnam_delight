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

public class TamarindTreeFeature extends Feature<NoneFeatureConfiguration> {
    
    public TamarindTreeFeature() {
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
        
        // Thân cây cao 3-4 blocks
        int trunkHeight = 3 + random.nextInt(2);
        
        // Tạo thân cây
        generateTrunk(world, pos, trunkHeight, random);
        
        // Tạo tán lá ở đỉnh thân
        generateLeavesCluster(world, pos.above(trunkHeight), random);
        
        // Tạo 1 cành và tán lá của nó
        generateBranch(world, pos.above(trunkHeight), random);
        
        return true;
    }
    
    private boolean canPlaceTree(WorldGenLevel world, BlockPos pos) {
        BlockState belowState = world.getBlockState(pos.below());
        if (!belowState.is(Blocks.GRASS_BLOCK) && !belowState.is(Blocks.DIRT)) {
            return false;
        }
        
        // Kiểm tra không gian cho cây
        for (int y = 0; y < 6; y++) {
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
        BlockState log = Blocks.JUNGLE_LOG.defaultBlockState();
        
        for (int y = 0; y < height; y++) {
            BlockPos trunkPos = pos.above(y);
            world.setBlock(trunkPos, log, 3);
        }
    }
    
    private void generateBranch(WorldGenLevel world, BlockPos startPos, RandomSource random) {
        // Chọn hướng ngẫu nhiên cho cành
        Direction dir = Direction.Plane.HORIZONTAL.getRandomDirection(random);
        int branchLength = 2 + random.nextInt(2); // 2-3 blocks
        
        BlockPos currentPos = startPos;
        
        // Tạo cành
        for (int i = 0; i < branchLength; i++) {
            currentPos = currentPos.relative(dir);
            world.setBlock(currentPos, Blocks.JUNGLE_LOG.defaultBlockState(), 3);
        }
        
        // Tạo tán lá ở cuối cành
        generateLeavesCluster(world, currentPos, random);
    }
    
    private void generateLeavesCluster(WorldGenLevel world, BlockPos center, RandomSource random) {
        int radius = 2;
        List<BlockPos> floweringLeavesPositions = new ArrayList<>();
        
        // Tạo tán lá hình cầu
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    double distance = Math.sqrt(x * x + y * y + z * z);
                    if (distance <= radius + random.nextFloat() * 0.5) {
                        BlockPos leafPos = center.offset(x, y, z);
                        if (world.getBlockState(leafPos).isAir()) {
                            // 70% tamarind_leaves, 30% flowering_tamarind_leaves
                            BlockState leafBlock;
                            if (random.nextFloat() < 0.7) {
                                leafBlock = VietnamDelightModBlocks.TAMARIND_LEAVES.get().defaultBlockState();
                            } else {
                                leafBlock = VietnamDelightModBlocks.FLOWERING_TAMARIND_LEAVES.get().defaultBlockState();
                                floweringLeavesPositions.add(leafPos);
                            }
                            
                            if (leafBlock.hasProperty(BlockStateProperties.PERSISTENT)) {
                                leafBlock = leafBlock.setValue(BlockStateProperties.PERSISTENT, false);
                            }
                            if (leafBlock.hasProperty(BlockStateProperties.DISTANCE)) {
                                leafBlock = leafBlock.setValue(BlockStateProperties.DISTANCE, 4);
                            }
                            
                            world.setBlock(leafPos, leafBlock, 3);
                        }
                    }
                }
            }
        }
        
        // Đặt tamarind_flowers xung quanh flowering_tamarind_leaves
        for (BlockPos floweringLeafPos : floweringLeavesPositions) {
            placeFlowersAroundLeaf(world, floweringLeafPos, random);
        }
    }
    
    private void placeFlowersAroundLeaf(WorldGenLevel world, BlockPos leafPos, RandomSource random) {
        // Kiểm tra tất cả 6 hướng (4 ngang + trên + dưới)
        Direction[] allDirections = Direction.values();
        
        for (Direction dir : allDirections) {
            // Có 40% cơ hội đặt hoa ở mỗi hướng
            if (random.nextFloat() < 0.4) {
                BlockPos flowerPos = leafPos.relative(dir);
                
                // Kiểm tra xem vị trí có thể đặt hoa không
                if (world.getBlockState(flowerPos).isAir()) {
                    BlockState flowerState = VietnamDelightModBlocks.TAMARIND_FLOWERS.get().defaultBlockState();
                    
                    // Xoay hoa theo hướng bám vào lá (không cần getOpposite vì hoa đã quay về phía block nó bám vào)
                    if (flowerState.hasProperty(BlockStateProperties.FACING)) {
                        flowerState = flowerState.setValue(BlockStateProperties.FACING, dir);
                    }
                    
                    world.setBlock(flowerPos, flowerState, 3);
                }
            }
        }
    }
}