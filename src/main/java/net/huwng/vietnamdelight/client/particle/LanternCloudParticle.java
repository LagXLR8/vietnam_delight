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
public class LanternCloudParticle extends TextureSheetParticle {
    public static LanternCloudParticleProvider provider(SpriteSet spriteSet) {
        return new LanternCloudParticleProvider(spriteSet);
    }

    public static class LanternCloudParticleProvider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public LanternCloudParticleProvider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn,
                                       double x, double y, double z,
                                       double xSpeed, double ySpeed, double zSpeed) {
            return new LanternCloudParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
        }
    }

    private final SpriteSet spriteSet;
    private final float initialScale;

    protected LanternCloudParticle(ClientLevel world, double x, double y, double z,
                                   double vx, double vy, double vz, SpriteSet spriteSet) {
        super(world, x, y, z, vx, vy, vz);
        this.spriteSet = spriteSet;

        this.setSize(0.2f, 0.2f);
        this.lifetime = 80;        // sống lâu hơn cloud mặc định
        this.gravity = 0f;
        this.hasPhysics = false;

        this.xd = vx;
        this.yd = vy;
        this.zd = vz;

        this.pickSprite(spriteSet);

        this.initialScale = 0.5f + (this.random.nextFloat() * 0.5f);
        this.quadSize = initialScale;

    }

    @Override
    public void tick() {
        super.tick();

        // Fade out dần theo tuổi thọ
        float lifeRatio = (float) this.age / (float) this.lifetime;
        this.alpha = 1.0f - lifeRatio; // trong suốt dần

        // Scale cloud to nhỏ dần
        this.quadSize = initialScale * (1.0f - lifeRatio);

        if (this.alpha <= 0.01f) {
            this.remove();
        }
    }
    
	protected int getRenderDistance() {
		return 128;
	}

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT; // 👈 cho phép alpha
    }
}
