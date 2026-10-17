package net.huwng.vietnamdelight.client.renderer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.model.RabbitModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

import net.huwng.vietnamdelight.entity.JadeRabbitEntity;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack;

public class JadeRabbitRenderer extends MobRenderer<JadeRabbitEntity, RabbitModel<JadeRabbitEntity>> {
	public JadeRabbitRenderer(EntityRendererProvider.Context context) {
		super(context, new RabbitModel<>(context.bakeLayer(ModelLayers.RABBIT)), 0.3f);

		// Glow layer
		this.addLayer(new RenderLayer<JadeRabbitEntity, RabbitModel<JadeRabbitEntity>>(this) {
			final ResourceLocation LAYER_TEXTURE = new ResourceLocation("vietnam_delight:textures/entities/jade_glow.png");

			@Override
			public void render(PoseStack poseStack, MultiBufferSource bufferSource, int light,
							   JadeRabbitEntity entity, float limbSwing, float limbSwingAmount,
							   float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
				VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.eyes(LAYER_TEXTURE));
				this.getParentModel().renderToBuffer(poseStack, vertexConsumer, light,
						LivingEntityRenderer.getOverlayCoords(entity, 0), 1, 1, 1, 1);
			}
		});
	}

	@Override
	protected void scale(JadeRabbitEntity entity, PoseStack poseStack, float f) {
		poseStack.scale(1f, 1f, 1);
	}

	@Override
	public ResourceLocation getTextureLocation(JadeRabbitEntity entity) {
		return new ResourceLocation("vietnam_delight:textures/entities/jade.png");
	}
}
