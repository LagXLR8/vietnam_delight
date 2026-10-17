package net.huwng.vietnamdelight.network;

import net.huwng.vietnamdelight.rope.RopeConnection;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.network.NetworkEvent;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class SyncRopeConnectionsPacket {
    private final ResourceKey<Level> dimension;
    private final List<RopeConnection> connections;

    public SyncRopeConnectionsPacket(ResourceKey<Level> dim, List<RopeConnection> conns) {
        this.dimension = dim;
        this.connections = conns;
    }

    // --- ENCODE ---
    public static void encode(SyncRopeConnectionsPacket msg, FriendlyByteBuf buf) {
        buf.writeResourceLocation(msg.dimension.location());
        buf.writeInt(msg.connections.size());
        for (RopeConnection c : msg.connections) {
            buf.writeBlockPos(c.nodeA);
            buf.writeBlockPos(c.nodeB);
            buf.writeInt(c.lanterns.size());
            for (RopeConnection.LanternEntry e : c.lanterns) {
                buf.writeFloat(e.t);
                boolean has = e.state != null && e.state.getBlock() != null;
                buf.writeBoolean(has);
                if (has) {
                    ResourceLocation rl = BuiltInRegistries.BLOCK.getKey(e.state.getBlock());
                    if (rl == null) rl = new ResourceLocation("minecraft", "air");
                    buf.writeResourceLocation(rl);
                }
            }
        }
    }

    // --- DECODE ---
    public static SyncRopeConnectionsPacket decode(FriendlyByteBuf buf) {
        ResourceKey<Level> dim = ResourceKey.create(Registries.DIMENSION, buf.readResourceLocation());
        int count = buf.readInt();
        List<RopeConnection> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            BlockPos a = buf.readBlockPos();
            BlockPos b = buf.readBlockPos();
            RopeConnection conn = new RopeConnection(a, b);

            int lsize = buf.readInt();
            for (int j = 0; j < lsize; j++) {
                float t = buf.readFloat();
                boolean has = buf.readBoolean();
                if (has) {
                    ResourceLocation rl = buf.readResourceLocation();
                    try {
                        Block blk = BuiltInRegistries.BLOCK.get(rl);
                        if (blk != null) {
                            conn.lanterns.add(new RopeConnection.LanternEntry(t, blk.defaultBlockState()));
                        } else {
                            conn.lanterns.add(new RopeConnection.LanternEntry(t, null));
                        }
                    } catch (Exception ex) {
                        conn.lanterns.add(new RopeConnection.LanternEntry(t, null));
                    }
                } else {
                    conn.lanterns.add(new RopeConnection.LanternEntry(t, null));
                }
            }
            list.add(conn);
        }
        return new SyncRopeConnectionsPacket(dim, list);
    }

    // --- HANDLE ---
    public static void handle(SyncRopeConnectionsPacket msg, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            // Sử dụng DistExecutor để gọi client handler một cách an toàn
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, 
                () -> () -> ClientPacketHandler.handleRopeSync(msg.dimension, msg.connections));
        });
        context.setPacketHandled(true);
    }
}