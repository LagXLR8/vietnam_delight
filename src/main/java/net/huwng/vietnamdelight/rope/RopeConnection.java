package net.huwng.vietnamdelight.rope;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class RopeConnection {
    public final BlockPos nodeA;
    public final BlockPos nodeB;

    public static class LanternEntry {
        public float t;
        public BlockState state;

        public LanternEntry(float t, BlockState state) {
            this.t = t;
            this.state = state;
        }
    }

    public final List<LanternEntry> lanterns = new ArrayList<>();

    public RopeConnection(BlockPos nodeA, BlockPos nodeB) {
        this.nodeA = nodeA;
        this.nodeB = nodeB;
    }

    /** Vị trí có võng dùng cho render dây */
    public Vec3 getPoint(double t) {
        Vec3 a = Vec3.atCenterOf(nodeA);
        Vec3 b = Vec3.atCenterOf(nodeB);
        double x = Mth.lerp(t, a.x, b.x);
        double y = Mth.lerp(t, a.y, b.y) - Math.sin(Math.PI * t) * 0.5; // võng nhẹ
        double z = Mth.lerp(t, a.z, b.z);
        return new Vec3(x, y, z);
    }

    /** Thêm lantern, có kiểm tra giới hạn */
    public boolean addLantern(float t, BlockState state, double minSpacing, int maxCount) {
        if (lanterns.size() >= maxCount) return false; // giới hạn số lượng

        Vec3 newPos = getPoint(t);
        for (LanternEntry e : lanterns) {
            Vec3 existing = getPoint(e.t);
            if (existing.distanceTo(newPos) < minSpacing) {
                return false; // quá gần
            }
        }
        lanterns.add(new LanternEntry(t, state));
        return true;
    }
    
	public boolean canPlaceLantern(double t, double minSpacing) {
	    Vec3 newPos = getPointAt(t);
	    for (LanternEntry e : lanterns) {
	        Vec3 lp = getPointAt(e.t);
	        if (lp.distanceTo(newPos) < minSpacing) {
	            return false; // quá gần, không cho đặt
	        }
	    }
	    return true;
	}

    /** Vị trí tuyến tính giữa 2 node (không võng) - dùng cho tính khoảng cách chính xác */
    public Vec3 getPointAt(double t) {
        Vec3 a = Vec3.atCenterOf(nodeA);
        Vec3 b = Vec3.atCenterOf(nodeB);
        return a.lerp(b, t);
    }
public Vec3 getPoint(float t) {
    Vec3 a = Vec3.atCenterOf(nodeA);
    Vec3 b = Vec3.atCenterOf(nodeB);
    return a.lerp(b, t);
}

    public double getNearestT(Vec3 pos) {
        double bestT = 0;
        double minDist = Double.MAX_VALUE;
        for (int i = 0; i <= 200; i++) {
            double t = i / 200.0;
            Vec3 p = getPointAt(t); // dùng tuyến tính thay vì getPoint
            double dist = pos.distanceToSqr(p);
            if (dist < minDist) {
                minDist = dist;
                bestT = t;
            }
        }
        return bestT;
    }

    public double distanceTo(Vec3 pos) {
        double minDist = Double.MAX_VALUE;
        for (int i = 0; i <= 100; i++) {
            double t = i / 100.0;
            Vec3 p = getPointAt(t); // tuyến tính
            double dist = pos.distanceTo(p);
            if (dist < minDist) minDist = dist;
        }
        return minDist;
    }

    public int findNearestLanternIndex(Vec3 pos, double radius) {
        double best = Double.MAX_VALUE;
        int bestIdx = -1;
        for (int i = 0; i < lanterns.size(); i++) {
            Vec3 lp = getPointAt(lanterns.get(i).t); // tuyến tính
            double d = lp.distanceTo(pos);
            if (d < radius && d < best) {
                best = d;
                bestIdx = i;
            }
        }
        return bestIdx;
    }

    /** Remove lantern at index and return its BlockState, or null */
    public BlockState removeLanternAt(int idx) {
        if (idx < 0 || idx >= lanterns.size()) return null;
        LanternEntry e = lanterns.remove(idx);
        return e.state;
    }
}
