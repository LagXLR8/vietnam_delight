package net.huwng.vietnamdelight.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

import net.huwng.vietnamdelight.block.entity.CeramicClayPotBlockEntity;
import net.huwng.vietnamdelight.init.VietnamDelightModBlocks;

import java.util.Random;

public class CeramicClayPotRenderer implements BlockEntityRenderer<CeramicClayPotBlockEntity> {

    public CeramicClayPotRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(CeramicClayPotBlockEntity be, float partialTicks, PoseStack poseStack,
                       MultiBufferSource buffer, int combinedLight, int combinedOverlay) {

        // Nếu nồi có nắp đậy (Lid) thì không cần render bên trong
        if (be.getLevel() != null && be.getLevel().getBlockState(be.getBlockPos().above()).is(VietnamDelightModBlocks.CERAMIC_CLAY_LID.get())) {
            return;
        }

        ItemStackHandler inv = be.getItems();
        Minecraft mc = Minecraft.getInstance();

        int renderIndex = 0;

        // Render các nguyên liệu input (slot 0-5)
        for (int slot = 0; slot < 6; slot++) {
            ItemStack stack = inv.getStackInSlot(slot);
            if (stack.isEmpty()) continue;

            poseStack.pushPose();

            // Chiều cao nâng dần theo số lượng input
            double height = 0.18 + (renderIndex * 0.07);
            poseStack.translate(0.5, height, 0.5);

            // Random xoay ổn định theo hash của slot và stack
            int seed = stack.hashCode() ^ (slot * 31);
            Random rand = new Random(seed);
            float rotation = rand.nextFloat() * 360f;
            poseStack.mulPose(Axis.YP.rotationDegrees(rotation));

            // Nằm ngang trong lòng nồi
            poseStack.mulPose(Axis.XP.rotationDegrees(90));
            poseStack.scale(0.55f, 0.55f, 0.55f);

            mc.getItemRenderer().renderStatic(
                    stack,
                    ItemDisplayContext.FIXED,
                    combinedLight,
                    combinedOverlay,
                    poseStack,
                    buffer,
                    be.getLevel(),
                    0
            );

            poseStack.popPose();
            renderIndex++;
        }

        // Render món ăn thành phẩm ở slot output (slot 6)
        if (inv.getSlots() > 6) {
            ItemStack outputStack = inv.getStackInSlot(6);
            if (!outputStack.isEmpty()) {
                int layers = (int) Math.ceil(outputStack.getCount() / 16.0);

                for (int i = 0; i < layers; i++) {
                    poseStack.pushPose();

                    double height = 0.22 + (renderIndex * 0.07) + (i * 0.05);
                    poseStack.translate(0.5, height, 0.5);

                    long seed = outputStack.getItem().hashCode()
                              + be.getBlockPos().asLong()
                              + (i * 131L);
                    Random rand = new Random(seed);
                    float rotation = (i * 72f) + rand.nextFloat() * 25f;
                    poseStack.mulPose(Axis.YP.rotationDegrees(rotation));

                    poseStack.mulPose(Axis.XP.rotationDegrees(90));
                    poseStack.scale(0.6f, 0.6f, 0.6f);

                    mc.getItemRenderer().renderStatic(
                            outputStack,
                            ItemDisplayContext.FIXED,
                            combinedLight,
                            combinedOverlay,
                            poseStack,
                            buffer,
                            be.getLevel(),
                            0
                    );

                    poseStack.popPose();
                }
            }
        }
    }
}
