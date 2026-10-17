package net.huwng.vietnamdelight.client.bloom;

import net.huwng.vietnamdelight.init.VietnamDelightModBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.ArrayList;
import java.util.List;

/**
 * blooming_golden_flamboyant_leaves, flowering_golden_flamboyant_leaves,
 * golden_flamboyant_flower, golden_flamboyant_petals and
 * golden_flamboyant_sapling are all plain Blocks (no BlockEntity) rendered
 * through the normal chunk mesh -- there's no per-instance render hook we
 * can piggyback on like GlowBlockEntityRenderer does for a BlockEntity.
 *
 * Instead: periodically (every SCAN_INTERVAL_TICKS) walk the LOADED CHUNK
 * SECTIONS within SCAN_RADIUS_CHUNKS of the player and record matching
 * block positions, each tagged with a GlowShape so the renderer can draw
 * something that actually resembles the block instead of one generic cube
 * for everything (petals are a thin slab, flower is a small attached
 * blob, leaves are a soft inset cube).
 *
 * This is a "V1": a real block-update-driven cache (add/remove entries as
 * blocks actually change nearby, instead of re-scanning wholesale) would
 * scale better for huge canopies, but needs hooking client block-change
 * notifications, which is more invasive. Re-scanning once a second is a
 * reasonable tradeoff for a first pass -- tune SCAN_RADIUS_CHUNKS /
 * SCAN_INTERVAL_TICKS if it's too heavy or too short-ranged for your trees.
 */
public final class FlamboyantGlowScanner {

    public enum GlowShape { FLOWER, PETALS, SAPLING }

    public record GlowSource(BlockPos pos, GlowShape shape, net.minecraft.core.Direction facing) {
    }

    private static final int SCAN_RADIUS_CHUNKS = 2; // ~32 blocks horizontally
    private static final int SCAN_Y_RADIUS = 24; // vertical range around the player -- trees aren't THAT tall
    private static final int SCAN_INTERVAL_TICKS = 20; // once a second -- covers the WIDE area

    // NEAR scan: small radius around the player, redone every tick. This is
    // what actually kills the place/break delay -- the block the player is
    // standing next to gets picked up next tick instead of waiting up to
    // SCAN_INTERVAL_TICKS for the wide sweep to come back around. Cheap
    // enough to run every tick since the volume is tiny compared to the
    // wide scan's chunk-section sweep.
    private static final int NEAR_RADIUS = 6;

    private static List<GlowSource> wideSources = new ArrayList<>();
    private static List<GlowSource> nearSources = new ArrayList<>();
    private static List<GlowSource> cachedSources = new ArrayList<>();
    private static int tickCounter = SCAN_INTERVAL_TICKS; // scan immediately on first tick

    private FlamboyantGlowScanner() {
    }

    public static void onClientTick() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) {
            return;
        }

        rescanNear(mc);

        tickCounter++;
        if (tickCounter < SCAN_INTERVAL_TICKS) {
            return;
        }
        tickCounter = 0;
        rescan(mc);
    }

    /** Small-radius scan around the player, run every tick (see NEAR_RADIUS javadoc above). */
    private static void rescanNear(Minecraft mc) {
        List<GlowSource> found = new ArrayList<>();
        BlockPos center = mc.player.blockPosition();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        int minY = Math.max(mc.level.getMinBuildHeight(), center.getY() - NEAR_RADIUS);
        int maxY = Math.min(mc.level.getMaxBuildHeight(), center.getY() + NEAR_RADIUS);

        for (int x = -NEAR_RADIUS; x <= NEAR_RADIUS; x++) {
            for (int z = -NEAR_RADIUS; z <= NEAR_RADIUS; z++) {
                for (int y = minY; y < maxY; y++) {
                    pos.set(center.getX() + x, y, center.getZ() + z);
                    if (!mc.level.isLoaded(pos)) {
                        continue;
                    }
                    BlockState state = mc.level.getBlockState(pos);
                    GlowShape shape = classify(state);
                    if (shape != null) {
                        found.add(new GlowSource(pos.immutable(), shape, facingOf(state, shape)));
                    }
                }
            }
        }

        nearSources = found;
        rebuildCache();
    }

    private static void rebuildCache() {
        List<GlowSource> merged = new ArrayList<>(wideSources.size() + nearSources.size());
        merged.addAll(wideSources);
        // near scan is authoritative for anything inside its radius -- drop
        // wide-scan duplicates for the same position so a block removed a
        // moment ago (still lingering in the stale wide cache) doesn't
        // double-draw or fight with the fresh near-scan result.
        for (GlowSource near : nearSources) {
            merged.removeIf(existing -> existing.pos().equals(near.pos()));
        }
        merged.addAll(nearSources);
        cachedSources = merged;
    }

    private static net.minecraft.core.Direction facingOf(BlockState state, GlowShape shape) {
        return shape == GlowShape.FLOWER
                ? state.getValue(net.huwng.vietnamdelight.block.GoldenFlamboyantFlowerBlock.FACING)
                : null;
    }

    private static void rescan(Minecraft mc) {
        List<GlowSource> found = new ArrayList<>();
        ChunkPos centerChunk = new ChunkPos(mc.player.blockPosition());
        int playerY = mc.player.blockPosition().getY();

        for (int cx = -SCAN_RADIUS_CHUNKS; cx <= SCAN_RADIUS_CHUNKS; cx++) {
            for (int cz = -SCAN_RADIUS_CHUNKS; cz <= SCAN_RADIUS_CHUNKS; cz++) {
                LevelChunk chunk = mc.level.getChunkSource().getChunk(centerChunk.x + cx, centerChunk.z + cz, false);
                if (chunk == null) {
                    continue; // not loaded, skip rather than force-load it
                }
                scanChunk(chunk, playerY, found);
            }
        }

        wideSources = found;
        rebuildCache();
    }

    private static void scanChunk(LevelChunk chunk, int playerY, List<GlowSource> found) {
        int minY = Math.max(chunk.getMinBuildHeight(), playerY - SCAN_Y_RADIUS);
        int maxY = Math.min(chunk.getMaxBuildHeight(), playerY + SCAN_Y_RADIUS);
        int baseX = chunk.getPos().getMinBlockX();
        int baseZ = chunk.getPos().getMinBlockZ();

        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = minY; y < maxY; y++) {
                    pos.set(baseX + x, y, baseZ + z);
                    BlockState state = chunk.getBlockState(pos);
                    GlowShape shape = classify(state);
                    if (shape != null) {
                        found.add(new GlowSource(pos.immutable(), shape, facingOf(state, shape)));
                    }
                }
            }
        }
    }

    private static GlowShape classify(BlockState state) {
        if (state.is(VietnamDelightModBlocks.GOLDEN_FLAMBOYANT_FLOWER.get())) {
            return GlowShape.FLOWER;
        }
        if (state.is(VietnamDelightModBlocks.GOLDEN_FLAMBOYANT_PETALS.get())) {
            return GlowShape.PETALS;
        }
        if (state.is(VietnamDelightModBlocks.GOLDEN_FLAMBOYANT_SAPLING.get())) {
            return GlowShape.SAPLING;
        }
        return null;
    }

    public static List<GlowSource> getCachedSources() {
        return cachedSources;
    }
}
