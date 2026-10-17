package net.huwng.vietnamdelight.client.bloom;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Selective bloom pipeline shared by all "golden flamboyant" glow sources.
 *
 * TWO INDEPENDENT CHANNELS, each with its own blur radius/strength:
 *  - WIDE: for large-area sources (the leaves' inset cube). Can use a big
 *    blur radius for a soft wide spread without issue, because the source
 *    covers enough screen pixels that a wide box blur doesn't dilute it
 *    away to nothing.
 *  - TIGHT: for small/thin sources (petal, flower, sapling cross, the
 *    particle). A single flat quad only covers a handful of screen pixels;
 *    running it through the SAME wide blur as the leaves averaged it down
 *    to a near-invisible smear (this is why "make leaves less tight" via a
 *    bigger shared radius killed the small sources' glow entirely -- see
 *    chat history). Keep this one's radius small so the source's actual
 *    color survives the average.
 *
 * Both channels share the same frame flow (see FlamboyantBloomRenderEvents
 * for the hook wiring):
 *  1. clearAndPrepare() - AFTER_SOLID_BLOCKS. Clears both channels' source
 *     targets and copies terrain depth into them.
 *  2. Glow sources call queueBloomDraw(Channel, Runnable) during their own
 *     render to REGISTER a draw command in the right channel.
 *  3. flushBloomQueue(pose) - AFTER_PARTICLES. Binds each channel's source
 *     target once, runs its queued draws, unbinds. `pose` is the current
 *     level PoseStack's matrix -- queued draws without their own baked
 *     matrix (particles) read it back via getFlushPose().
 *  4. compositeToMain() - blurs each channel with ITS OWN radius/strength
 *     and additively composites both onto the main render target, each
 *     with its own depth-occlusion test.
 */
public final class BloomRenderer {

    public enum Channel { WIDE, TIGHT }

    // Populated by RegisterShadersEvent in VietnamDelightModShaders.
    public static ShaderInstance blurShader;
    public static ShaderInstance compositeShader;

    private static final class ChannelState {
        RenderTarget source;
        RenderTarget blurA;
        RenderTarget blurB;
        final List<Runnable> queue = new ArrayList<>();
        float blurRadius;
        float compositeStrength;

        ChannelState(float blurRadius, float compositeStrength) {
            this.blurRadius = blurRadius;
            this.compositeStrength = compositeStrength;
        }
    }

    // Tune these per channel. WIDE can go big (e.g. 15-25) for a soft
    // spread on the leaves; TIGHT should stay small (2-4) or thin sources
    // vanish -- see class javadoc.
    private static final Map<Channel, ChannelState> CHANNELS = new EnumMap<>(Map.of(
            Channel.WIDE, new ChannelState(14.0f, 0.9f),
            Channel.TIGHT, new ChannelState(2.5f, 1.3f)
    ));

    private static int width = -1;
    private static int height = -1;
    private static Matrix4f flushPose;

    private BloomRenderer() {
    }

    private static void ensureTargets() {
        Minecraft mc = Minecraft.getInstance();
        RenderTarget main = mc.getMainRenderTarget();
        if (width == main.width && height == main.height && CHANNELS.get(Channel.WIDE).source != null) {
            return;
        }

        close();

        width = main.width;
        height = main.height;

        for (ChannelState channel : CHANNELS.values()) {
            channel.source = new RenderTarget(true) {
            };
            channel.source.resize(width, height, Minecraft.ON_OSX);
            channel.source.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);

            channel.blurA = new RenderTarget(false) {
            };
            channel.blurA.resize(width, height, Minecraft.ON_OSX);
            channel.blurA.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);

            channel.blurB = new RenderTarget(false) {
            };
            channel.blurB.resize(width, height, Minecraft.ON_OSX);
            channel.blurB.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
        }
    }

    public static void close() {
        for (ChannelState channel : CHANNELS.values()) {
            channel.queue.clear();
            if (channel.source != null) {
                channel.source.destroyBuffers();
                channel.source = null;
            }
            if (channel.blurA != null) {
                channel.blurA.destroyBuffers();
                channel.blurA = null;
            }
            if (channel.blurB != null) {
                channel.blurB.destroyBuffers();
                channel.blurB = null;
            }
        }
        width = -1;
        height = -1;
    }

    /** Call at RenderLevelStageEvent.Stage.AFTER_SOLID_BLOCKS. */
    public static void clearAndPrepare() {
        ensureTargets();
        Minecraft mc = Minecraft.getInstance();

        for (ChannelState channel : CHANNELS.values()) {
            channel.source.clear(Minecraft.ON_OSX);
            channel.source.copyDepthFrom(mc.getMainRenderTarget());
        }

        mc.getMainRenderTarget().bindWrite(false);
    }

    /**
     * Register a raw draw call to run into the given channel's bloom
     * source target LATER this frame. Cheap -- just adds to a list.
     * If your draw doesn't already have a baked camera-relative matrix
     * (e.g. inside Particle.render(), which gets no PoseStack), read
     * BloomRenderer.getFlushPose() inside the Runnable instead.
     */
    public static void queueBloomDraw(Channel channel, Runnable drawAction) {
        ChannelState state = CHANNELS.get(channel);
        if (state.source == null) {
            return;
        }
        state.queue.add(drawAction);
    }

    /** Valid only while flushBloomQueue() is running its queued draws. */
    public static Matrix4f getFlushPose() {
        return flushPose;
    }

    /**
     * Call ONCE per frame at RenderLevelStageEvent.Stage.AFTER_PARTICLES,
     * after all glow sources have queued their draws, and BEFORE
     * compositeToMain(). `pose` should be event.getPoseStack().last().pose().
     */
    public static void flushBloomQueue(Matrix4f pose) {
        flushPose = pose;
        Minecraft mc = Minecraft.getInstance();

        for (ChannelState channel : CHANNELS.values()) {
            if (channel.source == null || channel.queue.isEmpty()) {
                channel.queue.clear();
                continue;
            }

            channel.source.bindWrite(false);

            RenderSystem.enableDepthTest();
            RenderSystem.depthFunc(org.lwjgl.opengl.GL11.GL_LEQUAL);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();

            for (Runnable drawAction : channel.queue) {
                drawAction.run();
            }
            channel.queue.clear();

            RenderSystem.disableBlend();
            mc.getMainRenderTarget().bindWrite(false);
        }
    }

    /** Call at RenderLevelStageEvent.Stage.AFTER_PARTICLES, after flushBloomQueue(). */
    public static void compositeToMain() {
        if (blurShader == null || compositeShader == null) {
            return;
        }

        for (ChannelState channel : CHANNELS.values()) {
            if (channel.source == null) {
                continue;
            }
            compositeChannel(channel);
        }
    }

    private static void compositeChannel(ChannelState channel) {
        Matrix4f proj = RenderSystem.getProjectionMatrix();
        float projA = proj.m22();
        float projB = proj.m32();

        // Both passes reference channel.source's ORIGINAL depth (not
        // blurA/blurB's, which don't have one) so H and V agree on where
        // the real depth edges are.
        blitFullscreen(channel.source, channel.blurA, blurShader, shader -> {
            shader.safeGetUniform("BlurDir").set(1.0f, 0.0f);
            shader.safeGetUniform("Radius").set(channel.blurRadius);
            shader.safeGetUniform("ProjA").set(projA);
            shader.safeGetUniform("ProjB").set(projB);
            RenderSystem.setShaderTexture(1, channel.source.getDepthTextureId());
        });

        blitFullscreen(channel.blurA, channel.blurB, blurShader, shader -> {
            shader.safeGetUniform("BlurDir").set(0.0f, 1.0f);
            shader.safeGetUniform("Radius").set(channel.blurRadius);
            shader.safeGetUniform("ProjA").set(projA);
            shader.safeGetUniform("ProjB").set(projB);
            RenderSystem.setShaderTexture(1, channel.source.getDepthTextureId());
        });

        Minecraft mc = Minecraft.getInstance();
        RenderTarget main = mc.getMainRenderTarget();
        main.bindWrite(false);

        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(
                com.mojang.blaze3d.platform.GlStateManager.SourceFactor.ONE,
                com.mojang.blaze3d.platform.GlStateManager.DestFactor.ONE
        );

        // NOTE: sampling main's own depth attachment while main is bound
        // for writing (color only, depth test/write is off here). Most
        // drivers tolerate this read-only feedback for a single
        // full-screen pass -- if bloom looks corrupted/black on a specific
        // GPU, copy main's depth into a separate texture first instead.
        RenderSystem.setShaderTexture(0, channel.blurB.getColorTextureId());
        RenderSystem.setShaderTexture(1, channel.source.getDepthTextureId());
        RenderSystem.setShaderTexture(2, main.getDepthTextureId());
        RenderSystem.setShader(() -> compositeShader);
        compositeShader.safeGetUniform("ScreenSize").set((float) width, (float) height);
        compositeShader.safeGetUniform("Strength").set(channel.compositeStrength);

        // Same projection matrix the scene was just rendered with this
        // frame -- m22/m32 are exactly what's needed to turn a raw
        // depth-buffer sample back into a view-space distance in blocks.
        // See bloom_composite.fsh#linearDepth for the derivation.
        compositeShader.safeGetUniform("ProjA").set(projA);
        compositeShader.safeGetUniform("ProjB").set(projB);

        drawFullscreenQuad(compositeShader);

        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        RenderSystem.enableDepthTest();
    }

    private static void blitFullscreen(RenderTarget input, RenderTarget output, ShaderInstance shader, Consumer<ShaderInstance> extraUniforms) {
        output.bindWrite(true);
        RenderSystem.disableDepthTest();
        RenderSystem.disableBlend();

        RenderSystem.setShaderTexture(0, input.getColorTextureId());
        RenderSystem.setShader(() -> shader);
        shader.safeGetUniform("ScreenSize").set((float) output.width, (float) output.height);
        extraUniforms.accept(shader);

        drawFullscreenQuad(shader);

        Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
    }

    private static void drawFullscreenQuad(ShaderInstance shader) {
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
        buffer.vertex(-1.0, -1.0, 0.0).endVertex();
        buffer.vertex(1.0, -1.0, 0.0).endVertex();
        buffer.vertex(1.0, 1.0, 0.0).endVertex();
        buffer.vertex(-1.0, 1.0, 0.0).endVertex();

        shader.apply();
        com.mojang.blaze3d.vertex.BufferUploader.drawWithShader(buffer.end());
        shader.clear();
    }
}
