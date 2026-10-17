package net.huwng.vietnamdelight.client.particle;

import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.multiplayer.ClientLevel;

@OnlyIn(Dist.CLIENT)
public class SkyLanternFlashParticle extends TextureSheetParticle {
	public static SkyLanternFlashParticleProvider provider(SpriteSet spriteSet) {
		return new SkyLanternFlashParticleProvider(spriteSet);
	}

	public static class SkyLanternFlashParticleProvider implements ParticleProvider<SimpleParticleType> {
		private final SpriteSet spriteSet;

		public SkyLanternFlashParticleProvider(SpriteSet spriteSet) {
			this.spriteSet = spriteSet;
		}

		@Override
		public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z,
		                               double xSpeed, double ySpeed, double zSpeed) {
			return new SkyLanternFlashParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
		}
	}

	private final SpriteSet spriteSet;

	protected SkyLanternFlashParticle(ClientLevel world, double x, double y, double z,
	                                  double vx, double vy, double vz, SpriteSet spriteSet) {
		super(world, x, y, z);
		this.spriteSet = spriteSet;

		this.setSize(0.2f, 0.2f);

		this.lifetime = 3;   // sống ~1.5 giây
		this.gravity = 0f;
		this.hasPhysics = false;
		this.xd = vx;
		this.yd = vy;
		this.zd = vz;

		this.alpha = 0f; // bắt đầu trong suốt
		this.quadSize = 0.5f; // kích thước ban đầu
		this.pickSprite(spriteSet);
	}

	@Override
	public void tick() {
		super.tick();

		float lifeFrac = (float) this.age / (float) this.lifetime;

		// Fade in 30% đầu, fade out 30% cuối
		if (lifeFrac < 0.3f) {
			this.alpha = lifeFrac / 0.3f; // 0 -> 1
		} else if (lifeFrac > 0.7f) {
			this.alpha = 1f - ((lifeFrac - 0.7f) / 0.3f); // 1 -> 0
		} else {
			this.alpha = 1f;
		}

		// ✨ Flicker: mỗi 4 tick giảm sáng nhẹ
		if (this.age % 4 == 0) {
			this.alpha *= 0.6f;
		}

		// Particle nở ra nhẹ theo tuổi
		this.quadSize = 0.5f + (lifeFrac * 1f);
	}

	@Override
	public int getLightColor(float partialTick) {
		return 15728880; // luôn sáng (full bright)
	}

	@Override
	public ParticleRenderType getRenderType() {
		// translucent để hỗ trợ alpha trong suốt
		return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
	}

	// không override, chỉ thêm method thường để cho phép nhìn xa
	protected int getRenderDistance() {
		return 128;
	}
}
