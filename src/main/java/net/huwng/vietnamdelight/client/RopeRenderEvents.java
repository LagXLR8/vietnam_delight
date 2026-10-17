package net.huwng.vietnamdelight.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.util.Mth;
import net.huwng.vietnamdelight.rope.RopeConnection;
import net.huwng.vietnamdelight.rope.RopeConnection.LanternEntry;
import net.huwng.vietnamdelight.rope.RopeConnectionManager;
import net.huwng.vietnamdelight.init.VietnamDelightModBlocks;

@Mod.EventBusSubscriber(modid = "vietnam_delight", value = Dist.CLIENT)
public class RopeRenderEvents {

    // Ghost node client-only
    public static BlockPos ghostFirstNode = null;

    private static final ResourceLocation ROPE_TEX = new ResourceLocation("vietnam_delight", "textures/entities/rope.png");

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_SOLID_BLOCKS) return;

        Minecraft mc = Minecraft.getInstance();
        Level level = mc.level;
        if (level == null) return;

        PoseStack poseStack = event.getPoseStack();
        Camera camera = mc.gameRenderer.getMainCamera();
        double camX = camera.getPosition().x;
        double camY = camera.getPosition().y;
        double camZ = camera.getPosition().z;
        MultiBufferSource.BufferSource buffer = mc.renderBuffers().bufferSource();

        BlockRenderDispatcher dispatcher = mc.getBlockRenderer();
        BlockState lanternState = VietnamDelightModBlocks.PAPER_LANTERN.get().defaultBlockState();

        // Render tất cả Rope connections
        for (RopeConnection conn : RopeConnectionManager.getConnections(level)) {
            renderConnection(conn, poseStack, buffer, camX, camY, camZ, dispatcher, lanternState);
        }

        // Render ghost rope
        if (ghostFirstNode != null && mc.player != null) {
            Vec3 start = Vec3.atCenterOf(ghostFirstNode);
            Vec3 end = mc.player.getEyePosition(event.getPartialTick());
            if (start.closerThan(end, 15.0)) {
                RopeConnection ghostConn = new RopeConnection(ghostFirstNode, BlockPos.containing(end));
                renderConnection(ghostConn, poseStack, buffer, camX, camY, camZ, dispatcher, lanternState, true);
            }
        }

        buffer.endBatch();
    }

    private static void renderConnection(RopeConnection conn,
                                         PoseStack poseStack,
                                         MultiBufferSource buffer,
                                         double camX, double camY, double camZ,
                                         BlockRenderDispatcher dispatcher,
                                         BlockState lanternState) {
        renderConnection(conn, poseStack, buffer, camX, camY, camZ, dispatcher, lanternState, false);
    }

	private static void renderConnection(RopeConnection conn,
	                                     PoseStack poseStack,
	                                     MultiBufferSource buffer,
	                                     double camX, double camY, double camZ,
	                                     BlockRenderDispatcher dispatcher,
	                                     BlockState lanternState,
	                                     boolean ghost) {
	
	    VertexConsumer ropeBuffer = buffer.getBuffer(RenderType.entityTranslucent(ROPE_TEX));
	    int segments = 16;
	    float ropeWidth = 0.05f;
	
	    Vec3 start = Vec3.atCenterOf(conn.nodeA);
	    Vec3 end = Vec3.atCenterOf(conn.nodeB);
	
	    // render dây cong
	    for (int i = 0; i < segments; i++) {
	        float t1 = i / (float)segments;
	        float t2 = (i + 1) / (float)segments;
	
	        Vec3 p1 = new Vec3(
	                Mth.lerp(t1, start.x, end.x),
	                Mth.lerp(t1, start.y, end.y) - Math.sin(Math.PI * t1) * 0.5,
	                Mth.lerp(t1, start.z, end.z)
	        );
	
	        Vec3 p2 = new Vec3(
	                Mth.lerp(t2, start.x, end.x),
	                Mth.lerp(t2, start.y, end.y) - Math.sin(Math.PI * t2) * 0.5,
	                Mth.lerp(t2, start.z, end.z)
	        );
	
	        Vec3 dir = p2.subtract(p1).normalize();
	        Vec3 side1 = dir.cross(new Vec3(0, 1, 0)).normalize().scale(ropeWidth);
	        Vec3 side2 = dir.cross(side1).normalize().scale(ropeWidth);
	
	        drawQuad(ropeBuffer, poseStack, p1, p2, side1, ghost, camX, camY, camZ);
	        drawQuad(ropeBuffer, poseStack, p1, p2, side2, ghost, camX, camY, camZ);
	    }
	
	// Render lanterns trên dây
	if (!conn.lanterns.isEmpty()) {
	    for (RopeConnection.LanternEntry entry : conn.lanterns) {
	        if (!(entry.state.getBlock() instanceof LanternBlock)) continue;
	
	        Vec3 lp = conn.getPoint(entry.t);
	
	        // Treo lantern thẳng dưới dây (XZ giữ nguyên, Y hạ xuống)
	        double sag = Math.sin(Math.PI * entry.t) * 0.3;
	        Vec3 lanternPos = new Vec3(lp.x - 0.25 - sag, lp.y - 0.75 - sag, lp.z - 0.25 - sag);
	
	        poseStack.pushPose();
	        // Dịch đến vị trí dưới dây (trừ camera)
	        poseStack.translate(lanternPos.x - camX, lanternPos.y - camY, lanternPos.z - camZ);
	        poseStack.scale(0.8f, 0.8f, 0.8f);
	
	        // Render block lantern
	        dispatcher.renderSingleBlock(
	                entry.state,
	                poseStack,
	                buffer,
	                15728880,
	                OverlayTexture.NO_OVERLAY
	        );
	
	        poseStack.popPose();
	    }
	}

}
	private static void drawQuad(VertexConsumer buffer, PoseStack stack,
	                             Vec3 p1, Vec3 p2, Vec3 side,
	                             boolean ghost, double camX, double camY, double camZ) {
	    addVertex(buffer, stack, p1.subtract(side), ghost, camX, camY, camZ, 0f, 0f);
	    addVertex(buffer, stack, p1.add(side),      ghost, camX, camY, camZ, 1f, 0f);
	    addVertex(buffer, stack, p2.add(side),      ghost, camX, camY, camZ, 1f, 1f);
	    addVertex(buffer, stack, p2.subtract(side), ghost, camX, camY, camZ, 0f, 1f);
	}
	
	private static void addVertex(VertexConsumer buffer, PoseStack stack, Vec3 pos,
	                              boolean ghost, double camX, double camY, double camZ,
	                              float u, float v) {
	    buffer.vertex(stack.last().pose(),
	                    (float) (pos.x - camX),
	                    (float) (pos.y - camY),
	                    (float) (pos.z - camZ))
	            .color(1f, 1f, 1f, ghost ? 0.7f : 1f)
	            .uv(u, v)
	            .overlayCoords(OverlayTexture.NO_OVERLAY)
	            .uv2(15728880)
	            .normal(0, 1, 0)
	            .endVertex();
	}
}
