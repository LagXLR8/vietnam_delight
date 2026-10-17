package net.huwng.vietnamdelight.world.feature;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.huwng.vietnamdelight.init.VietnamDelightModBlocks;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WaterPrimroseFeature extends Feature<NoneFeatureConfiguration> {
    
    public WaterPrimroseFeature() {
        super(NoneFeatureConfiguration.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource random = context.random();

        // KIỂM TRA: Chỉ spawn gần Wild Lotus Flowers (trong vòng 32 blocks)
        if (!hasLotusFlowerNearby(level, origin)) {
            return false;
        }

        // Tìm điểm trung tâm trên mặt nước
        BlockPos centerPos = findWaterSurface(level, origin, random);
        if (centerPos == null) {
            return false;
        }

        Set<BlockPos> placedPositions = new HashSet<>();
        
        // Tạo vùng path trung tâm (3-5 blocks)
        List<BlockPos> centerPath = generateCenterPath(level, centerPos, random);
        for (BlockPos pos : centerPath) {
            if (canPlaceOnWaterSurface(level, pos)) {
                placeBlock(level, pos, random, placedPositions);
            }
        }

        // Từ mỗi điểm trong center path, tạo các nhánh kéo dài ra
        for (BlockPos pathPos : centerPath) {
            // 70% cơ hội tạo nhánh từ mỗi điểm (tăng từ 60%)
            if (random.nextFloat() < 0.7f) {
                List<BlockPos> branch = generateBranch(level, pathPos, random);
                
                // Đặt blocks theo nhánh
                for (BlockPos branchPos : branch) {
                    if (canPlaceOnWaterSurface(level, branchPos) && !placedPositions.contains(branchPos)) {
                        placeBlock(level, branchPos, random, placedPositions);
                    }
                }
                
                // Tạo path tròn ở cuối nhánh với Duckweed và Water Lettuce
                if (!branch.isEmpty()) {
                    BlockPos branchEnd = branch.get(branch.size() - 1);
                    generateCircularEnd(level, branchEnd, random, placedPositions);
                }
            }
        }

        return !placedPositions.isEmpty();
    }

    private BlockPos findWaterSurface(WorldGenLevel level, BlockPos origin, RandomSource random) {
        // Tìm trong vùng 16x16
        for (int attempt = 0; attempt < 15; attempt++) {
            int x = origin.getX() + random.nextInt(16) - 8;
            int z = origin.getZ() + random.nextInt(16) - 8;
            
            // Quét từ trên xuống để tìm mặt nước
            for (int y = origin.getY() + 10; y > origin.getY() - 10; y--) {
                BlockPos checkPos = new BlockPos(x, y, z);
                if (canPlaceOnWaterSurface(level, checkPos)) {
                    return checkPos;
                }
            }
        }
        return null;
    }

    private List<BlockPos> generateCenterPath(WorldGenLevel level, BlockPos start, RandomSource random) {
        List<BlockPos> path = new ArrayList<>();
        BlockPos current = start;
        
        // Path trung tâm DÀI VÀ TO: 8-15 blocks
        int pathLength = 8 + random.nextInt(8);
        Direction direction = Direction.Plane.HORIZONTAL.getRandomDirection(random);
        
        for (int i = 0; i < pathLength; i++) {
            path.add(current);
            
            // Thêm blocks xung quanh để làm path TO HƠN
            if (random.nextFloat() < 0.7f) {
                Direction perpendicular = direction.getClockWise();
                BlockPos side1 = current.relative(perpendicular);
                BlockPos side2 = current.relative(perpendicular.getOpposite());
                
                if (isNearWaterSurface(level, side1)) {
                    path.add(side1);
                }
                if (isNearWaterSurface(level, side2)) {
                    path.add(side2);
                }
            }
            
            // Di chuyển theo hướng, ít thay đổi hơn để đường thẳng hơn
            if (random.nextFloat() < 0.2f) {
                direction = random.nextBoolean() ? 
                    direction.getClockWise() : 
                    direction.getCounterClockWise();
            }
            
            current = current.relative(direction);
            
            // Kiểm tra còn trên mặt nước không
            if (!isNearWaterSurface(level, current)) {
                break;
            }
        }
        
        return path;
    }

    private List<BlockPos> generateBranch(WorldGenLevel level, BlockPos start, RandomSource random) {
        List<BlockPos> branch = new ArrayList<>();
        BlockPos current = start;
        
        // Nhánh DÀI HƠN NHIỀU: 8-16 blocks
        int branchLength = 8 + random.nextInt(9);
        Direction direction = Direction.Plane.HORIZONTAL.getRandomDirection(random);
        
        for (int i = 0; i < branchLength; i++) {
            branch.add(current);
            
            // Nhánh uốn lượn
            if (random.nextFloat() < 0.35f) {
                direction = random.nextBoolean() ? 
                    direction.getClockWise() : 
                    direction.getCounterClockWise();
            }
            
            current = current.relative(direction);
            
            // Đôi khi nhảy qua 1 ô để tạo khoảng cách
            if (random.nextFloat() < 0.15f) {
                current = current.relative(direction);
            }
            
            if (!isNearWaterSurface(level, current)) {
                break;
            }
        }
        
        return branch;
    }

    private void placeBlock(WorldGenLevel level, BlockPos pos, RandomSource random, Set<BlockPos> placed) {
        if (placed.contains(pos)) {
            return;
        }
        
        BlockState blockToPlace = selectBlock(random);
        level.setBlock(pos, blockToPlace, 2);
        placed.add(pos);
        
        // 30% cơ hội đặt thêm 1-2 block xung quanh
        if (random.nextFloat() < 0.3f) {
            int neighbors = 1 + random.nextInt(2);
            for (int i = 0; i < neighbors; i++) {
                Direction dir = Direction.Plane.HORIZONTAL.getRandomDirection(random);
                BlockPos neighborPos = pos.relative(dir);
                
                if (canPlaceOnWaterSurface(level, neighborPos) && !placed.contains(neighborPos)) {
                    BlockState neighborBlock = selectBlock(random);
                    level.setBlock(neighborPos, neighborBlock, 2);
                    placed.add(neighborPos);
                }
            }
        }
    }

    private BlockState selectBlock(RandomSource random) {
        float chance = random.nextFloat();
        
        // 60% lá xanh, 25% hoa tím, 15% hoa vàng
        if (chance < 0.60f) {
            return VietnamDelightModBlocks.WATER_PRIMROSE.get().defaultBlockState();
        } else if (chance < 0.85f) {
            return VietnamDelightModBlocks.WATER_PRIMROSE_FLOWER.get().defaultBlockState();
        } else {
            return VietnamDelightModBlocks.WATER_PRIMROSE_YELLOW_FLOWER.get().defaultBlockState();
        }
    }

    private boolean canPlaceOnWaterSurface(WorldGenLevel level, BlockPos pos) {
        BlockState blockAt = level.getBlockState(pos);
        BlockState blockBelow = level.getBlockState(pos.below());
        
        // Vị trí đặt phải là air hoặc có thể thay thế
        boolean positionClear = blockAt.isAir() || blockAt.canBeReplaced();
        
        // Block bên dưới phải là nước (source hoặc flowing)
        boolean waterBelow = blockBelow.getFluidState().is(FluidTags.WATER) || 
                            blockBelow.is(Blocks.WATER);
        
        return positionClear && waterBelow;
    }

    private boolean isNearWaterSurface(WorldGenLevel level, BlockPos pos) {
        // Kiểm tra xem gần mặt nước không (trong vòng 2 blocks lên/xuống)
        for (int yOffset = -2; yOffset <= 2; yOffset++) {
            BlockPos checkPos = pos.offset(0, yOffset, 0);
            if (canPlaceOnWaterSurface(level, checkPos)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasLotusFlowerNearby(WorldGenLevel level, BlockPos origin) {
        // Tìm Wild Lotus Flowers trong vòng 32 blocks
        int searchRadius = 32;
        
        for (int x = -searchRadius; x <= searchRadius; x += 4) {
            for (int z = -searchRadius; z <= searchRadius; z += 4) {
                for (int y = -8; y <= 8; y++) {
                    BlockPos checkPos = origin.offset(x, y, z);
                    BlockState state = level.getBlockState(checkPos);
                    
                    // Kiểm tra xem có phải là Wild Lotus Flowers không
                    if (isLotusFlower(state)) {
                        return true;
                    }
                }
            }
        }
        
        return false;
    }

    private boolean isLotusFlower(BlockState state) {
        // Kiểm tra block có phải là lotus flower không
        String blockName = state.getBlock().getDescriptionId();
        return blockName.contains("wild_white_lotus_flower") ||
               blockName.contains("wild_purple_lotus_flower") ||
               blockName.contains("wild_lotus_flower");
    }

    private void generateCircularEnd(WorldGenLevel level, BlockPos center, RandomSource random, Set<BlockPos> placed) {
        // Tạo vòng tròn với bán kính 2-3 blocks
        int radius = 2 + random.nextInt(2);
        
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                // Kiểm tra khoảng cách để tạo hình tròn
                double distance = Math.sqrt(x * x + z * z);
                
                if (distance <= radius && distance >= radius - 1.5) {
                    BlockPos pos = center.offset(x, 0, z);
                    
                    if (canPlaceOnWaterSurface(level, pos) && !placed.contains(pos)) {
                        // 50% Duckweed, 50% Water Lettuce
                        BlockState blockToPlace = random.nextBoolean() ?
                            VietnamDelightModBlocks.DUCKWEED.get().defaultBlockState() :
                            VietnamDelightModBlocks.WATER_LETTUCE.get().defaultBlockState();
                        
                        level.setBlock(pos, blockToPlace, 2);
                        placed.add(pos);
                    }
                }
            }
        }
    }
}