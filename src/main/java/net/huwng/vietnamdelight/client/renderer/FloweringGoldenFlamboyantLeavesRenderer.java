package net.huwng.vietnamdelight.client.renderer;

import net.huwng.vietnamdelight.block.entity.FloweringGoldenFlamboyantLeavesBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class FloweringGoldenFlamboyantLeavesRenderer extends AbstractGoldenFlamboyantLeavesRenderer<FloweringGoldenFlamboyantLeavesBlockEntity> {

    // NOTE: filename is "golden_flowering_..." (words swapped vs the block's
    // own registry name "flowering_golden_...") -- this matches the actual
    // PNG that already ships in the project, confirmed from the original
    // block model json. Don't "fix" the swap without renaming the file too.
    private static final ResourceLocation TEXTURE = new ResourceLocation("vietnam_delight", "textures/block/golden_flowering_flamboyant_leaves.png");

    public FloweringGoldenFlamboyantLeavesRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    protected ResourceLocation texture() {
        return TEXTURE;
    }
}
