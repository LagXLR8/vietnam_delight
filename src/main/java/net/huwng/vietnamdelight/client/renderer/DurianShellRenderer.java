package net.huwng.vietnamdelight.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.huwng.vietnamdelight.entity.DurianShellProjectileEntity;

public class DurianShellRenderer<T extends DurianShellProjectileEntity> extends EntityRenderer<T> {
    private final ItemRenderer itemRenderer;

    public DurianShellRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(T entity, float entityYaw, float partialTicks, PoseStack poseStack,
            MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();

        if (entity.isGrounded()) {
            // Khi đã cắm / nằm trên mặt đất: Nằm cố định, không xoay
            poseStack.mulPose(Axis.YP.rotationDegrees(entity.getGroundYRot()));
            poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
            poseStack.scale(1.25F, 1.25F, 1.25F);
        } else {
            // Khi đang bay trên không: Xoay lộn từ trước ra sau và giảm dần tốc độ xoay theo thời gian
            float yRot = Mth.lerp(partialTicks, entity.yRotO, entity.getYRot());
            float xRot = Mth.lerp(partialTicks, entity.xRotO, entity.getXRot());

            float t = entity.tickCount + partialTicks;
            // Tốc độ xoay giảm dần theo thời gian bay
            float spinRate = Math.max(4.0F, 28.0F - t * 0.6F);
            float spin = t * spinRate;

            poseStack.mulPose(Axis.YP.rotationDegrees(yRot - 90.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(xRot));
            // Xoay lộn từ phía trước ra phía sau (Pitch rotation)
            poseStack.mulPose(Axis.XP.rotationDegrees(spin));

            poseStack.scale(1F, 1F, 1F);
        }

        this.itemRenderer.renderStatic(
                entity.getItem(),
                ItemDisplayContext.GROUND,
                packedLight,
                OverlayTexture.NO_OVERLAY,
                poseStack,
                buffer,
                entity.level(),
                entity.getId());

        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
