
package net.huwng.vietnamdelight.client.renderer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

import net.huwng.vietnamdelight.entity.BananaBoateEntity;
import net.huwng.vietnamdelight.client.model.Modelbanana_boat;

public class BananaBoateRenderer extends MobRenderer<BananaBoateEntity, Modelbanana_boat<BananaBoateEntity>> {
	public BananaBoateRenderer(EntityRendererProvider.Context context) {
		super(context, new Modelbanana_boat(context.bakeLayer(Modelbanana_boat.LAYER_LOCATION)), 0.5f);
	}

	@Override
	public ResourceLocation getTextureLocation(BananaBoateEntity entity) {
		return new ResourceLocation("vietnam_delight:textures/entities/banana_boate.png");
	}
}
