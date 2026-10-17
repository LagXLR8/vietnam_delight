package net.huwng.vietnamdelight.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public class BougainvilleaPetalParticle extends TextureSheetParticle {

    public static BougainvilleaPetalParticleProvider provider(SpriteSet spriteSet) {
        return new BougainvilleaPetalParticleProvider(spriteSet);
    }

    public static class BougainvilleaPetalParticleProvider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public BougainvilleaPetalParticleProvider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z,
                                       double xSpeed, double ySpeed, double zSpeed) {
            return new BougainvilleaPetalParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
        }
    }

    private final SpriteSet spriteSet;

    // Gió và dao động tự do
    private double windX;
    private double windZ;
    private final float phase;

    // Góc quay 3D độc lập với camera (pitch, yaw, roll)
    private float pitch;
    private float oPitch;
    private float yaw;
    private float oYaw;
    private float roll;
    private float oRoll;

    private float rotSpeedPitch;
    private float rotSpeedYaw;
    private float rotSpeedRoll;

    // Nhịp Tumbling đột ngột (lộn nhào)
    private int tumbleCooldown;
    private int tumbleTicks;

    // Trạng thái tiếp đất & lướt trên mặt đất
    private boolean isSliding = false;
    private int slideTicks = 0;
    private boolean isLanded = false;
    private float landingYaw = 0.0F;
    private int groundTicks = 0;

    // Trạng thái nổi & trôi trên mặt nước
    private boolean isFloatingOnWater = false;
    private int waterTicks = 0;

    private static final int MAX_GROUND_TICKS = 140; // Nằm trên đất/nước ~7 giây
    private static final int FADE_START_TICKS = 90;  // Mờ dần trong ~2.5 giây cuối

    private static class WaterSurfaceInfo {
        final double surfaceY;
        final FluidState fluidState;
        final BlockPos pos;

        WaterSurfaceInfo(double surfaceY, FluidState fluidState, BlockPos pos) {
            this.surfaceY = surfaceY;
            this.fluidState = fluidState;
            this.pos = pos;
        }
    }

    private WaterSurfaceInfo getWaterSurfaceNear(double x, double y, double z) {
        BlockPos currentPos = BlockPos.containing(x, y, z);

        // 1. Kiểm tra chính block hiện tại
        FluidState fluid = this.level.getFluidState(currentPos);
        if (fluid.is(FluidTags.WATER) && !this.level.getFluidState(currentPos.above()).is(FluidTags.WATER)) {
            double surfaceY = currentPos.getY() + fluid.getHeight(this.level, currentPos);
            return new WaterSurfaceInfo(surfaceY, fluid, currentPos);
        }

        // 2. Kiểm tra block ngay bên dưới (phổ biến khi hạt đang ở phần không khí của source block nước hoặc ngay trên mặt nước)
        BlockPos belowPos = currentPos.below();
        FluidState belowFluid = this.level.getFluidState(belowPos);
        if (belowFluid.is(FluidTags.WATER) && !this.level.getFluidState(currentPos).is(FluidTags.WATER)) {
            double surfaceY = belowPos.getY() + belowFluid.getHeight(this.level, belowPos);
            return new WaterSurfaceInfo(surfaceY, belowFluid, belowPos);
        }

        // 3. Kiểm tra block ngay bên trên (nếu hạt đang nổi và bị sóng đẩy hơi xuống dưới)
        BlockPos abovePos = currentPos.above();
        FluidState aboveFluid = this.level.getFluidState(abovePos);
        if (aboveFluid.is(FluidTags.WATER) && !this.level.getFluidState(abovePos.above()).is(FluidTags.WATER)) {
            double surfaceY = abovePos.getY() + aboveFluid.getHeight(this.level, abovePos);
            return new WaterSurfaceInfo(surfaceY, aboveFluid, abovePos);
        }

        return null;
    }

    private void landOnWater(WaterSurfaceInfo water) {
        this.isSliding = false;
        this.isLanded = false;
        this.isFloatingOnWater = true;
        this.waterTicks = 0;
        this.landingYaw = this.yaw;
        this.pitch = (float) Math.PI * 0.5F;
        this.roll = 0.0F;

        this.y = water.surfaceY + 0.012;
        this.yo = this.y;
        this.yd = 0.0;

        Vec3 flow = water.fluidState.getFlow(this.level, water.pos);
        if (flow.lengthSqr() > 0.0001) {
            this.xd = flow.x * 0.035 + (this.random.nextDouble() - 0.5) * 0.008;
            this.zd = flow.z * 0.035 + (this.random.nextDouble() - 0.5) * 0.008;
        } else {
            // Nước tĩnh: trôi nhẹ theo quán tính rơi ban đầu + một lực đẩy gợn sóng ngẫu nhiên
            double driftAngle = this.random.nextDouble() * Math.PI * 2.0;
            double driftSpeed = 0.008 + this.random.nextDouble() * 0.012;
            this.xd = this.xd * 0.4 + Math.cos(driftAngle) * driftSpeed;
            this.zd = this.zd * 0.4 + Math.sin(driftAngle) * driftSpeed;
        }
    }

    protected BougainvilleaPetalParticle(ClientLevel world, double x, double y, double z,
                                         double vx, double vy, double vz, SpriteSet spriteSet) {
        super(world, x, y, z);
        this.spriteSet = spriteSet;

        this.setSize(0.25f, 0.25f);
        this.quadSize = 0.12f + this.random.nextFloat() * 0.04f;

        this.lifetime = 400 + this.random.nextInt(200);
        this.gravity = 0.012f;
        this.hasPhysics = true;

        this.phase = this.random.nextFloat() * (float) (Math.PI * 2.0);

        // Gió ban đầu
        this.windX = (this.random.nextDouble() - 0.5) * 0.04;
        this.windZ = (this.random.nextDouble() - 0.5) * 0.04;

        this.xd = vx * 0.05 + this.windX;
        this.yd = vy * 0.05 - 0.012;
        this.zd = vz * 0.05 + this.windZ;

        // Góc xoay 3D ban đầu hoàn toàn ngẫu nhiên
        this.pitch = this.random.nextFloat() * (float) (Math.PI * 2.0);
        this.oPitch = this.pitch;
        this.yaw = this.random.nextFloat() * (float) (Math.PI * 2.0);
        this.oYaw = this.yaw;
        this.roll = this.random.nextFloat() * (float) (Math.PI * 2.0);
        this.oRoll = this.roll;

        this.rotSpeedPitch = (this.random.nextFloat() - 0.5f) * 0.05f;
        this.rotSpeedYaw = (this.random.nextFloat() - 0.5f) * 0.04f;
        this.rotSpeedRoll = (this.random.nextFloat() - 0.5f) * 0.06f;

        this.tumbleCooldown = 20 + this.random.nextInt(35);
        this.tumbleTicks = 0;

        this.pickSprite(spriteSet);
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        this.oPitch = this.pitch;
        this.oYaw = this.yaw;
        this.oRoll = this.roll;

        // 1. Khi đã dừng hẳn trên mặt đất
        if (this.isLanded) {
            this.xd = 0.0;
            this.yd = 0.0;
            this.zd = 0.0;
            this.groundTicks++;

            if (this.groundTicks >= FADE_START_TICKS) {
                float progress = (float) (this.groundTicks - FADE_START_TICKS) / (float) (MAX_GROUND_TICKS - FADE_START_TICKS);
                this.alpha = Math.max(0.0F, 1.0F - progress);
            }

            if (this.groundTicks >= MAX_GROUND_TICKS) {
                this.remove();
            }
            return;
        }

        // 2. Khi đang nổi và trôi trên mặt nước
        if (this.isFloatingOnWater) {
            this.waterTicks++;

            WaterSurfaceInfo water = getWaterSurfaceNear(this.x, this.y, this.z);
            if (water == null) {
                if (this.onGround) {
                    settleOnGround();
                } else {
                    this.isFloatingOnWater = false;
                }
                return;
            }

            Vec3 flow = water.fluidState.getFlow(this.level, water.pos);
            if (flow.lengthSqr() > 0.0001) {
                // Nước chảy: trôi theo dòng chảy
                this.xd += (flow.x * 0.03 - this.xd) * 0.12;
                this.zd += (flow.z * 0.03 - this.zd) * 0.12;
            } else {
                // Nước tĩnh: giảm tốc dần (trôi một đoạn ngắn rồi bồng bềnh)
                this.xd *= 0.95;
                this.zd *= 0.95;

                // Thêm vi dao động sóng nước rất nhỏ làm cánh hoa/lá lay động tự nhiên
                float wave = (float) Math.sin(this.waterTicks * 0.08f + this.phase);
                this.xd += Math.cos(this.phase) * wave * 0.0004;
                this.zd += Math.sin(this.phase) * wave * 0.0004;
            }

            this.xd = Mth.clamp(this.xd, -0.04, 0.04);
            this.zd = Mth.clamp(this.zd, -0.04, 0.04);

            // Di chuyển hạt theo vận tốc trôi
            this.move(this.xd, 0.0, this.zd);

            // Giữ độ cao trên mặt nước + dập dềnh nhẹ theo gợn sóng
            float bobbing = (float) Math.sin((float) this.waterTicks * 0.12f + this.phase) * 0.004f;
            this.y = water.surfaceY + 0.012 + bobbing;
            this.yo = this.y;
            this.yd = 0.0;

            // Xoay nhẹ từ từ theo dòng trôi
            this.landingYaw += (float) (this.xd * 1.5 - this.zd * 1.5 + (this.rotSpeedYaw * 0.05f));

            // Nghiêng nhẹ dập dềnh theo sóng nước
            this.pitch = (float) Math.PI * 0.5F + (float) Math.sin(this.waterTicks * 0.10f + this.phase) * 0.04f;
            this.roll = (float) Math.cos(this.waterTicks * 0.08f + this.phase) * 0.04f;

            this.setSpriteFromAge(this.spriteSet);

            // Mờ dần trong ~2.5 giây cuối và biến mất
            if (this.waterTicks >= FADE_START_TICKS) {
                float progress = (float) (this.waterTicks - FADE_START_TICKS) / (float) (MAX_GROUND_TICKS - FADE_START_TICKS);
                this.alpha = Math.max(0.0F, 1.0F - progress);
            }

            if (this.waterTicks >= MAX_GROUND_TICKS) {
                this.remove();
            }
            return;
        }

        // 3. Chạm đất lần đầu: quyết định trượt tiếp hay dừng lại
        if (this.onGround && !this.isSliding) {
            if (this.random.nextFloat() < 0.75f) {
                // Lướt một đoạn nhỏ trên mặt đất
                this.isSliding = true;
                this.slideTicks = 10 + this.random.nextInt(15);
                // Giữ lại quán tính ngang nhưng nâng nhẹ y để tránh z-fighting
                this.y += 0.01;
                this.yo = this.y;
            } else {
                settleOnGround();
                return;
            }
        }

        // 4. Đang trong nhịp lướt trên mặt đất
        if (this.isSliding) {
            WaterSurfaceInfo water = getWaterSurfaceNear(this.x, this.y, this.z);
            if (water != null && this.y <= water.surfaceY + 0.08 && this.y >= water.surfaceY - 0.35) {
                landOnWater(water);
                return;
            }

            this.slideTicks--;

            // Ma sát làm chậm dần chuyển động lướt
            this.xd *= 0.86;
            this.zd *= 0.86;
            this.yd = 0.0;

            // Xoay dần phẳng ra song song với mặt đất
            this.pitch = Mth.lerp(0.2f, this.pitch, (float) Math.PI * 0.5F);
            this.roll = Mth.lerp(0.2f, this.roll, 0.0F);

            this.move(this.xd, this.yd, this.zd);

            double speedSq = this.xd * this.xd + this.zd * this.zd;
            if (this.slideTicks <= 0 || speedSq < 0.0001 || !this.onGround) {
                settleOnGround();
            }
            return;
        }

        // 5. Đang bay tự do lơ lửng trên không
        WaterSurfaceInfo waterBefore = getWaterSurfaceNear(this.x, this.y, this.z);
        if (waterBefore != null && this.y <= waterBefore.surfaceY + 0.08 && this.y >= waterBefore.surfaceY - 0.35 && this.yd <= 0) {
            landOnWater(waterBefore);
            return;
        }

        this.yd -= this.gravity;
        this.move(this.xd, this.yd, this.zd);

        WaterSurfaceInfo waterAfter = getWaterSurfaceNear(this.x, this.y, this.z);
        if (waterAfter != null && this.y <= waterAfter.surfaceY + 0.08 && this.y >= waterAfter.surfaceY - 0.35 && this.yd <= 0) {
            landOnWater(waterAfter);
            return;
        }

        // Gió đổi hướng chậm rãi
        if (this.age % 18 == 0) {
            this.windX += (this.random.nextDouble() - 0.5) * 0.012;
            this.windZ += (this.random.nextDouble() - 0.5) * 0.012;
            this.windX = Math.max(-0.04, Math.min(0.04, this.windX));
            this.windZ = Math.max(-0.04, Math.min(0.04, this.windZ));
        }

        this.xd += (this.windX - this.xd) * 0.06;
        this.zd += (this.windZ - this.zd) * 0.06;

        // Vận tốc tối đa bồng bềnh
        this.xd = Mth.clamp(this.xd, -0.06, 0.06);
        this.zd = Mth.clamp(this.zd, -0.06, 0.06);
        this.yd = Math.max(-0.04, this.yd);

        // Động học xoay 3D: Tumbling & Yaw
        this.tumbleCooldown--;
        if (this.tumbleCooldown <= 0) {
            this.tumbleTicks = 12 + this.random.nextInt(16);
            this.tumbleCooldown = 30 + this.random.nextInt(40);
            this.rotSpeedPitch = (this.random.nextFloat() - 0.5f) * 0.16f;
            this.rotSpeedYaw = (this.random.nextFloat() - 0.5f) * 0.12f;
        }

        if (this.tumbleTicks > 0) {
            this.tumbleTicks--;
            // Đang trong nhịp lộn nhào tumbling mạnh
            this.pitch += this.rotSpeedPitch;
            this.yaw += this.rotSpeedYaw;
            this.roll += this.rotSpeedRoll * 1.5f;
        } else {
            // Dao động bồng bềnh êm dịu theo hàm sin/cos
            float oscillation = (float) Math.sin((float) this.age * 0.15f + this.phase);
            float yawOsc = (float) Math.cos((float) this.age * 0.10f + this.phase);

            this.pitch += this.rotSpeedPitch * 0.6f + oscillation * 0.04f;
            this.yaw += this.rotSpeedYaw * 0.6f + yawOsc * 0.03f;
            this.roll += this.rotSpeedRoll;
        }

        this.setSpriteFromAge(this.spriteSet);

        if (this.age++ >= this.lifetime) {
            this.remove();
        }
    }

    private void settleOnGround() {
        this.isSliding = false;
        this.isFloatingOnWater = false;
        this.isLanded = true;
        this.landingYaw = this.yaw;
        this.y += 0.012; // Nâng nhẹ để nằm phẳng trên mặt block
        this.yo = this.y;
        this.xo = this.x;
        this.zo = this.z;
        this.xd = 0.0;
        this.yd = 0.0;
        this.zd = 0.0;
    }

    @Override
    public void render(VertexConsumer buffer, Camera camera, float partialTicks) {
        Vec3 camPos = camera.getPosition();
        float x = (float) (Mth.lerp((double) partialTicks, this.xo, this.x) - camPos.x());
        float y = (float) (Mth.lerp((double) partialTicks, this.yo, this.y) - camPos.y());
        float z = (float) (Mth.lerp((double) partialTicks, this.zo, this.z) - camPos.z());

        Quaternionf quaternion;
        if (this.isLanded) {
            // Nằm im phẳng trên mặt đất, xoay theo góc landingYaw ngẫu nhiên
            quaternion = new Quaternionf().rotationY(this.landingYaw).rotateX((float) Math.PI * 0.5F);
        } else if (this.isFloatingOnWater) {
            // Nằm phẳng trên mặt nước, xoay theo landingYaw và dập dềnh sóng nhẹ
            quaternion = new Quaternionf().rotationY(this.landingYaw).rotateX(this.pitch).rotateZ(this.roll);
        } else {
            // Tự do xoay 3D (Pitch, Yaw, Roll) trong không gian thế giới, KHÔNG quay mặt theo camera
            float curPitch = Mth.lerp(partialTicks, this.oPitch, this.pitch);
            float curYaw = Mth.lerp(partialTicks, this.oYaw, this.yaw);
            float curRoll = Mth.lerp(partialTicks, this.oRoll, this.roll);

            quaternion = new Quaternionf().rotationYXZ(curYaw, curPitch, curRoll);
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

        // Mặt trước (Front face)
        buffer.vertex((double) vertices[0].x(), (double) vertices[0].y(), (double) vertices[0].z()).uv(u1, v1).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(light).endVertex();
        buffer.vertex((double) vertices[1].x(), (double) vertices[1].y(), (double) vertices[1].z()).uv(u1, v0).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(light).endVertex();
        buffer.vertex((double) vertices[2].x(), (double) vertices[2].y(), (double) vertices[2].z()).uv(u0, v0).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(light).endVertex();
        buffer.vertex((double) vertices[3].x(), (double) vertices[3].y(), (double) vertices[3].z()).uv(u0, v1).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(light).endVertex();

        // Mặt sau (Back face - render hai mặt để khi lộn nhào không bị tàng hình do culling)
        buffer.vertex((double) vertices[3].x(), (double) vertices[3].y(), (double) vertices[3].z()).uv(u0, v1).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(light).endVertex();
        buffer.vertex((double) vertices[2].x(), (double) vertices[2].y(), (double) vertices[2].z()).uv(u0, v0).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(light).endVertex();
        buffer.vertex((double) vertices[1].x(), (double) vertices[1].y(), (double) vertices[1].z()).uv(u1, v0).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(light).endVertex();
        buffer.vertex((double) vertices[0].x(), (double) vertices[0].y(), (double) vertices[0].z()).uv(u1, v1).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(light).endVertex();
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }
}
