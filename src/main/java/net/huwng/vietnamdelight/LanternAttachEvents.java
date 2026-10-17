package net.huwng.vietnamdelight;

import net.huwng.vietnamdelight.network.ModNetworking;
import net.huwng.vietnamdelight.network.SyncRopeConnectionsPacket;
import net.huwng.vietnamdelight.rope.RopeConnection;
import net.huwng.vietnamdelight.rope.RopeConnectionManager;
import net.huwng.vietnamdelight.rope.RopeSavedData;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.phys.Vec3;

import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

@Mod.EventBusSubscriber(modid = "vietnam_delight")
public class LanternAttachEvents {

    // Tìm connection gần theo hướng nhìn (raymarch)
    private static RopeConnection findConnectionAlongRay(Player player, Level level, double range, double step, double maxDist) {
        Vec3 eye = player.getEyePosition(1.0F);
        Vec3 look = player.getLookAngle();

        RopeConnection best = null;
        double bestDist = Double.MAX_VALUE;

        int steps = (int) (range / step);
        for (int i = 0; i <= steps; i++) {
            Vec3 point = eye.add(look.x * i * step, look.y * i * step, look.z * i * step);
            RopeConnection conn = RopeConnectionManager.findNearest(level, point, maxDist);
            if (conn != null) {
                double t = conn.getNearestT(point);
                double dist = point.distanceTo(conn.getPoint(t));
                if (dist < bestDist) {
                    best = conn;
                    bestDist = dist;
                }
            }
        }
        return best;
    }

    // Chuột phải bằng lantern -> đặt lên dây
    @SubscribeEvent
    public static void onRightClick(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        Level level = player.level();
        ItemStack held = event.getItemStack();

        // chỉ xử lý nếu là lantern block
        Item it = held.getItem();
        if (!(it instanceof BlockItem blockItem)) return;
        Block block = blockItem.getBlock();
        if (!(block instanceof LanternBlock)) return;

        if (!level.isClientSide) {
            RopeConnection nearest = findConnectionAlongRay(player, level, 6.0, 0.15, 0.6);
            if (nearest != null) {
                Vec3 eye = player.getEyePosition(1.0f);
                Vec3 look = player.getLookAngle();
                Vec3 target = eye.add(look.scale(6.0));

                double t = nearest.getNearestT(target);
                BlockState state = block.defaultBlockState();

                // kiểm tra spacing + số lượng tối đa
                if (nearest.addLantern((float) t, state, 1, 16)) {
                    if (!player.isCreative()) held.shrink(1);

                    // sync
                    var conns = RopeConnectionManager.getConnections(level);
                    SyncRopeConnectionsPacket packet = new SyncRopeConnectionsPacket(level.dimension(), conns);
                    ModNetworking.CHANNEL.send(PacketDistributor.ALL.noArg(), packet);

                    // persist
                    if (level instanceof ServerLevel server) {
                        RopeSavedData saved = RopeSavedData.get(server);
                        saved.setData(RopeConnectionManager.serializeConnectionsForDimension(level));
                        saved.setDirty();
                    }

                    player.displayClientMessage(Component.literal("Lantern placed")
                        .withStyle(ChatFormatting.GREEN), true);

                    event.setCanceled(true);
                    event.setCancellationResult(InteractionResult.SUCCESS);
                } else {
                    player.displayClientMessage(Component.literal("Too close to another lantern")
                        .withStyle(ChatFormatting.RED), true);
                }
            }
        }
    }

	// Chuột phải tay không -> xóa lantern gần nhất
	@SubscribeEvent
	public static void onRightClickEmpty(PlayerInteractEvent.RightClickEmpty event) {
	    Player player = event.getEntity();
	    Level level = player.level();
	    if (level == null) return;
	
	    // chỉ khi tay không
	    if (!player.getMainHandItem().isEmpty()) return;
	
	    if (!level.isClientSide) {
	        RopeConnection conn = findConnectionAlongRay(player, level, 6.0, 0.15, 0.6);
	        if (conn != null) {
	            Vec3 eye = player.getEyePosition(1.0F);
	            Vec3 look = player.getLookAngle();
	            Vec3 sample = eye.add(look.scale(6.0));
	            int idx = conn.findNearestLanternIndex(sample, 0.6);
	            if (idx != -1) {
	                BlockState st = conn.removeLanternAt(idx);
	                if (st != null) {
	                    Block.popResource(level, player.blockPosition(), new ItemStack(st.getBlock()));
	                }
	
	                // sync
	                var conns = RopeConnectionManager.getConnections(level);
	                SyncRopeConnectionsPacket packet = new SyncRopeConnectionsPacket(level.dimension(), conns);
	                ModNetworking.CHANNEL.send(PacketDistributor.ALL.noArg(), packet);
	
	                // persist
	                if (level instanceof ServerLevel server) {
	                    RopeSavedData saved = RopeSavedData.get(server);
	                    saved.setData(RopeConnectionManager.serializeConnectionsForDimension(level));
	                    saved.setDirty();
	                }
	
	                player.displayClientMessage(
	                    Component.literal("Lantern removed").withStyle(ChatFormatting.YELLOW), true
	                );
	
	                event.setCanceled(true);
	                event.setCancellationResult(InteractionResult.SUCCESS);
	            }
	        }
	    }
	}
}
