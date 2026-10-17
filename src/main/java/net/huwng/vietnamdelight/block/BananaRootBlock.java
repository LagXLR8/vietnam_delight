package net.huwng.vietnamdelight.block;

import org.checkerframework.checker.units.qual.s;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.core.BlockPos;

import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.huwng.vietnamdelight.init.VietnamDelightModBlocks;
import net.huwng.vietnamdelight.block.BananaSuckerBlock;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.huwng.vietnamdelight.init.VietnamDelightModItems;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.BlockHitResult;
import java.util.Random;

public class BananaRootBlock extends Block {
	public static final IntegerProperty BLOCKSTATE = IntegerProperty.create("blockstate", 0, 2);

	public BananaRootBlock() {
		super(BlockBehaviour.Properties.of().instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.GRAVEL).strength(0.5f).lightLevel(s -> (new Object() {
			public int getLightLevel() {
				if (s.getValue(BLOCKSTATE) == 1)
					return 0;
				if (s.getValue(BLOCKSTATE) == 2)
					return 0;
				return 0;
			}
		}.getLightLevel())));
	}

	@Override
	public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
		return 15;
	}
	
@Override
	public boolean isRandomlyTicking(BlockState state) {
	    return true;
	}
	
	@Override
	public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
	    int currentState = state.getValue(BLOCKSTATE);
	
	    if (currentState == 1) {
	        BlockState above = level.getBlockState(pos.above());
	        if (above.getBlock() == VietnamDelightModBlocks.BANANA_PSEUDOSTEM.get()) {
	            for (Direction dir : Direction.Plane.HORIZONTAL) {
	                if (random.nextInt(4) == 0) {
	                    BlockPos neighborPos = pos.relative(dir);
	                    BlockState neighborState = level.getBlockState(neighborPos);
	
	                    if (isSoilBlock(neighborState.getBlock()) && level.getBlockState(neighborPos.above()).isAir()) {
	                        level.setBlock(neighborPos, this.defaultBlockState().setValue(BLOCKSTATE, 2), 3);
	                    }
	                }
	            }
	        }
			    } else if (currentState == 2) {
		    BlockPos above = pos.above();
		    if (level.getBlockState(above).isAir()) {
		        int suckerState = random.nextInt(3);
		        BlockState sucker = VietnamDelightModBlocks.BANANA_SUCKER.get().defaultBlockState()
		            .setValue(BananaSuckerBlock.BLOCKSTATE, suckerState);
		        level.setBlock(above, sucker, 3);
		        level.setBlock(pos, this.defaultBlockState().setValue(BLOCKSTATE, 0), 3);
		    }
		}
	}
	
	private boolean isSoilBlock(Block block) {
	    return block == Blocks.DIRT ||
	           block == Blocks.GRASS_BLOCK ||
	           block == Blocks.FARMLAND ||
	           block == Blocks.COARSE_DIRT ||
	           block == Blocks.PODZOL ||
	           block == Blocks.ROOTED_DIRT;
	}
	
	@Override
	public void spawnAfterBreak(BlockState state, ServerLevel level, BlockPos pos, ItemStack tool, boolean dropExperience) {
	    super.spawnAfterBreak(state, level, pos, tool, dropExperience);
	
	    if (state.getValue(BLOCKSTATE) == 1) {
	        RandomSource random = level.getRandom();
	        int count = 1 + random.nextInt(2); // 1–2 rhizome
	        ItemStack rhizome = new ItemStack(VietnamDelightModItems.BANANA_RHIZOME.get(), count);
	        popResource(level, pos, rhizome);
	    }
	}
	
	@Override
public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                             InteractionHand hand, BlockHitResult hit) {
    ItemStack heldItem = player.getItemInHand(hand);
    if (!level.isClientSide && heldItem.getItem() instanceof HoeItem) {
        level.setBlock(pos, Blocks.DIRT.defaultBlockState(), 3);
        level.playSound(null, pos, SoundEvents.HOE_TILL, SoundSource.BLOCKS, 1.0F, 1.0F);
        if (!player.isCreative()) {
            heldItem.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(hand));
        }

        return InteractionResult.SUCCESS;
    }

    return InteractionResult.PASS;
}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		return box(0, 0, 0, 16, 16, 16);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(BLOCKSTATE);
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
