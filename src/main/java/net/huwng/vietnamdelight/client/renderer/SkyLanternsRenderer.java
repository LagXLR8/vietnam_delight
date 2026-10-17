
package net.huwng.vietnamdelight.client.renderer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.Minecraft;

import net.huwng.vietnamdelight.entity.SkyLanternsEntity;
import net.huwng.vietnamdelight.client.model.Modelsky_lanterns;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack;

public class SkyLanternsRenderer extends MobRenderer<SkyLanternsEntity, Modelsky_lanterns<SkyLanternsEntity>> {
	public SkyLanternsRenderer(EntityRendererProvider.Context context) {
		super(context, new Modelsky_lanterns(context.bakeLayer(Modelsky_lanterns.LAYER_LOCATION)), 0.2f);
		this.addLayer(new RenderLayer<SkyLanternsEntity, Modelsky_lanterns<SkyLanternsEntity>>(this) {
			final ResourceLocation LAYER_TEXTURE = new ResourceLocation("vietnam_delight:textures/entities/sky_lanterns.png");

			@Override
			public void render(PoseStack poseStack, MultiBufferSource bufferSource, int light, SkyLanternsEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
				VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.eyes(LAYER_TEXTURE));
				EntityModel model = new Modelsky_lanterns(Minecraft.getInstance().getEntityModels().bakeLayer(Modelsky_lanterns.LAYER_LOCATION));
				this.getParentModel().copyPropertiesTo(model);
				model.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTicks);
				model.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
				model.renderToBuffer(poseStack, vertexConsumer, light, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
			}
		});
	}

	@Override
	public ResourceLocation getTextureLocation(SkyLanternsEntity entity) {
		return new ResourceLocation("vietnam_delight:textures/entities/sky_lanterns.png");
	}
}
