package net.huwng.vietnamdelight.init;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.client.renderer.entity.ThrownItemRenderer;

import net.huwng.vietnamdelight.client.renderer.SkyLanternsRenderer;
import net.huwng.vietnamdelight.client.renderer.JadeRabbitRenderer;
import net.huwng.vietnamdelight.client.renderer.BananaBoateRenderer;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class VietnamDelightModEntityRenderers {
	@SubscribeEvent
	public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(VietnamDelightModEntities.BANANA_BOATE.get(), BananaBoateRenderer::new);
		event.registerEntityRenderer(VietnamDelightModEntities.SALT_ENTITY.get(), net.huwng.vietnamdelight.client.renderer.SaltProjectileRenderer::new);
		event.registerEntityRenderer(VietnamDelightModEntities.SKY_LANTERNS.get(), SkyLanternsRenderer::new);
		event.registerEntityRenderer(VietnamDelightModEntities.JADE_RABBIT.get(), JadeRabbitRenderer::new);
		event.registerEntityRenderer(VietnamDelightModEntities.DURIAN_SHELL_PROJECTILE.get(), net.huwng.vietnamdelight.client.renderer.DurianShellRenderer::new);
		event.registerEntityRenderer(VietnamDelightModEntities.EXPLOSIVE_DURIAN_SHELL_PROJECTILE.get(), net.huwng.vietnamdelight.client.renderer.DurianShellRenderer::new);
	}
}
