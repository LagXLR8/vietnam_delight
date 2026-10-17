package net.huwng.vietnamdelight.world.feature;

import com.mojang.serialization.Codec;
import net.huwng.vietnamdelight.init.VietnamDelightModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import java.util.ArrayList;
import java.util.List;

public class WaterHyacinthFieldFeature extends Feature<NoneFeatureConfiguration> {
    
    private enum PatchType {
        SPARSE,      // Thưa thớt - chủ yếu lá
        NORMAL,      // Bình thường - lá + thân
        FLOWERING,   // Nhiều hoa - thân hoa + hoa
        DENSE        // Dày đặc - tất cả loại
    }
    
    public WaterHyacinthFieldFeature() {
        super(NoneFeatureConfiguration.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource random = context.random();

        // Kiểm tra có lotus flower gần đó không
        if (!hasLotusFlowerNearby(level, origin)) {
            return false;
        }

        // Tìm mặt nước (dùng logic đơn giản hơn như WaterPrimrose)
        BlockPos waterSurface = findWaterSurface(level, origin, random);
        if (waterSurface == null) {
            return false;
        }

        // Tạo một patch bèo tây với kích thước ngẫu nhiên lớn hơn
        int mainRadius = 6 + random.nextInt(11); // Bán kính từ 6-16 blocks
        int placed = 0;

        // Tạo nhiều sub-patches với loại khác nhau
        List<SubPatch> subPatches = generateSubPatches(waterSurface, mainRadius, random);

        for (int x = -mainRadius; x <= mainRadius; x++) {
            for (int z = -mainRadius; z <= mainRadius; z++) {
                BlockPos targetPos = waterSurface.offset(x, 0, z);
                
                // Kiểm tra vị trí có hợp lệ không (dùng logic đơn giản như WaterPrimrose)
                if (!canPlaceOnWaterSurface(level, targetPos)) {
                    continue;
                }

                // Tìm sub-patch gần nhất để xác định loại
                SubPatch nearestPatch = findNearestSubPatch(targetPos, subPatches);
                if (nearestPatch == null) {
                    continue;
                }

                // Tính khoảng cách đến center của sub-patch
                double distanceToCenter = Math.sqrt(
                    Math.pow(targetPos.getX() - nearestPatch.center.getX(), 2) +
                    Math.pow(targetPos.getZ() - nearestPatch.center.getZ(), 2)
                );

                // Chỉ đặt trong bán kính của sub-patch
                if (distanceToCenter > nearestPatch.radius) {
                    continue;
                }

                // Tính mật độ dựa trên khoảng cách (dày hơn ở giữa)
                double densityFactor = 1.0 - (distanceToCenter / nearestPatch.radius);
                double densityThreshold = nearestPatch.type == PatchType.DENSE ? 0.85 : 
                                         nearestPatch.type == PatchType.FLOWERING ? 0.75 :
                                         nearestPatch.type == PatchType.NORMAL ? 0.65 : 0.5;

                if (random.nextDouble() > densityFactor * densityThreshold) {
                    continue;
                }

                // Chọn loại block dựa trên loại patch
                BlockState blockToPlace = getBlockForPatchType(nearestPatch.type, random);
                
                if (blockToPlace != null && level.setBlock(targetPos, blockToPlace, 2)) {
                    placed++;
                }
            }
        }

        return placed > 0;
    }

    private BlockPos findWaterSurface(WorldGenLevel level, BlockPos origin, RandomSource random) {
        // Dùng logic giống WaterPrimrose - đơn giản và hiệu quả hơn
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

    private boolean canPlaceOnWaterSurface(WorldGenLevel level, BlockPos pos) {
        // Logic đơn giản giống WaterPrimrose
        BlockState blockAt = level.getBlockState(pos);
        BlockState blockBelow = level.getBlockState(pos.below());
        
        // Vị trí đặt phải là air hoặc có thể thay thế
        boolean positionClear = blockAt.isAir() || blockAt.canBeReplaced();
        
        // Block bên dưới phải là nước (source hoặc flowing)
        boolean waterBelow = blockBelow.getFluidState().is(FluidTags.WATER) || 
                            blockBelow.is(Blocks.WATER);
        
        return positionClear && waterBelow;
    }

    private List<SubPatch> generateSubPatches(BlockPos center, int mainRadius, RandomSource random) {
        List<SubPatch> patches = new ArrayList<>();
        
        // Tạo 4-7 sub-patches
        int patchCount = 4 + random.nextInt(6);
        
        for (int i = 0; i < patchCount; i++) {
            // Vị trí ngẫu nhiên trong main radius
            double angle = random.nextDouble() * 2 * Math.PI;
            double distance = random.nextDouble() * mainRadius * 0.7;
            
            int offsetX = (int)(Math.cos(angle) * distance);
            int offsetZ = (int)(Math.sin(angle) * distance);
            
            BlockPos patchCenter = center.offset(offsetX, 0, offsetZ);
            int patchRadius = 3 + random.nextInt(5); // 3-7 blocks (tăng từ 2-5)
            
            // Chọn loại patch ngẫu nhiên với trọng số
            PatchType type;
            int roll = random.nextInt(100);
            if (roll < 20) {
                type = PatchType.SPARSE;
            } else if (roll < 50) {
                type = PatchType.NORMAL;
            } else if (roll < 75) {
                type = PatchType.FLOWERING;
            } else {
                type = PatchType.DENSE;
            }
            
            patches.add(new SubPatch(patchCenter, patchRadius, type));
        }
        
        return patches;
    }

    private SubPatch findNearestSubPatch(BlockPos pos, List<SubPatch> patches) {
        SubPatch nearest = null;
        double minDistance = Double.MAX_VALUE;
        
        for (SubPatch patch : patches) {
            double distance = Math.sqrt(
                Math.pow(pos.getX() - patch.center.getX(), 2) +
                Math.pow(pos.getZ() - patch.center.getZ(), 2)
            );
            
            // Chỉ xem xét nếu trong phạm vi
            if (distance <= patch.radius && distance < minDistance) {
                minDistance = distance;
                nearest = patch;
            }
        }
        
        return nearest;
    }

    private BlockState getBlockForPatchType(PatchType type, RandomSource random) {
        int choice = random.nextInt(100);
        
        switch (type) {
            case SPARSE:
                // 70% lá, 25% thân, 5% hoa thân
                if (choice < 70) {
                    return VietnamDelightModBlocks.WATER_HYACINTH.get().defaultBlockState();
                } else if (choice < 95) {
                    return VietnamDelightModBlocks.WATER_HYACINTH_STEM.get().defaultBlockState();
                } else {
                    return VietnamDelightModBlocks.WATER_HYACINTH_FLOWER_STEM.get().defaultBlockState();
                }
                
            case NORMAL:
                // 40% lá, 40% thân, 15% hoa thân, 5% hoa
                if (choice < 40) {
                    return VietnamDelightModBlocks.WATER_HYACINTH.get().defaultBlockState();
                } else if (choice < 80) {
                    return VietnamDelightModBlocks.WATER_HYACINTH_STEM.get().defaultBlockState();
                } else if (choice < 95) {
                    return VietnamDelightModBlocks.WATER_HYACINTH_FLOWER_STEM.get().defaultBlockState();
                } else {
                    return VietnamDelightModBlocks.WATER_HYACINTH_FLOWER.get().defaultBlockState();
                }
                
            case FLOWERING:
                // 15% lá, 25% thân, 35% hoa thân, 25% hoa
                if (choice < 15) {
                    return VietnamDelightModBlocks.WATER_HYACINTH.get().defaultBlockState();
                } else if (choice < 40) {
                    return VietnamDelightModBlocks.WATER_HYACINTH_STEM.get().defaultBlockState();
                } else if (choice < 75) {
                    return VietnamDelightModBlocks.WATER_HYACINTH_FLOWER_STEM.get().defaultBlockState();
                } else {
                    return VietnamDelightModBlocks.WATER_HYACINTH_FLOWER.get().defaultBlockState();
                }
                
            case DENSE:
                // 25% lá, 30% thân, 30% hoa thân, 15% hoa
                if (choice < 25) {
                    return VietnamDelightModBlocks.WATER_HYACINTH.get().defaultBlockState();
                } else if (choice < 55) {
                    return VietnamDelightModBlocks.WATER_HYACINTH_STEM.get().defaultBlockState();
                } else if (choice < 85) {
                    return VietnamDelightModBlocks.WATER_HYACINTH_FLOWER_STEM.get().defaultBlockState();
                } else {
                    return VietnamDelightModBlocks.WATER_HYACINTH_FLOWER.get().defaultBlockState();
                }
        }
        
        return VietnamDelightModBlocks.WATER_HYACINTH.get().defaultBlockState();
    }

    private boolean hasLotusFlowerNearby(WorldGenLevel level, BlockPos origin) {
        // Tìm Wild Lotus Flowers trong vòng 32 blocks
        int searchRadius = 64;
        
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
               blockName.contains("duckweed") ||
               blockName.contains("wild_lotus_flower");
    }

    private static class SubPatch {
        final BlockPos center;
        final int radius;
        final PatchType type;
        
        SubPatch(BlockPos center, int radius, PatchType type) {
            this.center = center;
            this.radius = radius;
            this.type = type;
        }
    }
}