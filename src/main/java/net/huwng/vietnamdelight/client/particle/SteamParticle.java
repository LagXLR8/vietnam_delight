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
public class SteamParticle extends TextureSheetParticle {
    public static SteamParticleProvider provider(SpriteSet spriteSet) {
        return new SteamParticleProvider(spriteSet);
    }

    public static class SteamParticleProvider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public SteamParticleProvider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new SteamParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
        }
    }

    private final SpriteSet spriteSet;
    private final float initialAlpha;

    protected SteamParticle(ClientLevel world, double x, double y, double z, double vx, double vy, double vz, SpriteSet spriteSet) {
        super(world, x, y, z);
        this.spriteSet = spriteSet;
        
        // Kích thước ban đầu nhỏ, sẽ phình to dần
        this.setSize(0.15f, 0.15f);
        this.quadSize = 0.15f;
        
        // Thời gian sống lâu hơn để bay lên cao
        this.lifetime = 40 + this.random.nextInt(20); // 40-60 ticks (2-3 giây)
        
        this.gravity = -0.01f; // Gravity âm = bay lên
        this.hasPhysics = false;
        
        // Vận tốc bay lên chậm + random ngang nhẹ
        this.xd = (this.random.nextDouble() - 0.5) * 0.02; // Di chuyển ngang nhẹ
        this.yd = 0.03 + this.random.nextDouble() * 0.02; // Bay lên chậm
        this.zd = (this.random.nextDouble() - 0.5) * 0.02;
        
        // Độ trong suốt ban đầu (hơi mờ)
        this.initialAlpha = 0.6f + this.random.nextFloat() * 0.2f; // 0.6-0.8
        this.alpha = this.initialAlpha;
        
        // Màu trắng xám cho steam
        this.rCol = 0.9f + this.random.nextFloat() * 0.1f;
        this.gCol = 0.9f + this.random.nextFloat() * 0.1f;
        this.bCol = 0.9f + this.random.nextFloat() * 0.1f;
        
        this.setSpriteFromAge(spriteSet);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void tick() {
        super.tick();
        
        if (!this.removed) {
            // Cập nhật sprite
            this.setSprite(this.spriteSet.get((this.age / 3) % 12 + 1, 12));
            
            // Phình to dần theo thời gian
            float agePercent = (float) this.age / (float) this.lifetime;
            this.quadSize = 0.15f + (agePercent * 0.25f); // Từ 0.15 -> 0.4
            
            // Mờ dần khi gần hết tuổi thọ
            if (agePercent > 0.7f) {
                // 30% cuối đời: mờ dần từ alpha ban đầu -> 0
                float fadeProgress = (agePercent - 0.7f) / 0.3f;
                this.alpha = this.initialAlpha * (1.0f - fadeProgress);
            }
            
            // Giảm tốc độ bay lên dần (mô phỏng ma sát không khí)
            this.yd *= 0.98;
            this.xd *= 0.95;
            this.zd *= 0.95;
        }
    }
}