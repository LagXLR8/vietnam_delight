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
import net.huwng.vietnamdelight.entity.SaltEntityEntity;

public class SaltProjectileRenderer extends EntityRenderer<SaltEntityEntity> {
    private final ItemRenderer itemRenderer;

    public SaltProjectileRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(SaltEntityEntity entity, float entityYaw, float partialTicks, PoseStack poseStack,
            MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();

        float yRot = Mth.lerp(partialTicks, entity.yRotO, entity.getYRot());
        float xRot = Mth.lerp(partialTicks, entity.xRotO, entity.getXRot());

        float t = entity.tickCount + partialTicks;
        // Xoay lộn 3D đẹp mắt trong khi bay từ trước ra sau
        float spinRate = Math.max(6.0F, 24.0F - t * 0.5F);
        float spin = t * spinRate;

        poseStack.mulPose(Axis.YP.rotationDegrees(yRot - 90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(xRot));
        // Xoay lộn từ phía trước ra phía sau theo trục X (Pitch rotation)
        poseStack.mulPose(Axis.XP.rotationDegrees(spin));

        poseStack.scale(0.85F, 0.85F, 0.85F);

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
    public ResourceLocation getTextureLocation(SaltEntityEntity entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
