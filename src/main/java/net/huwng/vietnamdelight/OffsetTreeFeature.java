package net.huwng.vietnamdelight;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;

import java.util.Optional;

public class OffsetTreeFeature extends Feature<TreeConfiguration> {
    public OffsetTreeFeature() {
        super(TreeConfiguration.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<TreeConfiguration> context) {
        BlockPos origin = context.origin();
        BlockPos offsetPos = origin.offset(0, 0, 0); // lệch trái và sau

        return Feature.TREE.place(new FeaturePlaceContext<>(
            Optional.empty(),
            context.level(),
            context.chunkGenerator(),
            context.random(),
            offsetPos,
            context.config()
        ));
    }
}
