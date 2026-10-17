package net.huwng.vietnamdelight.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public class JadeDropParticle extends TextureSheetParticle {
    public static JadeDropParticleProvider provider(SpriteSet spriteSet) {
        return new JadeDropParticleProvider(spriteSet);
    }

    public static class JadeDropParticleProvider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public JadeDropParticleProvider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z,
                                       double xSpeed, double ySpeed, double zSpeed) {
            return new JadeDropParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
        }
    }

    private final SpriteSet spriteSet;
    private final float rotSpeed;

    // Trạng thái tiếp đất
    private boolean isLanded = false;
    private float landingAngle = 0.0F;
    private int groundTicks = 0;
    private static final int MAX_GROUND_TICKS = 100; // Nằm trên đất ~5 giây
    private static final int FADE_START_TICKS = 60;   // Mờ dần trong 2 giây cuối

    protected JadeDropParticle(ClientLevel world, double x, double y, double z,
                               double vx, double vy, double vz, SpriteSet spriteSet) {
        super(world, x, y, z);
        this.spriteSet = spriteSet;

        this.setSize(0.01F, 0.01F);

        // Thời gian sống trên không
        this.lifetime = this.random.nextInt(80) + 60;
        this.gravity = 0.04F;
        this.hasPhysics = true;

        // Vận tốc và hướng ban đầu như gốc
        this.xd = (this.random.nextDouble() - 0.5D) * 0.1D;
        this.yd = -0.05D;
        this.zd = (this.random.nextDouble() - 0.5D) * 0.1D;
        this.rotSpeed = (float) Math.random() * 0.2F - 0.1F;
        this.roll = (float) (Math.random() * (2 * Math.PI));
        this.oRoll = this.roll;
        this.alpha = 0.8F;

        this.pickSprite(spriteSet);
    }

    @Override
    public void tick() {
        if (this.isLanded) {
            // Khi đã nằm tĩnh trên mặt đất: cố định vị trí, không dịch chuyển
            this.xo = this.x;
            this.yo = this.y;
            this.zo = this.z;
            this.xd = 0.0;
            this.yd = 0.0;
            this.zd = 0.0;
            this.groundTicks++;

            // Mờ dần (fade out)
            if (this.groundTicks >= FADE_START_TICKS) {
                float progress = (float) (this.groundTicks - FADE_START_TICKS) / (float) (MAX_GROUND_TICKS - FADE_START_TICKS);
                this.alpha = Math.max(0.0F, 0.8F * (1.0F - progress));
            }

            if (this.groundTicks >= MAX_GROUND_TICKS) {
                this.remove();
            }
            return;
        }

        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (this.age++ >= this.lifetime) {
            this.remove();
            return;
        }

        // Vật lý rơi và nảy
        this.yd -= (double) this.gravity;
        this.xd += (this.random.nextDouble() - 0.5D) * 0.002D;
        this.zd += (this.random.nextDouble() - 0.5D) * 0.002D;
        this.xd *= 0.98D;
        this.zd *= 0.98D;
        this.move(this.xd, this.yd, this.zd);

        // Xoay khi đang rơi
        this.oRoll = this.roll;
        this.roll += this.rotSpeed;

        if (this.onGround) {
            this.xd *= 0.7D;
            this.zd *= 0.7D;

            // Khi vận tốc đã triệt tiêu -> chuyển sang trạng thái nằm phẳng trên mặt đất như lá sầu riêng
            if (Math.abs(this.xd) < 0.015D && Math.abs(this.zd) < 0.015D && Math.abs(this.yd) < 0.015D) {
                this.isLanded = true;
                this.landingAngle = (float) (Math.random() * Math.PI * 2.0);
                this.y += 0.015; // Nâng nhẹ trên bề mặt block tránh z-fighting
                this.yo = this.y;
                this.xo = this.x;
                this.zo = this.z;
                this.xd = 0.0;
                this.yd = 0.0;
                this.zd = 0.0;
                this.gravity = 0.0F;
                return;
            } else {
                // Bounce nhẹ khi chạm đất
                this.yd = Math.abs(this.yd) * 0.3D;
                this.gravity *= 0.98F;
            }
        }

        this.setSpriteFromAge(this.spriteSet);
    }

    @Override
    public void render(VertexConsumer buffer, Camera camera, float partialTicks) {
        Vec3 camPos = camera.getPosition();
        float x;
        float y;
        float z;

        if (this.isLanded) {
            // Tọa độ tĩnh khi nằm đất
            x = (float) (this.x - camPos.x());
            y = (float) (this.y - camPos.y());
            z = (float) (this.z - camPos.z());
        } else {
            x = (float) (Mth.lerp((double) partialTicks, this.xo, this.x) - camPos.x());
            y = (float) (Mth.lerp((double) partialTicks, this.yo, this.y) - camPos.y());
            z = (float) (Mth.lerp((double) partialTicks, this.zo, this.z) - camPos.z());
        }

        Quaternionf quaternion;
        if (this.isLanded) {
            // Nằm phẳng trên mặt phẳng ngang X-Z
            quaternion = new Quaternionf().rotationY(this.landingAngle).rotateX((float) Math.PI * 0.5F);
        } else {
            // Xoay theo camera + roll khi đang rơi
            quaternion = new Quaternionf(camera.rotation());
            if (this.roll != 0.0F) {
                quaternion.rotateZ(Mth.lerp(partialTicks, this.oRoll, this.roll));
            }
        }

        Vector3f[] vertices = new Vector3f[]{
                new Vector3f(-1.0F, -1.0F, 0.0F),
                new Vector3f(-1.0F, 1.0F, 0.0F),
                new Vector3f(1.0F, 1.0F, 0.0F),
                new Vector3f(1.0F, -1.0F, 0.0F)
        };
        float size = this.getQuadSize(partialTicks);

        for (int i = 0; i < 4; ++i) {
            Vector3f vertex = vertices[i];
            vertex.rotate(quaternion);
            vertex.mul(size);
            vertex.add(x, y, z);
        }

        float u0 = this.getU0();
        float u1 = this.getU1();
        float v0 = this.getV0();
        float v1 = this.getV1();
        int light = this.getLightColor(partialTicks);

        buffer.vertex((double) vertices[0].x(), (double) vertices[0].y(), (double) vertices[0].z()).uv(u1, v1).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(light).endVertex();
        buffer.vertex((double) vertices[1].x(), (double) vertices[1].y(), (double) vertices[1].z()).uv(u1, v0).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(light).endVertex();
        buffer.vertex((double) vertices[2].x(), (double) vertices[2].y(), (double) vertices[2].z()).uv(u0, v0).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(light).endVertex();
        buffer.vertex((double) vertices[3].x(), (double) vertices[3].y(), (double) vertices[3].z()).uv(u0, v1).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(light).endVertex();
    }

    @Override
    protected int getLightColor(float partialTick) {
        // Full bright glow
        return 15728880;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }
}