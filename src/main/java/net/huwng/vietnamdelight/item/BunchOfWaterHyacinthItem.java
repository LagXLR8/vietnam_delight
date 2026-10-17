package net.huwng.vietnamdelight.item;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;

import net.huwng.vietnamdelight.init.VietnamDelightModBlocks;
import net.huwng.vietnamdelight.block.WaterHyacinthLayerBlock;

public class BunchOfWaterHyacinthItem extends Item {
    public BunchOfWaterHyacinthItem() {
        super(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON));
    }
    
    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        Player player = context.getPlayer();
        
        // Nếu click vào WaterHyacinthLayer và chưa đầy (< 8 layers)
        if (state.getBlock() instanceof WaterHyacinthLayerBlock) {
            int layers = state.getValue(WaterHyacinthLayerBlock.LAYERS);
            if (layers < 8) {
                // Tăng thêm 1 layer
                level.setBlock(pos, state.setValue(WaterHyacinthLayerBlock.LAYERS, layers + 1), 3);
                level.playSound(null, pos, state.getSoundType().getPlaceSound(), 
                    SoundSource.BLOCKS, 1.0F, 1.0F);
                
                if (player != null && !player.isCreative()) {
                    context.getItemInHand().shrink(1);
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }
        
        // Click vào block khác hoặc vào không gian trống -> đặt block mới
        BlockPos placePos = pos.relative(context.getClickedFace());
        BlockState stateAtPlace = level.getBlockState(placePos);
        
        if (stateAtPlace.isAir() || stateAtPlace.canBeReplaced(new BlockPlaceContext(context))) {
            BlockState newState = VietnamDelightModBlocks.WATER_HYACINTH_LAYER.get().defaultBlockState();
            if (newState.canSurvive(level, placePos)) {
                level.setBlock(placePos, newState, 3);
                level.playSound(null, placePos, newState.getSoundType().getPlaceSound(), 
                    SoundSource.BLOCKS, 1.0F, 1.0F);
                
                if (player != null && !player.isCreative()) {
                    context.getItemInHand().shrink(1);
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }
        
        return InteractionResult.PASS;
    }
}