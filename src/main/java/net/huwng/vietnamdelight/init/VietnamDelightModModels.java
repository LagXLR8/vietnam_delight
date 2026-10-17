package net.huwng.vietnamdelight.init;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.api.distmarker.Dist;

import net.huwng.vietnamdelight.client.model.Modelsky_lanterns;
import net.huwng.vietnamdelight.client.model.Modelbanana_boat;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = {Dist.CLIENT})
public class VietnamDelightModModels {
	@SubscribeEvent
	public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
		event.registerLayerDefinition(Modelbanana_boat.LAYER_LOCATION, Modelbanana_boat::createBodyLayer);
		event.registerLayerDefinition(Modelsky_lanterns.LAYER_LOCATION, Modelsky_lanterns::createBodyLayer);
	}
}
