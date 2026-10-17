package net.huwng.vietnamdelight.world.feature;

import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import org.jetbrains.annotations.Nullable;

public class FlamboyantTreeGrower extends AbstractTreeGrower {

    @Nullable
    @Override
    protected ResourceKey<ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource random, boolean hasFlowers) {
        return ResourceKey.create(
            net.minecraft.core.registries.Registries.CONFIGURED_FEATURE,
            new net.minecraft.resources.ResourceLocation("vietnam_delight", "flamboyant_tree")
        );
    }
}
