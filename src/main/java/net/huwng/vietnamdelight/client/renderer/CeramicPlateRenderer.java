package net.huwng.vietnamdelight.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.huwng.vietnamdelight.block.entity.CeramicPlateBlockEntity;

public class CeramicPlateRenderer implements BlockEntityRenderer<CeramicPlateBlockEntity> {

    public CeramicPlateRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(CeramicPlateBlockEntity be, float partialTicks, PoseStack poseStack,
                       MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        ItemStack stack = be.getStoredItem();
        if (stack.isEmpty()) return;

        poseStack.pushPose();

        // Vị trí: trung tâm block dĩa
        poseStack.translate(0.5, 0.08, 0.5);

        // Xoay quanh trục Y theo góc nhìn của người chơi khi đặt vào
        poseStack.mulPose(Axis.YP.rotationDegrees(-be.getItemRotation()));

        // Xoay item nằm ngang trên mặt dĩa
        poseStack.mulPose(Axis.XP.rotationDegrees(90));

        // Scale item cho vừa dĩa
        poseStack.scale(0.75f, 0.75f, 0.75f);

        Minecraft.getInstance().getItemRenderer().renderStatic(
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
    }
}
