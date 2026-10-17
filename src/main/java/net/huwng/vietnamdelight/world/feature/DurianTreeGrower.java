package net.huwng.vietnamdelight.world.feature;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import org.jetbrains.annotations.Nullable;

public class DurianTreeGrower extends AbstractTreeGrower {

    @Nullable
    @Override
    protected ResourceKey<ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource random, boolean hasFlowers) {
        // Trồng 1 chồi đơn lẻ: 40% cơ hội mọc cây sầu riêng đại thụ siêu cao, 60% mọc cây sầu riêng tiêu chuẩn
        if (random.nextFloat() < 0.40f) {
            return ResourceKey.create(
                Registries.CONFIGURED_FEATURE,
                new ResourceLocation("vietnam_delight", "tall_durian_tree")
            );
        }
        return ResourceKey.create(
            Registries.CONFIGURED_FEATURE,
            new ResourceLocation("vietnam_delight", "durian_tree")
        );
    }
}
