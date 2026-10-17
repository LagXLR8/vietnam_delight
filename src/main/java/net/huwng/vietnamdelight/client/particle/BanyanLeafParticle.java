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
public class BanyanLeafParticle extends TextureSheetParticle {
    public static BanyanLeafParticleProvider provider(SpriteSet spriteSet) {
        return new BanyanLeafParticleProvider(spriteSet);
    }

    public static class BanyanLeafParticleProvider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public BanyanLeafParticleProvider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z,
                                       double xSpeed, double ySpeed, double zSpeed) {
            return new BanyanLeafParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
        }
    }

    private final SpriteSet spriteSet;

    // Hướng gió chung, đổi chậm theo thời gian
    private double windX;
    private double windZ;

    // Trạng thái tiếp đất
    private boolean isLanded = false;
    private float landingAngle = 0.0F;
    private int groundTicks = 0;
    private static final int MAX_GROUND_TICKS = 120; // Nằm trên đất ~6 giây
    private static final int FADE_START_TICKS = 80;   // Bắt đầu fade mờ dần sau 4 giây

    // Đếm số tick liên tiếp bị cản
    private int stuckTicks = 0;

    protected BanyanLeafParticle(ClientLevel world, double x, double y, double z,
                                 double vx, double vy, double vz, SpriteSet spriteSet) {
        super(world, x, y, z);
        this.spriteSet = spriteSet;

        // Kích thước va chạm và render
        this.setSize(0.5f, 0.5f);
        this.quadSize *= 0.75f;

        // Thời gian sống tối đa trên không (nếu chưa chạm đất)
        this.lifetime = 500 + this.random.nextInt(200);

        // Trọng lực rất nhẹ, lơ lửng bồng bềnh
        this.gravity = 0.015f;
        this.hasPhysics = true;

        // Hướng gió ban đầu
        this.windX = (this.random.nextDouble() - 0.5) * 0.04;
        this.windZ = (this.random.nextDouble() - 0.5) * 0.04;

        this.xd = vx * 0.05 + this.windX;
        this.yd = vy * 0.05 - 0.01;
        this.zd = vz * 0.05 + this.windZ;

        this.roll = (float) (Math.random() * (2 * Math.PI));
        this.oRoll = this.roll;

        this.pickSprite(spriteSet);
    }

    @Override
    public void tick() {
        if (this.isLanded) {
            // Khi đã nằm trên mặt đất: cố định vị trí tuyệt đối, không di chuyển, không rung lắc
            this.xo = this.x;
            this.yo = this.y;
            this.zo = this.z;
            this.xd = 0.0;
            this.yd = 0.0;
            this.zd = 0.0;
            this.groundTicks++;

            // Mờ dần (fade out) trong những giây cuối
            if (this.groundTicks >= FADE_START_TICKS) {
                float progress = (float) (this.groundTicks - FADE_START_TICKS) / (float) (MAX_GROUND_TICKS - FADE_START_TICKS);
                this.alpha = Math.max(0.0F, 1.0F - progress);
            }

            if (this.groundTicks >= MAX_GROUND_TICKS) {
                this.remove();
            }
            return;
        }

        double prevX = this.x;
        double prevZ = this.z;
        double intendedXd = this.xd;
        double intendedZd = this.zd;

        super.tick();

        // Khi chạm mặt đất -> chuyển sang trạng thái nằm im trên đất
        if (this.onGround) {
            this.isLanded = true;
            this.landingAngle = (float) (Math.random() * Math.PI * 2.0);
            this.y += 0.015; // Nâng nhẹ để nằm phẳng ngay trên bề mặt block
            this.yo = this.y;
            this.xo = this.x;
            this.zo = this.z;
            this.xd = 0.0;
            this.yd = 0.0;
            this.zd = 0.0;
            return;
        }

        // Phát hiện bị cản ngang (đụng tường/vật thể)
        double intendedDist = Math.sqrt(intendedXd * intendedXd + intendedZd * intendedZd);
        double actualDist = Math.sqrt((this.x - prevX) * (this.x - prevX) + (this.z - prevZ) * (this.z - prevZ));

        if (intendedDist > 0.003 && actualDist < intendedDist * 0.25) {
            this.stuckTicks++;
            if (this.stuckTicks >= 4) {
                this.remove();
                return;
            }
        } else {
            this.stuckTicks = 0;
        }

        // Gió đổi hướng chậm rãi
        if (this.age % 15 == 0) {
            this.windX += (this.random.nextDouble() - 0.5) * 0.01;
            this.windZ += (this.random.nextDouble() - 0.5) * 0.01;

            this.windX = Math.max(-0.045, Math.min(0.045, this.windX));
            this.windZ = Math.max(-0.045, Math.min(0.045, this.windZ));
        }

        this.xd += (this.windX - this.xd) * 0.05;
        this.zd += (this.windZ - this.zd) * 0.05;

        if (this.xd > 0.06) this.xd = 0.06;
        if (this.xd < -0.06) this.xd = -0.06;
        if (this.zd > 0.06) this.zd = 0.06;
        if (this.zd < -0.06) this.zd = -0.06;

        this.oRoll = this.roll;
        this.roll += 0.025f;

        this.setSpriteFromAge(this.spriteSet);
    }

    @Override
    public void render(VertexConsumer buffer, Camera camera, float partialTicks) {
        Vec3 camPos = camera.getPosition();
        float x;
        float y;
        float z;

        if (this.isLanded) {
            // Khi đã nằm đất: tọa độ tĩnh cố định, triệt tiêu hoàn toàn rung lắc
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
            // Nằm im dưới đất: Đặt nằm phẳng trên mặt phẳng ngang X-Z, không xoay theo camera người chơi
            quaternion = new Quaternionf().rotationY(this.landingAngle).rotateX((float) Math.PI * 0.5F);
        } else {
            // Khi đang bay: Xoay theo camera người chơi + góc chao liệng roll
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
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }
}
