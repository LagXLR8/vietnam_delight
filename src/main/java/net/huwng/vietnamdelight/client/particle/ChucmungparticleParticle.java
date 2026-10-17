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
public class ChucmungparticleParticle extends TextureSheetParticle {
    public static ChucmungparticleParticleProvider provider(SpriteSet spriteSet) {
        return new ChucmungparticleParticleProvider(spriteSet);
    }

    public static class ChucmungparticleParticleProvider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public ChucmungparticleParticleProvider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new ChucmungparticleParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
        }
    }

    private final SpriteSet spriteSet;
    private final float initialAlpha;
    private float baseQuadSize;

    protected ChucmungparticleParticle(ClientLevel world, double x, double y, double z, double vx, double vy, double vz, SpriteSet spriteSet) {
        super(world, x, y, z);
        this.spriteSet = spriteSet;
        this.setSize(0.2f, 0.2f);
        
        // LIFETIME 200 TICKS = 10 GIÂY (đủ để hiển thị đầy đủ)
        // Giảm xuống 200 vì block code sẽ spawn lại particles liên tục
        this.lifetime = 200;
        this.gravity = 0f; // Không bị rơi
        this.hasPhysics = false; // Tắt physics để giữ nguyên vị trí
        
        // Velocity - nếu có velocity thì dùng, không thì đứng yên
        this.xd = vx;
        this.yd = vy;
        this.zd = vz;
        
        // Màu sắc gradient hồng đỏ tươi sáng
        float colorVariation = (float)(Math.random() * 0.2);
        this.rCol = 1.0f;
        this.gCol = 0.1f + colorVariation; // Thêm biến thể màu
        this.bCol = 0.4f + colorVariation;
        this.alpha = 0.95f; // Alpha cao để rõ nét
        this.initialAlpha = 0.95f;
        
        // Kích thước ngẫu nhiên nhẹ để tạo chiều sâu
        this.baseQuadSize = 0.35f + (float)(Math.random() * 0.15);
        this.quadSize = this.baseQuadSize;
        
        this.setSpriteFromAge(spriteSet);
    }

    @Override
    public int getLightColor(float partialTick) {
        // Luôn sáng tối đa để nhìn rõ từ xa
        return 15728880;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void tick() {
        super.tick();
        
        // Cập nhật sprite theo thời gian nếu có nhiều frame
        this.setSpriteFromAge(spriteSet);
        
        // Tính toán fade out effect
        float lifeProgress = (float) this.age / (float) this.lifetime;
        
        // Giữ alpha = 0.95 trong 90% đầu, sau đó fade out nhanh
        if (lifeProgress > 0.90f) {
            float fadeProgress = (lifeProgress - 0.90f) / 0.10f;
            this.alpha = initialAlpha * (1.0f - fadeProgress * 0.7f); // Fade nhẹ, không biến mất hoàn toàn
        }
        
        // Thêm hiệu ứng lấp lánh tinh tế (mỗi 30 tick = 1.5 giây)
        int sparkCycle = this.age % 30;
        if (sparkCycle < 3) {
            // Tăng size khi lấp lánh
            this.quadSize = this.baseQuadSize * 1.25f;
            this.alpha = Math.min(1.0f, this.alpha * 1.15f);
        } else if (sparkCycle < 6) {
            // Trở về bình thường
            this.quadSize = this.baseQuadSize;
        } else {
            // Giảm nhẹ
            this.quadSize = this.baseQuadSize * 0.95f;
        }
        
        // Làm chậm velocity dần dần (nếu có)
        if (Math.abs(this.xd) > 0.001 || Math.abs(this.yd) > 0.001 || Math.abs(this.zd) > 0.001) {
            this.xd *= 0.92;
            this.yd *= 0.92;
            this.zd *= 0.92;
            
            // Dừng hẳn khi velocity quá nhỏ
            if (Math.abs(this.xd) < 0.001) this.xd = 0;
            if (Math.abs(this.yd) < 0.001) this.yd = 0;
            if (Math.abs(this.zd) < 0.001) this.zd = 0;
        }
    }
}