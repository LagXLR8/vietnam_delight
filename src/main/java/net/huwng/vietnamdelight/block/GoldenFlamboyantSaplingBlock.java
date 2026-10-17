package net.huwng.vietnamdelight.block;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.core.BlockPos;

import net.huwng.vietnamdelight.world.feature.GoldenFlamboyantTreeGrower;
import net.huwng.vietnamdelight.init.VietnamDelightModItems;
import net.huwng.vietnamdelight.block.entity.BloomingGoldenFlamboyantLeavesBlockEntity;
import net.huwng.vietnamdelight.block.entity.FloweringGoldenFlamboyantLeavesBlockEntity;

import java.util.Map;

public class GoldenFlamboyantSaplingBlock extends SaplingBlock {

    // How far to look for an existing blooming/flowering golden flamboyant
    // tree before letting a sapling grow into a new one. Kept as BLOCKS
    // (not chunks) so the intent stays readable; converted to a chunk
    // search radius in hasNearbyBloom().
    private static final int BLOOM_CHECK_RADIUS = 100;

    public GoldenFlamboyantSaplingBlock() {
        super(new GoldenFlamboyantTreeGrower(),
            BlockBehaviour.Properties.of()
                .mapColor(net.minecraft.world.level.material.MapColor.PLANT)
                .instrument(NoteBlockInstrument.BASEDRUM)
                .sound(SoundType.GRASS)
                .instabreak()
                .noCollission()
                .noOcclusion()
                .isRedstoneConductor((bs, br, bp) -> false)
                .randomTicks()
                .pushReaction(PushReaction.DESTROY)
        );
    }

    @Override
    public void advanceTree(ServerLevel level, BlockPos pos, BlockState state, RandomSource random) {
        if (hasNearbyBloom(level, pos)) {
            // Another golden flamboyant is already blooming/flowering
            // within range -- don't let a second one take root, wither
            // into a dead bush instead.
            level.setBlock(pos, Blocks.DEAD_BUSH.defaultBlockState(), 3);
            return;
        }
        super.advanceTree(level, pos, state, random);
    }

    /**
     * Looks for a blooming_golden_flamboyant_leaves or
     * flowering_golden_flamboyant_leaves BlockEntity within
     * BLOOM_CHECK_RADIUS blocks. Both are BlockEntity-backed, so this walks
     * each nearby chunk's already-indexed BlockEntity map instead of
     * brute-force scanning every block position in a 100-block sphere
     * (which at this radius would be millions of getBlockState calls) --
     * cheap even though it only runs on the rare occasion a sapling
     * actually attempts to grow.
     */
    private static boolean hasNearbyBloom(ServerLevel level, BlockPos origin) {
        long radiusSq = (long) BLOOM_CHECK_RADIUS * BLOOM_CHECK_RADIUS;
        int chunkRadius = (BLOOM_CHECK_RADIUS >> 4) + 1;
        ChunkPos originChunk = new ChunkPos(origin);

        for (int cx = -chunkRadius; cx <= chunkRadius; cx++) {
            for (int cz = -chunkRadius; cz <= chunkRadius; cz++) {
                int chunkX = originChunk.x + cx;
                int chunkZ = originChunk.z + cz;
                if (!level.hasChunk(chunkX, chunkZ)) {
                    continue; // don't force-load distant chunks just to check
                }
                LevelChunk chunk = level.getChunk(chunkX, chunkZ);
                for (Map.Entry<BlockPos, BlockEntity> entry : chunk.getBlockEntities().entrySet()) {
                    BlockEntity be = entry.getValue();
                    if (!(be instanceof BloomingGoldenFlamboyantLeavesBlockEntity)
                            && !(be instanceof FloweringGoldenFlamboyantLeavesBlockEntity)) {
                        continue;
                    }
                    if (entry.getKey().distSqr(origin) <= radiusSq) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
        return true;
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
        return 0;
    }

    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return box(2, 0, 2, 14, 12, 14);
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player) {
        return new ItemStack(VietnamDelightModItems.GOLDEN_FLAMBOYANT_SAPLING.get());
    }

	@Override
	public int getFireSpreadSpeed(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		return 60;
	}

	@Override
	public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		return 100;
	}
}

