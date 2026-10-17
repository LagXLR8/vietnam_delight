package net.huwng.vietnamdelight.item;

import net.huwng.vietnamdelight.block.BougainvilleaTrunkBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;

public class BougainvilleaTrunkItem extends BlockItem {
    public BougainvilleaTrunkItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState targetState = level.getBlockState(pos);
        Player player = context.getPlayer();
        ItemStack held = context.getItemInHand();

        if (player != null && player.isShiftKeyDown()) {
            return super.useOn(context);
        }

        // Kiểm tra xem khối được click có phải là khối lá cây không
        BougainvilleaTrunkBlock.LeafType leafType = BougainvilleaTrunkBlock.getLeafTypeFromBlock(targetState.getBlock());
        if (leafType != BougainvilleaTrunkBlock.LeafType.NONE) {
            // Đặt Bougainvillea Trunk lồng vào trong khối lá!
            if (!level.isClientSide) {
                BougainvilleaTrunkBlock trunkBlock = (BougainvilleaTrunkBlock) this.getBlock();
                BlockState newTrunkState = trunkBlock.defaultBlockState()
                        .setValue(BougainvilleaTrunkBlock.LEAF, leafType)
                        .setValue(BougainvilleaTrunkBlock.WATERLOGGED, level.getFluidState(pos).getType() == Fluids.WATER)
                        .setValue(BougainvilleaTrunkBlock.NORTH, trunkBlock.canConnect(level, pos, Direction.NORTH, level.getBlockState(pos.north())))
                        .setValue(BougainvilleaTrunkBlock.SOUTH, trunkBlock.canConnect(level, pos, Direction.SOUTH, level.getBlockState(pos.south())))
                        .setValue(BougainvilleaTrunkBlock.EAST, trunkBlock.canConnect(level, pos, Direction.EAST, level.getBlockState(pos.east())))
                        .setValue(BougainvilleaTrunkBlock.WEST, trunkBlock.canConnect(level, pos, Direction.WEST, level.getBlockState(pos.west())))
                        .setValue(BougainvilleaTrunkBlock.UP, trunkBlock.canConnect(level, pos, Direction.UP, level.getBlockState(pos.above())))
                        .setValue(BougainvilleaTrunkBlock.DOWN, trunkBlock.canConnect(level, pos, Direction.DOWN, level.getBlockState(pos.below())));

                level.setBlock(pos, newTrunkState, 3);
                level.playSound(null, pos, SoundType.WOOD.getPlaceSound(), SoundSource.BLOCKS, 1.0F, 1.0F);

                if (player != null && !player.getAbilities().instabuild) {
                    held.shrink(1);
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        return super.useOn(context);
    }
}
