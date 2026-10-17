package net.huwng.vietnamdelight.client.renderer;

import net.huwng.vietnamdelight.block.entity.BloomingGoldenFlamboyantLeavesBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class BloomingGoldenFlamboyantLeavesRenderer extends AbstractGoldenFlamboyantLeavesRenderer<BloomingGoldenFlamboyantLeavesBlockEntity> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("vietnam_delight", "textures/block/blooming_golden_flamboyant_leaves.png");

    public BloomingGoldenFlamboyantLeavesRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    protected ResourceLocation texture() {
        return TEXTURE;
    }
}
