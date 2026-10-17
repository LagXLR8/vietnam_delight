package net.huwng.vietnamdelight.world.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class BougainvilleaTreeFeature extends Feature<NoneFeatureConfiguration> {
    public BougainvilleaTreeFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    public BougainvilleaTreeFeature() {
        this(NoneFeatureConfiguration.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource random = context.random();

        // Kiểm tra khối đỡ bên dưới: phải là đất hoặc cát
        BlockPos below = origin.below();
        BlockState belowState = level.getBlockState(below);
        if (!belowState.is(BlockTags.DIRT) && !belowState.is(BlockTags.SAND)) {
            return false;
        }

        // Vị trí mọc phải thoáng (không khí hoặc có thể thay thế)
        BlockState originState = level.getBlockState(origin);
        if (!originState.isAir() && !originState.canBeReplaced()) {
            return false;
        }

        return BougainvilleaTreeGrower.grow(level, origin, random);
    }
}
