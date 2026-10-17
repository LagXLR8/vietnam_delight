package net.huwng.vietnamdelight.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.BlockHitResult;

import net.huwng.vietnamdelight.block.entity.CeramicClayVaseBlockEntity;

import javax.annotation.Nullable;

public class CeramicClayVaseBlock extends Block implements EntityBlock {
    public static final BooleanProperty DIRTY = BooleanProperty.create("dirty");

    public CeramicClayVaseBlock() {
        super(BlockBehaviour.Properties.of()
            .instrument(NoteBlockInstrument.BASEDRUM)
            .sound(SoundType.DECORATED_POT)
            .instabreak()
            .noOcclusion()
            .isRedstoneConductor((bs, br, bp) -> false)
        );
        this.registerDefaultState(this.stateDefinition.any().setValue(DIRTY, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(DIRTY);
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
        return Shapes.or(
            box(2, 0, 2, 14, 3, 14),
            box(1, 3, 1, 15, 14, 15),
            box(2, 14, 2, 14, 16, 14)
        );
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CeramicClayVaseBlockEntity(pos, state);
    }

    // Khi block bị phá bởi người chơi -> drop item chứa màu (BlockColor)
    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof CeramicClayVaseBlockEntity vaseBE) {
                ItemStack drop = new ItemStack(this);
                drop.getOrCreateTag().putInt("BlockColor", vaseBE.getCurrentColor());
                Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), drop);
            }
        }
        super.playerWillDestroy(level, pos, state, player);
    }

    // Khi đặt block từ item có NBT BlockColor -> set màu cho BE
    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);

        if (!level.isClientSide) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof CeramicClayVaseBlockEntity vaseBE && stack.hasTag() && stack.getTag().contains("BlockColor")) {
                vaseBE.setCurrentColor(stack.getTag().getInt("BlockColor"));
                level.sendBlockUpdated(pos, state, state, 3);
            }
        }
    }
}
