package net.huwng.vietnamdelight.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import net.huwng.vietnamdelight.block.entity.CeramicPlateBlockEntity;

public class CeramicPlateBlock extends Block implements EntityBlock {
    public static final BooleanProperty DIRTY = BooleanProperty.create("dirty");

    public CeramicPlateBlock() {
        super(BlockBehaviour.Properties.of()
            .instrument(NoteBlockInstrument.BASEDRUM)
            .sound(SoundType.DECORATED_POT)
            .strength(0f, 2f)
            .requiresCorrectToolForDrops()
            .dynamicShape());
        this.registerDefaultState(this.stateDefinition.any().setValue(DIRTY, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(DIRTY);
    }

    @Override
    public float getExplosionResistance() {
        return 6f;
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return false;
    }

    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return box(1, 0, 1, 15, 2, 15);
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
        return 0;
    }
    
	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
	    if (!level.isClientSide) {
	        BlockEntity be = level.getBlockEntity(pos);
	        if (be instanceof CeramicPlateBlockEntity plateBE) {
	            ItemStack held = player.getItemInHand(hand);
	
	            if (!held.isEmpty() && plateBE.getStoredItem().isEmpty()) {
	                // đặt item lên dĩa với góc xoay theo hướng người chơi
	                ItemStack copy = held.copy();
	                copy.setCount(1);
	                plateBE.setStoredItem(copy, player.getYRot());
	                held.shrink(1);
	                return InteractionResult.SUCCESS;
	            } else if (held.isEmpty() && !plateBE.getStoredItem().isEmpty()) {
	                // lấy item lại
	                player.setItemInHand(hand, plateBE.getStoredItem());
	                plateBE.setStoredItem(ItemStack.EMPTY);
	                return InteractionResult.SUCCESS;
	            }
	        }
	    }
	    return InteractionResult.sidedSuccess(level.isClientSide);
	}

	@Override
	public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
	    if (!level.isClientSide) {
	        BlockEntity be = level.getBlockEntity(pos);
	        if (be instanceof CeramicPlateBlockEntity plateBE) {
	        	
	            ItemStack drop = new ItemStack(this);
	            drop.getOrCreateTag().putInt("BlockColor", plateBE.getCurrentColor());
	            Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), drop);
	
	            ItemStack stored = plateBE.getStoredItem();
	            if (!stored.isEmpty()) {
	                Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), stored);
	            }
	        }
	    }
	    super.playerWillDestroy(level, pos, state, player);
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
	    super.setPlacedBy(level, pos, state, placer, stack);
	
	    if (!level.isClientSide) {
	        BlockEntity be = level.getBlockEntity(pos);
	        if (be instanceof CeramicPlateBlockEntity plateBE && stack.hasTag() && stack.getTag().contains("BlockColor")) {
	            plateBE.setCurrentColor(stack.getTag().getInt("BlockColor"));
	            level.sendBlockUpdated(pos, state, state, 3);
	        }
	    }
	}

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CeramicPlateBlockEntity(pos, state);
    }
}
