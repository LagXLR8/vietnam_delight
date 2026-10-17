package net.huwng.vietnamdelight.rope;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;

import java.util.*;

public class RopeConnectionManager {
    private static final Map<net.minecraft.resources.ResourceKey<Level>, List<RopeConnection>> CONNECTIONS = new HashMap<>();

    public static List<RopeConnection> getConnections(Level level) {
        return CONNECTIONS.computeIfAbsent(level.dimension(), k -> new ArrayList<>());
    }

    public static void addConnection(Level level, RopeConnection connection) {
        getConnections(level).add(connection);
    }

    public static void removeConnection(Level level, RopeConnection connection) {
        getConnections(level).remove(connection);
    }

    public static RopeConnection findNearest(Level level, Vec3 pos, double radius) {
        return getConnections(level).stream()
                .filter(c -> c.distanceTo(pos) <= radius)
                .findFirst()
                .orElse(null);
    }

    public static void clear(Level level) {
        CONNECTIONS.remove(level.dimension());
    }
	
	 public static CompoundTag serializeConnectionsForDimension(Level level) {
	    CompoundTag root = new CompoundTag();
	    List<RopeConnection> conns = getConnections(level);
	    ListTag arr = new ListTag();
	    for (RopeConnection c : conns) {
	        CompoundTag ct = new CompoundTag();
	        ct.putLong("a", c.nodeA.asLong());
	        ct.putLong("b", c.nodeB.asLong());
	
	        ListTag lanternArr = new ListTag();
	        for (RopeConnection.LanternEntry le : c.lanterns) {
	            CompoundTag leTag = new CompoundTag();
	            leTag.putFloat("t", le.t);
	            if (le.state != null && le.state.getBlock() != null) {
	                ResourceLocation rl = BuiltInRegistries.BLOCK.getKey(le.state.getBlock());
	                if (rl == null) rl = new ResourceLocation("minecraft", "air");
	                leTag.putString("block", rl.toString());
	            } else {
	                leTag.putString("block", "minecraft:air");
	            }
	            lanternArr.add(leTag);
	        }
	        ct.put("lanterns", lanternArr);
	        arr.add(ct);
	    }
	    root.put("conns", arr);
	    return root;
	}
	
	public static void deserializeConnectionsForDimension(Level level, CompoundTag root) {
	    getConnections(level).clear();
	    if (!root.contains("conns")) return;
	    ListTag arr = root.getList("conns", 10);
	    for (int i = 0; i < arr.size(); i++) {
	        CompoundTag ct = arr.getCompound(i);
	        BlockPos a = BlockPos.of(ct.getLong("a"));
	        BlockPos b = BlockPos.of(ct.getLong("b"));
	        RopeConnection conn = new RopeConnection(a, b);
	        ListTag lanternArr = ct.getList("lanterns", 10);
	        for (int j = 0; j < lanternArr.size(); j++) {
	            CompoundTag leTag = lanternArr.getCompound(j);
	            float t = leTag.getFloat("t");
	            String blk = leTag.getString("block");
	            try {
	                Block block = BuiltInRegistries.BLOCK.get(new ResourceLocation(blk));
	                if (block != null) conn.lanterns.add(new RopeConnection.LanternEntry(t, block.defaultBlockState()));
	                else conn.lanterns.add(new RopeConnection.LanternEntry(t, null));
	            } catch (Exception ex) {
	                conn.lanterns.add(new RopeConnection.LanternEntry(t, null));
	            }
	        }
	        getConnections(level).add(conn);
	    }
	}
	    /** Tìm connection chứa lantern gần hitPos */
    public static RopeConnection getLanternAt(Level level, BlockPos pos, Vec3 hitPos) {
        for (RopeConnection conn : getConnections(level)) {
            int idx = conn.findNearestLanternIndex(hitPos, 0.5);
            if (idx != -1) {
                return conn;
            }
        }
        return null;
    }

    /** Xoá lantern gần vị trí hit */
    public static void removeLantern(Level level, RopeConnection conn, Vec3 hitPos) {
        int idx = conn.findNearestLanternIndex(hitPos, 0.5);
        if (idx != -1) {
            conn.removeLanternAt(idx);
            sync(level);
        }
    }

    /** Đồng bộ xuống client + lưu lại */
    public static void sync(Level level) {
        if (level.isClientSide) return;

        var conns = getConnections(level);
        var packet = new net.huwng.vietnamdelight.network.SyncRopeConnectionsPacket(level.dimension(), conns);
        net.huwng.vietnamdelight.network.ModNetworking.CHANNEL.send(
            net.minecraftforge.network.PacketDistributor.ALL.noArg(),
            packet
        );

        if (level instanceof ServerLevel server) {
            net.huwng.vietnamdelight.rope.RopeSavedData saved = net.huwng.vietnamdelight.rope.RopeSavedData.get(server);
            saved.setData(serializeConnectionsForDimension(level));
            saved.setDirty();
        }
    }
}
