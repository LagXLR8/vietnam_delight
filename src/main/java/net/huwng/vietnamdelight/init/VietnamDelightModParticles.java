package net.huwng.vietnamdelight.init;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.api.distmarker.Dist;

import net.huwng.vietnamdelight.client.particle.SteamParticle;
import net.huwng.vietnamdelight.client.particle.SkyLanternFlashParticle;
import net.huwng.vietnamdelight.client.particle.LanternCloudParticle;
import net.huwng.vietnamdelight.client.particle.JadeDropParticle;
import net.huwng.vietnamdelight.client.particle.ChucmungparticleParticle;
import net.huwng.vietnamdelight.client.particle.BanyanLeafParticle;
import net.huwng.vietnamdelight.client.particle.FlamboyantPetalParticle;
import net.huwng.vietnamdelight.client.particle.GoldenFlamboyantPetalParticle;
import net.huwng.vietnamdelight.client.particle.DurianLeafParticle;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class VietnamDelightModParticles {
	@SubscribeEvent
	public static void registerParticles(RegisterParticleProvidersEvent event) {
		event.registerSpriteSet(VietnamDelightModParticleTypes.SKY_LANTERN_FLASH.get(), SkyLanternFlashParticle::provider);
		event.registerSpriteSet(VietnamDelightModParticleTypes.LANTERN_CLOUD.get(), LanternCloudParticle::provider);
		event.registerSpriteSet(VietnamDelightModParticleTypes.BANYAN_LEAF.get(), BanyanLeafParticle::provider);
		event.registerSpriteSet(VietnamDelightModParticleTypes.FLAMBOYANT_PETAL.get(), FlamboyantPetalParticle::provider);
		event.registerSpriteSet(VietnamDelightModParticleTypes.GOLDEN_FLAMBOYANT_PETAL.get(), GoldenFlamboyantPetalParticle::provider);
		event.registerSpriteSet(VietnamDelightModParticleTypes.JADE_DROP.get(), JadeDropParticle::provider);
		event.registerSpriteSet(VietnamDelightModParticleTypes.CHUCMUNGPARTICLE.get(), ChucmungparticleParticle::provider);
		event.registerSpriteSet(VietnamDelightModParticleTypes.STEAM.get(), SteamParticle::provider);
		event.registerSpriteSet(VietnamDelightModParticleTypes.DURIAN_LEAF.get(), DurianLeafParticle::provider);
		event.registerSpriteSet(VietnamDelightModParticleTypes.ORANGE_BOUGAINVILLEA_PARTICLE.get(), net.huwng.vietnamdelight.client.particle.BougainvilleaPetalParticle::provider);
		event.registerSpriteSet(VietnamDelightModParticleTypes.PINK_BOUGAINVILLEA_PARTICLE.get(), net.huwng.vietnamdelight.client.particle.BougainvilleaPetalParticle::provider);
		event.registerSpriteSet(VietnamDelightModParticleTypes.WHITE_BOUGAINVILLEA_PARTICLE.get(), net.huwng.vietnamdelight.client.particle.BougainvilleaPetalParticle::provider);
		event.registerSpriteSet(VietnamDelightModParticleTypes.LEAF_BOUGAINVILLEA_PARTICLE.get(), net.huwng.vietnamdelight.client.particle.BougainvilleaPetalParticle::provider);
	}
}
