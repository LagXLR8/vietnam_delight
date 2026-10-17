package net.huwng.vietnamdelight.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.huwng.vietnamdelight.client.bloom.BloomRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix4f;

/**
 * Same technique as GlowBlockEntityRenderer in the BloomTest POC: since the
 * block's model is RenderShape.INVISIBLE, we draw the ENTIRE visible cube
 * ourselves --
 *  1. Normal appearance -- raw textured draw using the leaf block's own PNG.
 *  2. Bloom source -- the SAME textured cube drawn again into
 *     BloomRenderer's offscreen bloom target, so the glow halo matches the
 *     leaves' own painted colors.
 *
 * isFullyEnclosed() adds occlusion culling on top of what the POC had --
 * leaves commonly form large dense canopies (unlike the isolated test
 * block), so skipping fully-hidden instances matters a lot more here. A
 * neighbor counts as "occluding" if it's a normal opaque full-cube block,
 * OR any LeavesBlock -- vanilla leaves (and these) have noOcclusion() set
 * (needed to see through natural gaps), so canOcclude() alone would never
 * treat a leaf-surrounded-by-leaves cluster as hidden.
 */
public abstract class AbstractGoldenFlamboyantLeavesRenderer<T extends BlockEntity> implements BlockEntityRenderer<T> {

    private static final float INSET = 0.02f;

    protected abstract ResourceLocation texture();

    @Override
    public int getViewDistance() {
        return 172;
    }

    @Override
    public void render(T blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        if (isFullyEnclosed(blockEntity)) {
            return;
        }

        Matrix4f pose = poseStack.last().pose();
        ResourceLocation tex = texture();

        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, tex);
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        drawCube(pose, 255);

        BloomRenderer.queueBloomDraw(BloomRenderer.Channel.WIDE, () -> {
            RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
            RenderSystem.setShaderTexture(0, tex);
            drawCube(pose, 150); // translucent -- glow blends with the leaves rather than a hard duplicate
        });
    }

    private static void drawCube(Matrix4f pose, int alpha) {
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

        float min = INSET;
        float max = 1.0f - INSET;
        float[][] uv = {{0, 0}, {1, 0}, {1, 1}, {0, 1}};
        for (int face = 0; face < 6; face++) {
            float[][] quad = cubeFace(face, min, max);
            for (int i = 0; i < 4; i++) {
                float[] v = quad[i];
                buffer.vertex(pose, v[0], v[1], v[2])
                        .uv(uv[i][0], uv[i][1])
                        .color(255, 255, 255, alpha)
                        .endVertex();
            }
        }

        com.mojang.blaze3d.vertex.BufferUploader.drawWithShader(buffer.end());
    }

    private static float[][] cubeFace(int face, float min, float max) {
        return switch (face) {
            case 0 -> new float[][]{{min, min, min}, {max, min, min}, {max, min, max}, {min, min, max}}; // down
            case 1 -> new float[][]{{min, max, min}, {min, max, max}, {max, max, max}, {max, max, min}}; // up
            case 2 -> new float[][]{{min, min, min}, {min, max, min}, {max, max, min}, {max, min, min}}; // north
            case 3 -> new float[][]{{max, min, max}, {max, max, max}, {min, max, max}, {min, min, max}}; // south
            case 4 -> new float[][]{{min, min, max}, {min, max, max}, {min, max, min}, {min, min, min}}; // west
            default -> new float[][]{{max, min, min}, {max, max, min}, {max, max, max}, {max, min, max}}; // east
        };
    }

    private static boolean isFullyEnclosed(BlockEntity blockEntity) {
        Level level = blockEntity.getLevel();
        if (level == null) {
            return false;
        }
        BlockPos pos = blockEntity.getBlockPos();

        for (Direction dir : Direction.values()) {
            BlockPos neighborPos = pos.relative(dir);
            BlockState neighborState = level.getBlockState(neighborPos);

            boolean occludes = neighborState.getBlock() instanceof LeavesBlock
                    || (neighborState.canOcclude() && Block.isShapeFullBlock(neighborState.getShape(level, neighborPos)));

            if (!occludes) {
                return false;
            }
        }
        return true;
    }
}
