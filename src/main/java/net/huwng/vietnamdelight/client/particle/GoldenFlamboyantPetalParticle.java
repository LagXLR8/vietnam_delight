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
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

import net.huwng.vietnamdelight.client.bloom.BloomRenderer;

/**
 * IMPORTANT: this particle does NOT rely on ParticleEngine calling render()
 * for us -- same technique as GlowParticle in the BloomTest POC. It's
 * ParticleRenderType.NO_RENDER, so ParticleEngine's own batched loop never
 * touches it; instead we keep our own static registry of live instances and
 * draw them ourselves from FlamboyantBloomRenderEvents at
 * RenderLevelStageEvent.Stage.AFTER_PARTICLES (both the normal appearance
 * AND the bloom copy, in the same pass -- see renderAllManually()).
 * ParticleEngine still owns tick()/movement/lifetime as normal, we only
 * took over drawing.
 */
@OnlyIn(Dist.CLIENT)
public class GoldenFlamboyantPetalParticle extends TextureSheetParticle {
    public static GoldenFlamboyantPetalParticleProvider provider(SpriteSet spriteSet) {
        return new GoldenFlamboyantPetalParticleProvider(spriteSet);
    }

    public static class GoldenFlamboyantPetalParticleProvider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public GoldenFlamboyantPetalParticleProvider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z,
                                       double xSpeed, double ySpeed, double zSpeed) {
            return new GoldenFlamboyantPetalParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
        }
    }

    private static final List<GoldenFlamboyantPetalParticle> ACTIVE = new ArrayList<>();

    private final SpriteSet spriteSet;

    // hướng gió chung, đổi chậm theo thời gian để cánh hoa cùng trôi theo 1 chiều tự nhiên
    private double windX;
    private double windZ;

    // đếm số tick liên tiếp bị cản (đụng tường/vật cản) để biết khi nào nên biến mất
    private int stuckTicks = 0;

    protected GoldenFlamboyantPetalParticle(ClientLevel world, double x, double y, double z,
                                      double vx, double vy, double vz, SpriteSet spriteSet) {
        super(world, x, y, z);
        this.spriteSet = spriteSet;

        // kích thước va chạm (bounding box) của cánh hoa
        this.setSize(0.6f, 0.6f);

        // giảm scale hiển thị của particle lại (quadSize là size render thật sự, setSize chỉ là hitbox)
        this.quadSize *= 0.7f;

        // thời gian sống tối đa chỉ là lưới an toàn: bình thường cánh hoa sẽ biến mất
        // ngay khi chạm đất hoặc bị vật cản chặn lại (xem tick()), không cần chờ hết thời gian này.
        // Tăng lên khá dài (~20-30 giây) để cánh hoa có thể bay lượn lâu nếu chưa chạm gì.
        this.lifetime = 400 + this.random.nextInt(200);

        // trọng lực rất nhẹ, gần như lơ lửng rồi mới rơi
        this.gravity = 0.015f;

        this.hasPhysics = true;

        // hướng gió ngẫu nhiên ban đầu, nhẹ
        this.windX = (this.random.nextDouble() - 0.5) * 0.05;
        this.windZ = (this.random.nextDouble() - 0.5) * 0.05;

        // vận tốc ban đầu nhỏ
        this.xd = vx * 0.05 + this.windX;
        this.yd = vy * 0.05 - 0.01;
        this.zd = vz * 0.05 + this.windZ;

        // random góc quay
        this.roll = (float) (Math.random() * (2 * Math.PI));
        this.oRoll = this.roll;

        this.pickSprite(spriteSet);

        ACTIVE.add(this);
    }

    @Override
    public void remove() {
        super.remove();
        ACTIVE.remove(this);
    }

    @Override
    public void tick() {
        // lưu vị trí + vận tốc dự kiến trước khi super.tick() di chuyển particle,
        // dùng để phát hiện particle có bị vật cản chặn ngang hay không
        double prevX = this.x;
        double prevZ = this.z;
        double intendedXd = this.xd;
        double intendedZd = this.zd;

        super.tick();

        // đã chạm đất -> biến mất ngay, không cần chờ hết lifetime
        if (this.onGround) {
            this.remove();
            return;
        }

        // phát hiện bị cản ngang (đụng tường/vật thể): vận tốc dự kiến đáng kể
        // nhưng vị trí thực tế gần như không đổi sau khi va chạm được xử lý trong super.tick()
        double intendedDist = Math.sqrt(intendedXd * intendedXd + intendedZd * intendedZd);
        double actualDist = Math.sqrt((this.x - prevX) * (this.x - prevX) + (this.z - prevZ) * (this.z - prevZ));

        if (intendedDist > 0.003 && actualDist < intendedDist * 0.25) {
            this.stuckTicks++;
            // chờ vài tick liên tiếp để chắc chắn là bị chặn thật (tránh biến mất nhầm do sai số nhỏ)
            if (this.stuckTicks >= 4) {
                this.remove();
                return;
            }
        } else {
            this.stuckTicks = 0;
        }

        // gió đổi hướng chậm rãi, tạo cảm giác cánh hoa trôi bồng bềnh theo luồng gió
        if (this.age % 15 == 0) {
            this.windX += (this.random.nextDouble() - 0.5) * 0.01;
            this.windZ += (this.random.nextDouble() - 0.5) * 0.01;

            this.windX = Math.max(-0.045, Math.min(0.045, this.windX));
            this.windZ = Math.max(-0.045, Math.min(0.045, this.windZ));
        }

        // kéo vận tốc ngang về phía hướng gió hiện tại thay vì dao động ngẫu nhiên đột ngột
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
    public ParticleRenderType getRenderType() {
        // Kept as NO_RENDER so ParticleEngine's own buffered batching never
        // touches this particle -- we draw it ourselves, see class javadoc.
        return ParticleRenderType.NO_RENDER;
    }

    @Override
    public void render(VertexConsumer buffer, Camera camera, float partialTick) {
        // Intentionally empty -- see class javadoc, drawing happens via renderAllManually().
    }

    /** Called from FlamboyantBloomRenderEvents at AFTER_PARTICLES. */
    public static void renderAllManually(Matrix4f pose, float partialTick) {
        if (ACTIVE.isEmpty()) {
            return;
        }

        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        Quaternionf camRotation = camera.rotation();

        for (GoldenFlamboyantPetalParticle particle : new ArrayList<>(ACTIVE)) {
            particle.drawSelf(camera, camRotation, pose, partialTick);
        }
    }

    private void drawSelf(Camera camera, Quaternionf camRotation, Matrix4f pose, float partialTick) {
        double camX = camera.getPosition().x;
        double camY = camera.getPosition().y;
        double camZ = camera.getPosition().z;
        float relX = (float) (Mth.lerp(partialTick, this.xo, this.x) - camX);
        float relY = (float) (Mth.lerp(partialTick, this.yo, this.y) - camY);
        float relZ = (float) (Mth.lerp(partialTick, this.zo, this.z) - camZ);
        float interpolatedRoll = Mth.lerp(partialTick, this.oRoll, this.roll);
        float u0 = this.getU0(), u1 = this.getU1(), v0 = this.getV0(), v1 = this.getV1();

        // 1. Normal appearance -- straight to whatever's currently bound
        //    (main target, we're called while level rendering is still
        //    active), full color/alpha as painted.
        drawBillboard(pose, relX, relY, relZ, interpolatedRoll, this.quadSize, u0, u1, v0, v1, camRotation, 255, 255, 255, 255);

        // 2. Bloom draw -- deferred into BloomRenderer's queue. Same SIZE
        //    as the normal sprite (a smaller copy caused a visible
        //    scale mismatch against the real texture -- see chat), just
        //    tinted/blended softer via alpha and the blur pass itself.
        BloomRenderer.queueBloomDraw(BloomRenderer.Channel.TIGHT, () -> drawBillboard(
                BloomRenderer.getFlushPose(), relX, relY, relZ, interpolatedRoll, this.quadSize, u0, u1, v0, v1, camRotation,
                255, 230, 160, 190
        ));
    }

    private static void drawBillboard(
            Matrix4f pose, float x, float y, float z, float roll, float size,
            float u0, float u1, float v0, float v1, Quaternionf camRotation,
            int r, int g, int b, int a
    ) {
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        RenderSystem.disableCull();

        Quaternionf rotation = new Quaternionf(camRotation);
        if (roll != 0.0f) {
            rotation.mul(new Quaternionf().rotationZ(roll));
        }

        Vector3f[] corners = new Vector3f[]{
                new Vector3f(-1.0f, -1.0f, 0.0f),
                new Vector3f(-1.0f, 1.0f, 0.0f),
                new Vector3f(1.0f, 1.0f, 0.0f),
                new Vector3f(1.0f, -1.0f, 0.0f)
        };
        float[][] uv = {{u0, v1}, {u0, v0}, {u1, v0}, {u1, v1}};

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

        for (int i = 0; i < corners.length; i++) {
            Vector3f corner = corners[i];
            corner.rotate(rotation);
            corner.mul(size);
            buffer.vertex(pose, x + corner.x, y + corner.y, z + corner.z)
                    .uv(uv[i][0], uv[i][1])
                    .color(r, g, b, a)
                    .endVertex();
        }

        com.mojang.blaze3d.vertex.BufferUploader.drawWithShader(buffer.end());
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }
}
