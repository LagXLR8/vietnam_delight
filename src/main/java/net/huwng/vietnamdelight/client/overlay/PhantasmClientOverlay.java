package net.huwng.vietnamdelight.client.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractCandleBlock;
import net.minecraft.world.level.block.state.BlockState;

import net.huwng.vietnamdelight.VietnamDelightMod;
import net.huwng.vietnamdelight.init.VietnamDelightModBlocks;
import net.huwng.vietnamdelight.init.VietnamDelightModMobEffects;

public class PhantasmClientOverlay {
    public static final ResourceLocation PHANTASM_SHADER = new ResourceLocation(VietnamDelightMod.MODID, "shaders/post/phantasm.json");

    private static int clientStandingTicks = 0;
    private static boolean cachedNearPhantasm = false;
    private static boolean isShaderActive = false;

    // Eye-blink transition state
    private static boolean lastHadPhantasm = false;
    private static int blinkTicks = 0;
    private static final int BLINK_DURATION = 10; // 10 ticks = 0.5s eye blink

    public static void clientTick() {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null || mc.level == null) {
            if (isShaderActive) {
                disableShader(mc);
            }
            clientStandingTicks = 0;
            cachedNearPhantasm = false;
            lastHadPhantasm = false;
            blinkTicks = 0;
            return;
        }

        boolean hasPhantasmEffect = player.hasEffect(VietnamDelightModMobEffects.PHANTASM.get());

        // Trigger eye blink at the exact moment entering ghost mode
        if (hasPhantasmEffect && !lastHadPhantasm) {
            blinkTicks = BLINK_DURATION;
        }
        lastHadPhantasm = hasPhantasmEffect;

        if (blinkTicks > 0) {
            blinkTicks--;
        }

        // Manage post-processing edge detection & dark environment shader
        if (hasPhantasmEffect) {
            clientStandingTicks = 400;
            if (!isShaderActive || mc.gameRenderer.currentEffect() == null) {
                enableShader(mc);
            }
            return;
        } else {
            if (isShaderActive) {
                disableShader(mc);
            }
        }

        // Check if player is standing near a lit Phantasm Candle
        if (player.tickCount % 4 == 0) {
            cachedNearPhantasm = isClientNearLitPhantasm(mc.level, player.blockPosition(), 8.0D);
        }

        if (cachedNearPhantasm) {
            if (clientStandingTicks < 400) {
                clientStandingTicks++;
            }
        } else {
            if (clientStandingTicks > 0) {
                clientStandingTicks = Math.max(0, clientStandingTicks - 3);
            }
        }
    }

    public static void enableShader(Minecraft mc) {
        if (mc.gameRenderer != null) {
            try {
                mc.gameRenderer.loadEffect(PHANTASM_SHADER);
                isShaderActive = true;
            } catch (Exception e) {
                VietnamDelightMod.LOGGER.error("Failed to load Phantasm shader", e);
            }
        }
    }

    public static void disableShader(Minecraft mc) {
        if (mc.gameRenderer != null) {
            try {
                mc.gameRenderer.shutdownEffect();
            } catch (Exception ignored) {}
        }
        isShaderActive = false;
    }

    private static boolean isClientNearLitPhantasm(Level level, BlockPos playerPos, double radius) {
        int r = (int) Math.ceil(radius);
        int yr = Math.min(r, 4);
        for (int x = -r; x <= r; x++) {
            for (int z = -r; z <= r; z++) {
                if (x * x + z * z > radius * radius) continue;
                for (int y = -yr; y <= yr; y++) {
                    BlockPos checkPos = playerPos.offset(x, y, z);
                    BlockState state = level.getBlockState(checkPos);
                    if (state.hasProperty(AbstractCandleBlock.LIT) && state.getValue(AbstractCandleBlock.LIT)) {
                        if (state.is(VietnamDelightModBlocks.PHANTASM_CANDLE.get()) || state.is(VietnamDelightModBlocks.PHANTASM_CANDLE_HOLDER.get())) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public static void render(GuiGraphics guiGraphics, int width, int height) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;

        boolean hasPhantasmEffect = player.hasEffect(VietnamDelightModMobEffects.PHANTASM.get());

        // 1. Cracks during preparation phase (10s to 20s)
        if (!hasPhantasmEffect && clientStandingTicks > 200) {
            float progress = (clientStandingTicks - 200) / 200.0F; // 0.0 to 1.0
            renderScreenCracks(guiGraphics, width, height, progress);
        }

        // 2. Eye-blink black flash when transitioning into spirit realm
        if (blinkTicks > 0) {
            float ratio = (float) blinkTicks / BLINK_DURATION;
            float blinkAlpha = (float) Math.sin(ratio * Math.PI); // smooth 0 -> 1 -> 0
            int alpha = (int) (255 * blinkAlpha);
            if (alpha > 0) {
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                guiGraphics.fill(0, 0, width, height, (alpha << 24)); // pure black blink
                RenderSystem.disableBlend();
            }
        }
    }

    private static void renderScreenCracks(GuiGraphics guiGraphics, int width, int height, float progress) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        int alpha = (int) (220 * Math.min(1.0F, progress * 1.3F));

        // Top-Left crack tree
        drawCrackSegment(guiGraphics, 0.00F, 0.35F, progress, 0.00F, 0.00F, 0.18F, 0.15F, width, height, alpha);
        drawCrackSegment(guiGraphics, 0.25F, 0.65F, progress, 0.18F, 0.15F, 0.30F, 0.08F, width, height, alpha);
        drawCrackSegment(guiGraphics, 0.55F, 0.90F, progress, 0.30F, 0.08F, 0.38F, 0.02F, width, height, alpha);
        drawCrackSegment(guiGraphics, 0.20F, 0.60F, progress, 0.18F, 0.15F, 0.28F, 0.32F, width, height, alpha);
        drawCrackSegment(guiGraphics, 0.50F, 0.95F, progress, 0.28F, 0.32F, 0.42F, 0.40F, width, height, alpha);

        // Top-Right crack tree
        drawCrackSegment(guiGraphics, 0.00F, 0.35F, progress, 1.00F, 0.00F, 0.82F, 0.18F, width, height, alpha);
        drawCrackSegment(guiGraphics, 0.25F, 0.65F, progress, 0.82F, 0.18F, 0.68F, 0.10F, width, height, alpha);
        drawCrackSegment(guiGraphics, 0.20F, 0.60F, progress, 0.82F, 0.18F, 0.72F, 0.32F, width, height, alpha);
        drawCrackSegment(guiGraphics, 0.50F, 0.95F, progress, 0.72F, 0.32F, 0.58F, 0.44F, width, height, alpha);

        // Bottom-Left crack tree
        drawCrackSegment(guiGraphics, 0.10F, 0.45F, progress, 0.00F, 1.00F, 0.16F, 0.82F, width, height, alpha);
        drawCrackSegment(guiGraphics, 0.35F, 0.75F, progress, 0.16F, 0.82F, 0.10F, 0.65F, width, height, alpha);
        drawCrackSegment(guiGraphics, 0.30F, 0.70F, progress, 0.16F, 0.82F, 0.32F, 0.70F, width, height, alpha);
        drawCrackSegment(guiGraphics, 0.60F, 1.00F, progress, 0.32F, 0.70F, 0.44F, 0.58F, width, height, alpha);

        // Bottom-Right crack tree
        drawCrackSegment(guiGraphics, 0.10F, 0.45F, progress, 1.00F, 1.00F, 0.84F, 0.80F, width, height, alpha);
        drawCrackSegment(guiGraphics, 0.35F, 0.75F, progress, 0.84F, 0.80F, 0.90F, 0.64F, width, height, alpha);
        drawCrackSegment(guiGraphics, 0.30F, 0.70F, progress, 0.84F, 0.80F, 0.68F, 0.68F, width, height, alpha);
        drawCrackSegment(guiGraphics, 0.60F, 1.00F, progress, 0.68F, 0.68F, 0.54F, 0.54F, width, height, alpha);

        // Center connecting shatter fractures
        drawCrackSegment(guiGraphics, 0.70F, 1.00F, progress, 0.42F, 0.40F, 0.58F, 0.44F, width, height, alpha);
        drawCrackSegment(guiGraphics, 0.75F, 1.00F, progress, 0.44F, 0.58F, 0.54F, 0.54F, width, height, alpha);
        drawCrackSegment(guiGraphics, 0.80F, 1.00F, progress, 0.42F, 0.40F, 0.44F, 0.58F, width, height, alpha);
        drawCrackSegment(guiGraphics, 0.80F, 1.00F, progress, 0.58F, 0.44F, 0.54F, 0.54F, width, height, alpha);

        RenderSystem.disableBlend();
    }

    private static void drawCrackSegment(GuiGraphics guiGraphics, float startRatio, float endRatio, float progress,
                                         float x0Norm, float y0Norm, float x1Norm, float y1Norm, int w, int h, int alpha) {
        if (progress < startRatio) return;
        float segProgress = Math.min(1.0F, (progress - startRatio) / (endRatio - startRatio));
        int x0 = (int) (x0Norm * w);
        int y0 = (int) (y0Norm * h);
        int targetX1 = (int) (x1Norm * w);
        int targetY1 = (int) (y1Norm * h);
        int x1 = (int) (x0 + (targetX1 - x0) * segProgress);
        int y1 = (int) (y0 + (targetY1 - y0) * segProgress);

        int shadowColor = ((alpha / 2) << 24) | 0x1A0033;
        int coreColor = (alpha << 24) | 0xD8B4E2;

        drawCrackLine(guiGraphics, x0 + 1, y0 + 1, x1 + 1, y1 + 1, shadowColor);
        drawCrackLine(guiGraphics, x0, y0, x1, y1, coreColor);
    }

    private static void drawCrackLine(GuiGraphics guiGraphics, int x0, int y0, int x1, int y1, int color) {
        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);
        int sx = x0 < x1 ? 1 : -1;
        int sy = y0 < y1 ? 1 : -1;
        int err = dx - dy;

        while (true) {
            guiGraphics.fill(x0, y0, x0 + 1, y0 + 1, color);
            if (x0 == x1 && y0 == y1) break;
            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x0 += sx;
            }
            if (e2 < dx) {
                err += dx;
                y0 += sy;
            }
        }
    }
}
