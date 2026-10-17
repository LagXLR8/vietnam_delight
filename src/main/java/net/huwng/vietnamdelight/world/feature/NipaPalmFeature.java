package net.huwng.vietnamdelight.world.feature;

import net.huwng.vietnamdelight.init.VietnamDelightModBlocks;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.core.BlockPos;
import net.huwng.vietnamdelight.block.NipaFrondSidesBlock;
import net.huwng.vietnamdelight.block.NipaFruitCropBlock;

public class NipaPalmFeature extends Feature<NoneFeatureConfiguration> {
    // Height constants
    private static final int MIN_HEIGHT = 3;
    private static final int MAX_HEIGHT = 4;
    
    // Probability constants
    private static final float SIDE_FROND_CHANCE = 0.6f;
    private static final float ROOT_SPREAD_CHANCE = 0.8f;
    private static final float DIAGONAL_ROOT_CHANCE = 0.3f;
    private static final float CLUSTER_ROOT_CHANCE = 0.3f;
    private static final float WATERLOGGED_ABOVE_ROOT_CHANCE = 0.3f;
    private static final float RIPE_FRUIT_CHANCE = 0.3f;
    
    // Mud flat generation constants
    private static final int MUD_FLAT_TRIES = 1000;
    private static final int MUD_FLAT_XZ_SPREAD = 10;
    private static final int MUD_FLAT_Y_SPREAD = 4;
    
    // Cached directions array
    private static final Direction[] HORIZONTAL_DIRECTIONS = {
        Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST
    };
    
    public NipaPalmFeature() {
        super(NoneFeatureConfiguration.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos pos = context.origin();
        RandomSource random = context.random();
        
        if (!canPlaceAt(level, pos)) {
            return false;
        }
        
        // Tạo mud flat xung quanh trước
        generateMudFlat(level, pos, random);
        
        int trunkHeight = MIN_HEIGHT + random.nextInt(MAX_HEIGHT - MIN_HEIGHT + 1);
        
        // Đặt nipa_root (thân cây)
        placeNipaRoots(level, pos, trunkHeight, random);
        
        // Đặt nipa_roots xung quanh thân
        placeNipaRootsSurrounding(level, pos, trunkHeight, random);
        
        // Đặt nipa_frond ở trên đỉnh
        BlockPos topPos = pos.above(trunkHeight);
        placeTopFrond(level, topPos);
        
        // Đặt nipa_frond_sides xung quanh (4 hướng)
        placeSideFronds(level, pos, trunkHeight, random);
        
        // Đặt nipa posts xung quanh (60% chance mỗi cây)
        if (random.nextFloat() < 0.6f) {
            placeNipaPostsNearby(level, pos, random);
        }
        
        return true;
    }

    private void generateMudFlat(WorldGenLevel level, BlockPos origin, RandomSource random) {
        for (int i = 0; i < MUD_FLAT_TRIES; i++) {
            // Random vị trí xung quanh
            int xOffset = random.nextInt(MUD_FLAT_XZ_SPREAD * 2 + 1) - MUD_FLAT_XZ_SPREAD;
            int zOffset = random.nextInt(MUD_FLAT_XZ_SPREAD * 2 + 1) - MUD_FLAT_XZ_SPREAD;
            int yOffset = random.nextInt(MUD_FLAT_Y_SPREAD * 2 + 1) - MUD_FLAT_Y_SPREAD;
            
            BlockPos targetPos = origin.offset(xOffset, yOffset, zOffset);
            BlockState currentState = level.getBlockState(targetPos);
            
            // Kiểm tra block hiện tại có thể thay thế không
            if (!currentState.is(Blocks.DIRT) && 
                !currentState.is(Blocks.COARSE_DIRT) && 
                !currentState.is(Blocks.GRASS_BLOCK) && 
                !currentState.is(Blocks.SAND) && 
                !currentState.is(Blocks.GRAVEL)) {
                continue;
            }
            
            // Chọn ngẫu nhiên block để đặt theo weighted (bao gồm muddy_nipa_roots)
            BlockState blockToPlace = getWeightedMudBlock(random);
            level.setBlock(targetPos, blockToPlace, 3);
        }
    }

    private BlockState getWeightedMudBlock(RandomSource random) {
        // Total weight = 10 + 3 + 2 + 2 = 17
        int roll = random.nextInt(17);
        
        if (roll < 10) {
            // 10/17 chance = ~58.8%
            return Blocks.MUD.defaultBlockState();
        } else if (roll < 13) {
            // 3/17 chance = ~17.6%
            return Blocks.COARSE_DIRT.defaultBlockState();
        } else if (roll < 15) {
            // 2/17 chance = ~11.8%
            return Blocks.GRAVEL.defaultBlockState();
        } else {
            // 2/17 chance = ~11.8%
            return VietnamDelightModBlocks.MUDDY_NIPA_ROOTS.get().defaultBlockState();
        }
    }

    private void placeNipaRoots(WorldGenLevel level, BlockPos pos, int height, RandomSource random) {
        BlockState baseState = VietnamDelightModBlocks.NIPA_ROOT.get().defaultBlockState();
        
        for (int i = 0; i < height; i++) {
            BlockPos currentPos = pos.above(i);
            
            if (!canPlaceRootAt(level, currentPos)) {
                continue;
            }
            
            BlockState blockState = baseState;
            
            if (level.isWaterAt(currentPos) && baseState.hasProperty(BlockStateProperties.WATERLOGGED)) {
                blockState = baseState.setValue(BlockStateProperties.WATERLOGGED, true);
            }
            
            level.setBlock(currentPos, blockState, 3);
        }
    }

    private void placeNipaRootsSurrounding(WorldGenLevel level, BlockPos basePos, int trunkHeight, RandomSource random) {
        int rootLayers = Math.min(2, trunkHeight);
    
        for (int height = 0; height < rootLayers; height++) {
            BlockPos currentLevel = basePos.above(height);
    
            for (Direction dir : HORIZONTAL_DIRECTIONS) {
                if (random.nextFloat() < ROOT_SPREAD_CHANCE) {
                    BlockPos sidePos = currentLevel.relative(dir);
                    placeNipaRootsBlock(level, sidePos, random);
    
                    // 30% cơ hội mọc chéo xuống
                    if (random.nextFloat() < DIAGONAL_ROOT_CHANCE) {
                        BlockPos diagonalDown = sidePos.relative(dir).below();
                        placeMuddyNipaRootsBlock(level, diagonalDown);
                    }
    
                    // 30% cơ hội mọc thêm phía dưới
                    if (random.nextFloat() < CLUSTER_ROOT_CHANCE) {
                        BlockPos belowPos = sidePos.below();
                        placeMuddyNipaRootsBlock(level, belowPos);
                    }
                }
            }
        }
    }

    private void placeNipaRootsBlock(WorldGenLevel level, BlockPos pos, RandomSource random) {
        if (!canPlaceRootAt(level, pos)) {
            return;
        }
    
        BlockState rootState = VietnamDelightModBlocks.NIPA_ROOTS.get().defaultBlockState();
        boolean isWaterlogged = false;
    
        if (level.isWaterAt(pos) && rootState.hasProperty(BlockStateProperties.WATERLOGGED)) {
            rootState = rootState.setValue(BlockStateProperties.WATERLOGGED, true);
            isWaterlogged = true;
        }
    
        level.setBlock(pos, rootState, 3);
        
        // 30% cơ hội đặt nipa_roots phía trên nếu waterlogged
        if (isWaterlogged && random.nextFloat() < WATERLOGGED_ABOVE_ROOT_CHANCE) {
            BlockPos abovePos = pos.above();
            if (canPlaceRootAt(level, abovePos)) {
                BlockState aboveRootState = VietnamDelightModBlocks.NIPA_ROOTS.get().defaultBlockState();
                
                if (level.isWaterAt(abovePos) && aboveRootState.hasProperty(BlockStateProperties.WATERLOGGED)) {
                    aboveRootState = aboveRootState.setValue(BlockStateProperties.WATERLOGGED, true);
                }
                
                level.setBlock(abovePos, aboveRootState, 3);
            }
        }
    }

    private void placeMuddyNipaRootsBlock(WorldGenLevel level, BlockPos pos) {
        if (!canPlaceRootAt(level, pos)) {
            return;
        }
    
        BlockState muddyRootState = VietnamDelightModBlocks.MUDDY_NIPA_ROOTS.get().defaultBlockState();
        level.setBlock(pos, muddyRootState, 3);
    }

    private void placeTopFrond(WorldGenLevel level, BlockPos topPos) {
        if (!canPlaceRootAt(level, topPos)) {
            return;
        }
    
        Direction randomDirection = Direction.Plane.HORIZONTAL.getRandomDirection(level.getRandom());
    
        BlockState frondState = VietnamDelightModBlocks.NIPA_FROND.get()
            .defaultBlockState()
            .setValue(BlockStateProperties.HORIZONTAL_FACING, randomDirection);
    
        level.setBlock(topPos, frondState, 3);
    }

    private void placeSideFronds(WorldGenLevel level, BlockPos basePos, int trunkHeight, RandomSource random) {
        BlockPos frondLevelPos = basePos.above(Math.max(0, trunkHeight - 1));
        int fruitDirection = random.nextInt(4);
        boolean hasPlacedFruit = false;
    
        for (int i = 0; i < HORIZONTAL_DIRECTIONS.length; i++) {
            Direction dir = HORIZONTAL_DIRECTIONS[i];
    
            if (random.nextFloat() < SIDE_FROND_CHANCE) {
                BlockPos sidePos = frondLevelPos.relative(dir);
    
                if (!canPlaceRootAt(level, sidePos)) {
                    continue;
                }
    
                if (i == fruitDirection && !hasPlacedFruit) {
                    placeFrondSidesWithFruit(level, sidePos, dir, random);
                    hasPlacedFruit = true;
                } else {
                    placeFrondSidesNormal(level, sidePos, dir);
                }
            }
        }
    }

    private void placeFrondSidesNormal(WorldGenLevel level, BlockPos pos, Direction facing) {
        BlockState frondState = VietnamDelightModBlocks.NIPA_FROND_SIDES.get().defaultBlockState();
        
        if (frondState.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
            frondState = frondState.setValue(BlockStateProperties.HORIZONTAL_FACING, facing);
        }
        
        level.setBlock(pos, frondState, 3);
    }

    private void placeFrondSidesWithFruit(WorldGenLevel level, BlockPos pos, Direction facing, RandomSource random) {
        BlockPos belowPos = pos.below();
        BlockState belowState = level.getBlockState(belowPos);
        
        // Cho phép đặt nếu phía dưới là không khí, nước hoặc có thể thay thế
        if (!belowState.isAir() && !belowState.is(Blocks.WATER) && !belowState.canBeReplaced()) {
            placeFrondSidesNormal(level, pos, facing);
            return;
        }
        
        BlockState frondState = VietnamDelightModBlocks.NIPA_FROND_SIDES.get().defaultBlockState();
    
        if (frondState.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
            frondState = frondState.setValue(BlockStateProperties.HORIZONTAL_FACING, facing);
        }
    
        frondState = frondState.setValue(NipaFrondSidesBlock.BLOCKSTATE, 1);
        level.setBlock(pos, frondState, 3);
    
        // Đặt quả bất kể là nước hay không khí
        placeFruit(level, belowPos, random);
    }

    private void placeFruit(WorldGenLevel level, BlockPos pos, RandomSource random) {
        BlockState fruitState;
        boolean isWater = level.isWaterAt(pos);
    
        if (random.nextFloat() < RIPE_FRUIT_CHANCE) {
            // Nipa fruit chín (không waterlogged)
            fruitState = VietnamDelightModBlocks.NIPA_FRUIT.get().defaultBlockState();
        } else {
            // Nipa fruit crop (có thể waterlogged)
            fruitState = VietnamDelightModBlocks.NIPA_FRUIT_CROP.get().defaultBlockState();
            int cropState = random.nextInt(7) + 1;
    
            if (fruitState.hasProperty(NipaFruitCropBlock.BLOCKSTATE)) {
                fruitState = fruitState.setValue(NipaFruitCropBlock.BLOCKSTATE, cropState);
            }
            
            // Áp dụng waterlogged nếu vị trí là nước
            if (isWater && fruitState.hasProperty(BlockStateProperties.WATERLOGGED)) {
                fruitState = fruitState.setValue(BlockStateProperties.WATERLOGGED, true);
            }
        }
    
        level.setBlock(pos, fruitState, 3);
    }

    private void placeNipaPostsNearby(WorldGenLevel level, BlockPos palmPos, RandomSource random) {
        // Thử spawn 3-6 posts xung quanh cây palm
        int postCount = 3 + random.nextInt(4); // 3-6 posts
        int successfulPlacements = 0;
        
        for (int i = 0; i < postCount * 3 && successfulPlacements < postCount; i++) {
            // Random vị trí trong bán kính 3-8 blocks
            int radius = 3 + random.nextInt(6);
            double angle = random.nextDouble() * Math.PI * 2;
            
            int xOffset = (int)(Math.cos(angle) * radius);
            int zOffset = (int)(Math.sin(angle) * radius);
            
            // Tìm vị trí phù hợp - tìm từ dưới lên
            BlockPos searchStart = palmPos.offset(xOffset, -5, zOffset);
            
            // Tìm kiếm trong khoảng 10 blocks theo chiều Y
            for (int yOffset = 0; yOffset <= 10; yOffset++) {
                BlockPos testPos = searchStart.above(yOffset);
                
                if (canPlaceNipaPost(level, testPos)) {
                    placeNipaPost(level, testPos, random);
                    successfulPlacements++;
                    break; // Đã đặt post, chuyển sang vị trí tiếp theo
                }
            }
        }
    }

    private boolean canPlaceNipaPost(WorldGenLevel level, BlockPos pos) {
        BlockState groundState = level.getBlockState(pos);
        
        // Phải là bùn
        if (!groundState.is(Blocks.MUD)) {
            return false;
        }
        
        // Kiểm tra phía trên có nước không
        BlockPos waterPos = pos.above();
        BlockState waterState = level.getBlockState(waterPos);
        
        if (!waterState.is(Blocks.WATER)) {
            return false;
        }
        
        // Kiểm tra độ sâu nước = ĐÚNG 1 block
        BlockPos aboveWater = waterPos.above();
        BlockState aboveWaterState = level.getBlockState(aboveWater);
        
        // Phía trên nước PHẢI là không khí (không được là nước)
        if (aboveWaterState.is(Blocks.WATER)) {
            return false; // Nước sâu hơn 1 block -> reject
        }
        
        return aboveWaterState.isAir() || aboveWaterState.canBeReplaced();
    }

    private void placeNipaPost(WorldGenLevel level, BlockPos pos, RandomSource random) {
        // Đặt Nipa_Post
        BlockPos postPos = pos.above();
        
        BlockState nipaPostState = VietnamDelightModBlocks.NIPA_POST.get().defaultBlockState();
        
        // Waterlogged vì đặt trong nước
        if (nipaPostState.hasProperty(BlockStateProperties.WATERLOGGED)) {
            nipaPostState = nipaPostState.setValue(BlockStateProperties.WATERLOGGED, true);
        }
        
        level.setBlock(postPos, nipaPostState, 3);
        
        // Đặt nipa_frond_post phía trên với random rotation
        BlockPos frondPos = postPos.above();
        BlockState frondPostState = VietnamDelightModBlocks.NIPA_FROND_POST.get().defaultBlockState();
        
        // Random rotation (4 hướng)
        Direction randomDirection = Direction.Plane.HORIZONTAL.getRandomDirection(random);
        
        if (frondPostState.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
            frondPostState = frondPostState.setValue(BlockStateProperties.HORIZONTAL_FACING, randomDirection);
        }
        
        level.setBlock(frondPos, frondPostState, 3);
    }

    private boolean canPlaceAt(WorldGenLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        
        if (!state.is(Blocks.MUD) && !state.is(Blocks.DIRT) && !state.is(Blocks.SAND)) {
            return false;
        }
        
        // Kiểm tra có nước xung quanh
        BlockPos above = pos.above();
        return level.isWaterAt(above) || 
               level.isWaterAt(pos.east()) ||
               level.isWaterAt(pos.west()) ||
               level.isWaterAt(pos.north()) ||
               level.isWaterAt(pos.south());
    }

    private boolean canPlaceRootAt(WorldGenLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return state.isAir() || 
               state.canBeReplaced() ||
               state.is(Blocks.WATER) ||
               state.is(Blocks.DIRT) || 
               state.is(Blocks.GRASS_BLOCK) || 
               state.is(Blocks.SAND) ||
               state.is(Blocks.MUD);
    }
}