package net.huwng.vietnamdelight.client.bloom;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.huwng.vietnamdelight.client.bloom.FlamboyantGlowScanner.GlowShape;
import net.huwng.vietnamdelight.client.bloom.FlamboyantGlowScanner.GlowSource;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix4f;

import java.util.List;

/**
 * Wires FlamboyantGlowScanner + BloomRenderer into the actual frame/tick
 * events. Same RenderLevelStageEvent stages already used successfully by
 * RopeRenderEvents in this project.
 *
 * PETALS/FLOWER/SAPLING draw with each block's REAL texture, as a SINGLE
 * flat quad matching the one face that actually carries the artwork (the
 * model's other faces are just 1px edge strips of the same texture, not
 * worth drawing separately):
 *  - PETALS: single quad on the "up" face (y = 1/16, matching the model's
 *    up/down UV which is the only meaningful face).
 *  - FLOWER: single quad on the plane the block is actually attached to
 *    (derived from FACING, matching GoldenFlamboyantFlowerBlock#getShape's
 *    thin axis).
 *  - SAPLING: 2 diagonal quads (cross model), now spanning the FULL block
 *    height (0..1) instead of the collision box height (0..0.75) -- plant
 *    textures are typically taller than their hitbox, using the hitbox
 *    for the visual was the "wrong scale" bug.
 *
 * All three now draw at full alpha (255) -- a thin flat quad covers much
 * less screen area than the leaves' full inset cube, so the SAME alpha
 * looked dimmer after blur (box blur averages over mostly-empty
 * surrounding pixels for a thin shape); pushing to full alpha is the
 * lever we have to even that out without a per-shape blur/strength split.
 *
 * These 3 blocks keep their normal baked chunk-mesh model as-is (unlike the
 * leaves, which became BlockEntity-driven) -- we only ADD a bloom-only
 * draw here, we don't touch their normal appearance at all.
 */
@Mod.EventBusSubscriber(modid = "vietnam_delight", value = Dist.CLIENT)
public class FlamboyantBloomRenderEvents {

    private static final ResourceLocation PETALS_TEXTURE = new ResourceLocation("vietnam_delight", "textures/block/golden_flamboyant_petals.png");
    private static final ResourceLocation FLOWER_TEXTURE = new ResourceLocation("vietnam_delight", "textures/block/golden_flamboyant_flower.png");
    private static final ResourceLocation SAPLING_TEXTURE = new ResourceLocation("vietnam_delight", "textures/block/golden_flamboyant_sapling_glow.png");

    private static final int FULL_ALPHA = 255;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        FlamboyantGlowScanner.onClientTick();
    }

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_SOLID_BLOCKS) {
            BloomRenderer.clearAndPrepare();
        } else if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            Matrix4f pose = event.getPoseStack().last().pose();
            net.huwng.vietnamdelight.client.particle.GoldenFlamboyantPetalParticle.renderAllManually(pose, event.getPartialTick());
            queueScannedBlockGlow();
            BloomRenderer.flushBloomQueue(pose);
            BloomRenderer.compositeToMain();
        }
    }

    private static void queueScannedBlockGlow() {
        List<GlowSource> sources = FlamboyantGlowScanner.getCachedSources();
        if (sources.isEmpty()) {
            return;
        }

        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        double camX = camera.getPosition().x;
        double camY = camera.getPosition().y;
        double camZ = camera.getPosition().z;

        for (GlowSource source : sources) {
            BlockPos pos = source.pos();
            float relX = (float) (pos.getX() - camX);
            float relY = (float) (pos.getY() - camY);
            float relZ = (float) (pos.getZ() - camZ);
            GlowShape shape = source.shape();
            Direction facing = source.facing();

            BloomRenderer.queueBloomDraw(BloomRenderer.Channel.TIGHT, () -> {
                Matrix4f pose = BloomRenderer.getFlushPose();
                switch (shape) {
                    case PETALS -> drawPetals(pose, relX, relY, relZ);
                    case FLOWER -> drawFlower(pose, relX, relY, relZ, facing);
                    case SAPLING -> drawSaplingCross(pose, relX, relY, relZ);
                }
            });
        }
    }

    // --- PETALS: single quad on the "up" face only (y = 1/16). ---
    private static void drawPetals(Matrix4f pose, float ox, float oy, float oz) {
        beginTextured(PETALS_TEXTURE);
        float y = 1f / 16f;
        drawQuadXZ(pose, ox, oy, oz, 0f, y, 0f, 1f, 1f, FULL_ALPHA);
        endTextured();
    }

    // --- FLOWER: single quad on whichever plane the block is actually
    //     attached to (derived from the same box GoldenFlamboyantFlowerBlock
    //     #getShape uses, just collapsed to its thin axis's midpoint). ---
    private static void drawFlower(Matrix4f pose, float ox, float oy, float oz, Direction facing) {
        if (facing == null) {
            facing = Direction.SOUTH;
        }
        float[] box = switch (facing) {
            case NORTH -> new float[]{0, 0, 15, 16, 16, 16};
            case EAST -> new float[]{0, 0, 0, 1, 16, 16};
            case WEST -> new float[]{15, 0, 0, 16, 16, 16};
            case UP -> new float[]{0, 0, 0, 16, 1, 16};
            case DOWN -> new float[]{0, 15, 0, 16, 16, 16};
            default -> new float[]{0, 0, 0, 16, 16, 1}; // SOUTH
        };
        float minX = box[0] / 16f, minY = box[1] / 16f, minZ = box[2] / 16f;
        float maxX = box[3] / 16f, maxY = box[4] / 16f, maxZ = box[5] / 16f;

        beginTextured(FLOWER_TEXTURE);

        float dx = maxX - minX, dy = maxY - minY, dz = maxZ - minZ;
        if (dz <= dx && dz <= dy) {
            float midZ = (minZ + maxZ) / 2f;
            drawQuadXY(pose, ox, oy, oz, minX, minY, midZ, maxX, maxY, FULL_ALPHA);
        } else if (dx <= dy) {
            float midX = (minX + maxX) / 2f;
            drawQuadYZ(pose, ox, oy, oz, midX, minY, minZ, maxY, maxZ, FULL_ALPHA);
        } else {
            float midY = (minY + maxY) / 2f;
            drawQuadXZ(pose, ox, oy, oz, minX, midY, minZ, maxX, maxZ, FULL_ALPHA);
        }

        endTextured();
    }

    // --- SAPLING: cross model, full block height (0..1) -- plant textures
    //     are usually taller than the collision box, using the hitbox
    //     height (0.75) made this look visibly too short/wrong-scaled. ---
    private static void drawSaplingCross(Matrix4f pose, float ox, float oy, float oz) {
        beginTextured(SAPLING_TEXTURE);

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

        float min = 0.05f, max = 0.95f, minY = 0f, maxY = 1.0f;
        int a = FULL_ALPHA;

        // Diagonal 1: (min,min) -> (max,max)
        quadVertex(buffer, pose, ox, oy, oz, min, minY, min, 0, 1, a);
        quadVertex(buffer, pose, ox, oy, oz, max, minY, max, 1, 1, a);
        quadVertex(buffer, pose, ox, oy, oz, max, maxY, max, 1, 0, a);
        quadVertex(buffer, pose, ox, oy, oz, min, maxY, min, 0, 0, a);

        // Diagonal 2: (min,max) -> (max,min)
        quadVertex(buffer, pose, ox, oy, oz, min, minY, max, 0, 1, a);
        quadVertex(buffer, pose, ox, oy, oz, max, minY, min, 1, 1, a);
        quadVertex(buffer, pose, ox, oy, oz, max, maxY, min, 1, 0, a);
        quadVertex(buffer, pose, ox, oy, oz, min, maxY, max, 0, 0, a);

        com.mojang.blaze3d.vertex.BufferUploader.drawWithShader(buffer.end());
        endTextured();
    }

    private static void beginTextured(ResourceLocation texture) {
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.disableCull(); // flat/cross shapes need to be visible from both sides
    }

    private static void endTextured() {
        RenderSystem.enableCull();
    }

    /** Single quad lying flat in the XZ plane (fixed Y). */
    private static void drawQuadXZ(Matrix4f pose, float ox, float oy, float oz, float minX, float y, float minZ, float maxX, float maxZ, int alpha) {
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        quadVertex(buffer, pose, ox, oy, oz, minX, y, minZ, 0, 0, alpha);
        quadVertex(buffer, pose, ox, oy, oz, minX, y, maxZ, 0, 1, alpha);
        quadVertex(buffer, pose, ox, oy, oz, maxX, y, maxZ, 1, 1, alpha);
        quadVertex(buffer, pose, ox, oy, oz, maxX, y, minZ, 1, 0, alpha);
        com.mojang.blaze3d.vertex.BufferUploader.drawWithShader(buffer.end());
    }

    /** Single quad standing in the XY plane (fixed Z). */
    private static void drawQuadXY(Matrix4f pose, float ox, float oy, float oz, float minX, float minY, float z, float maxX, float maxY, int alpha) {
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        quadVertex(buffer, pose, ox, oy, oz, minX, minY, z, 0, 1, alpha);
        quadVertex(buffer, pose, ox, oy, oz, minX, maxY, z, 0, 0, alpha);
        quadVertex(buffer, pose, ox, oy, oz, maxX, maxY, z, 1, 0, alpha);
        quadVertex(buffer, pose, ox, oy, oz, maxX, minY, z, 1, 1, alpha);
        com.mojang.blaze3d.vertex.BufferUploader.drawWithShader(buffer.end());
    }

    /** Single quad standing in the YZ plane (fixed X). */
    private static void drawQuadYZ(Matrix4f pose, float ox, float oy, float oz, float x, float minY, float minZ, float maxY, float maxZ, int alpha) {
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        quadVertex(buffer, pose, ox, oy, oz, x, minY, minZ, 0, 1, alpha);
        quadVertex(buffer, pose, ox, oy, oz, x, maxY, minZ, 0, 0, alpha);
        quadVertex(buffer, pose, ox, oy, oz, x, maxY, maxZ, 1, 0, alpha);
        quadVertex(buffer, pose, ox, oy, oz, x, minY, maxZ, 1, 1, alpha);
        com.mojang.blaze3d.vertex.BufferUploader.drawWithShader(buffer.end());
    }

    private static void quadVertex(BufferBuilder buffer, Matrix4f pose, float ox, float oy, float oz, float x, float y, float z, float u, float v, int alpha) {
        buffer.vertex(pose, ox + x, oy + y, oz + z).uv(u, v).color(255, 255, 255, alpha).endVertex();
    }
}
