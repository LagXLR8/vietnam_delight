package net.huwng.vietnamdelight.world.features;

import net.huwng.vietnamdelight.init.VietnamDelightModBlocks;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.core.BlockPos;

public class NipaPalmGrower {
    // Height constants - cây cao 1-2 blocks từ propagule
    private static final int MIN_HEIGHT = 2;
    private static final int MAX_HEIGHT = 3;
    
    // Probability constants
    private static final float SIDE_FROND_CHANCE = 0.6f;
    private static final float ROOT_SPREAD_CHANCE = 0.8f;
    private static final float DIAGONAL_ROOT_CHANCE = 0.3f;
    private static final float CLUSTER_ROOT_CHANCE = 0.3f;
    private static final float WATERLOGGED_ABOVE_ROOT_CHANCE = 0.3f;
    
    // Cached directions
    private static final Direction[] HORIZONTAL_DIRECTIONS = {
        Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST
    };
    
    /**
     * Grow Nipa Palm tree từ propagule
     * @param world Level accessor
     * @param basePos Vị trí base (bùn/đất/cát) - phía dưới propagule
     * @param propagulePos Vị trí của propagule (trong nước) - sẽ trở thành trunk đầu tiên
     * @param random Random source
     * @return true nếu grow thành công
     */
    public static boolean growNipaPalm(LevelAccessor world, BlockPos basePos, BlockPos propagulePos, RandomSource random) {
        if (!canPlaceAt(world, basePos)) {
            return false;
        }
        
        int trunkHeight = MIN_HEIGHT + random.nextInt(MAX_HEIGHT - MIN_HEIGHT + 1);
        
        // Kiểm tra không gian PHÍA TRÊN propagule
        if (!hasEnoughSpace(world, propagulePos.above(), trunkHeight)) {
            return false;
        }
        
        // Đặt nipa_root (thân cây chính) - bắt đầu từ PHÍA TRÊN propagule
        // Propagule sẽ được thay thế thủ công trong tryGrow()
        if (trunkHeight > 1) {
            placeTrunk(world, propagulePos.above(), trunkHeight - 1, random);
        }
        
        // Đặt nipa_roots xung quanh thân (hệ rễ) - base từ basePos
        placeSurroundingRoots(world, basePos, Math.min(2, trunkHeight + 1), random);
        
        // Đặt nipa_frond ở trên đỉnh
        BlockPos topPos = propagulePos.above(trunkHeight);
        placeTopFrond(world, topPos, random);
        
        // Đặt nipa_frond_sides xung quanh (4 hướng)
        placeSideFronds(world, propagulePos, trunkHeight, random);
        
        return true;
    }

    private static boolean hasEnoughSpace(LevelAccessor world, BlockPos startPos, int height) {
        // Kiểm tra ít nhất 2 ô phía trên propagule
        int checkHeight = Math.max(2, height);
        
        for (int i = 0; i < checkHeight; i++) {
            BlockPos checkPos = startPos.above(i);
            BlockState state = world.getBlockState(checkPos);
            
            if (!state.isAir() && !state.is(Blocks.WATER)) {
                return false;
            }
        }
        return true;
    }

    private static void placeTrunk(LevelAccessor world, BlockPos startPos, int height, RandomSource random) {
        BlockState rootState = VietnamDelightModBlocks.NIPA_ROOT.get().defaultBlockState();
        
        for (int i = 0; i < height; i++) {
            BlockPos currentPos = startPos.above(i);
            
            BlockState stateAtPos = world.getBlockState(currentPos);
            if (!stateAtPos.isAir() && !stateAtPos.is(Blocks.WATER) && !stateAtPos.canBeReplaced()) {
                continue;
            }
            
            world.setBlock(currentPos, rootState, 3);
        }
    }

    private static void placeSurroundingRoots(LevelAccessor world, BlockPos basePos, int rootLayers, RandomSource random) {
        for (int height = 0; height < rootLayers; height++) {
            BlockPos currentLevel = basePos.above(height);
    
            for (Direction dir : HORIZONTAL_DIRECTIONS) {
                if (random.nextFloat() >= ROOT_SPREAD_CHANCE) {
                    continue;
                }
                
                BlockPos sidePos = currentLevel.relative(dir);
                placeRootsBlock(world, sidePos, random);
    
                // 30% cơ hội mọc chéo xuống
                if (random.nextFloat() < DIAGONAL_ROOT_CHANCE) {
                    BlockPos diagonalDown = sidePos.relative(dir).below();
                    placeMuddyRootsBlock(world, diagonalDown);
                }
    
                // 30% cơ hội mọc thêm phía dưới
                if (random.nextFloat() < CLUSTER_ROOT_CHANCE) {
                    BlockPos belowPos = sidePos.below();
                    placeMuddyRootsBlock(world, belowPos);
                }
            }
        }
    }

    private static void placeRootsBlock(LevelAccessor world, BlockPos pos, RandomSource random) {
        BlockState stateAtPos = world.getBlockState(pos);
        if (!stateAtPos.isAir() && !stateAtPos.is(Blocks.WATER) && !stateAtPos.canBeReplaced()) {
            return;
        }
    
        BlockState rootsState = VietnamDelightModBlocks.NIPA_ROOTS.get().defaultBlockState();
        boolean isWaterlogged = false;
    
        if (world.getFluidState(pos).isSource() && rootsState.hasProperty(BlockStateProperties.WATERLOGGED)) {
            rootsState = rootsState.setValue(BlockStateProperties.WATERLOGGED, true);
            isWaterlogged = true;
        }
    
        world.setBlock(pos, rootsState, 3);
        
        // 30% cơ hội đặt nipa_roots phía trên nếu waterlogged
        if (isWaterlogged && random.nextFloat() < WATERLOGGED_ABOVE_ROOT_CHANCE) {
            BlockPos abovePos = pos.above();
            BlockState aboveState = world.getBlockState(abovePos);
            
            if (aboveState.isAir() || aboveState.is(Blocks.WATER) || aboveState.canBeReplaced()) {
                BlockState aboveRootsState = VietnamDelightModBlocks.NIPA_ROOTS.get().defaultBlockState();
                
                if (world.getFluidState(abovePos).isSource() && aboveRootsState.hasProperty(BlockStateProperties.WATERLOGGED)) {
                    aboveRootsState = aboveRootsState.setValue(BlockStateProperties.WATERLOGGED, true);
                }
                
                world.setBlock(abovePos, aboveRootsState, 3);
            }
        }
    }

    private static void placeMuddyRootsBlock(LevelAccessor world, BlockPos pos) {
        BlockState stateAtPos = world.getBlockState(pos);
        
        // Chỉ đặt muddy roots trên bùn, đất, hoặc cát
        if (!stateAtPos.is(Blocks.MUD) && !stateAtPos.is(Blocks.DIRT) && !stateAtPos.is(Blocks.SAND)) {
            return;
        }
    
        BlockState muddyRootsState = VietnamDelightModBlocks.MUDDY_NIPA_ROOTS.get().defaultBlockState();
        world.setBlock(pos, muddyRootsState, 3);
    }

    private static void placeTopFrond(LevelAccessor world, BlockPos topPos, RandomSource random) {
        BlockState stateAtPos = world.getBlockState(topPos);
        if (!stateAtPos.isAir() && !stateAtPos.is(Blocks.WATER) && !stateAtPos.canBeReplaced()) {
            return;
        }
    
        Direction randomDirection = HORIZONTAL_DIRECTIONS[random.nextInt(HORIZONTAL_DIRECTIONS.length)];
    
        BlockState frondState = VietnamDelightModBlocks.NIPA_FROND.get()
            .defaultBlockState()
            .setValue(BlockStateProperties.HORIZONTAL_FACING, randomDirection);
    
        world.setBlock(topPos, frondState, 3);
    }

    private static void placeSideFronds(LevelAccessor world, BlockPos basePos, int trunkHeight, RandomSource random) {
        BlockPos frondLevelPos = basePos.above(Math.max(0, trunkHeight - 1));
    
        for (Direction dir : HORIZONTAL_DIRECTIONS) {
            if (random.nextFloat() < SIDE_FROND_CHANCE) {
                BlockPos sidePos = frondLevelPos.relative(dir);
    
                BlockState stateAtPos = world.getBlockState(sidePos);
                if (!stateAtPos.isAir() && !stateAtPos.is(Blocks.WATER) && !stateAtPos.canBeReplaced()) {
                    continue;
                }
    
                placeFrondSidesBlock(world, sidePos, dir);
            }
        }
    }

    private static void placeFrondSidesBlock(LevelAccessor world, BlockPos pos, Direction facing) {
        BlockState frondState = VietnamDelightModBlocks.NIPA_FROND_SIDES.get().defaultBlockState();
        
        if (frondState.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
            frondState = frondState.setValue(BlockStateProperties.HORIZONTAL_FACING, facing);
        }
        
        world.setBlock(pos, frondState, 3);
    }

    private static boolean canPlaceAt(LevelAccessor world, BlockPos basePos) {
        BlockState state = world.getBlockState(basePos);
        
        if (!state.is(Blocks.MUD) && !state.is(Blocks.DIRT) && !state.is(Blocks.SAND)) {
            return false;
        }
        
        BlockPos above = basePos.above();
        return world.getFluidState(above).isSource() || 
               world.getFluidState(basePos.north()).isSource() ||
               world.getFluidState(basePos.south()).isSource() ||
               world.getFluidState(basePos.east()).isSource() ||
               world.getFluidState(basePos.west()).isSource();
    }
}