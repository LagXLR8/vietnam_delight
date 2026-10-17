package net.huwng.vietnamdelight.init;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.Position;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;

import net.huwng.vietnamdelight.block.WaterHyacinthLayerBlock;

public class WaterHyacinthDispenserBehavior {
    
    public static void register() {
        DispenseItemBehavior behavior = new DefaultDispenseItemBehavior() {
            @Override
            protected ItemStack execute(net.minecraft.core.BlockSource source, ItemStack stack) {
                Level level = source.getLevel();
                Direction direction = source.getBlockState().getValue(DispenserBlock.FACING);
                BlockPos targetPos = source.getPos().relative(direction);
                BlockState targetState = level.getBlockState(targetPos);
                
                // Kiểm tra nếu vị trí đích đã có WaterHyacinthLayer
                if (targetState.getBlock() instanceof WaterHyacinthLayerBlock) {
                    if (targetState.hasProperty(WaterHyacinthLayerBlock.LAYERS)) {
                        int currentLayers = targetState.getValue(WaterHyacinthLayerBlock.LAYERS);
                        
                        if (currentLayers < 8) {
                            // Cộng thêm 1 layer
                            level.setBlock(targetPos, targetState.setValue(WaterHyacinthLayerBlock.LAYERS, currentLayers + 1), 3);
                            level.playSound(null, targetPos, targetState.getSoundType().getPlaceSound(), 
                                SoundSource.BLOCKS, 1.0F, 1.0F);
                            
                            stack.shrink(1);
                            return stack;
                        }
                    }
                }
                
                // Kiểm tra nếu vị trí đích trống hoặc có thể thay thế
                if (targetState.isAir() || targetState.canBeReplaced()) {
                    BlockState newState = VietnamDelightModBlocks.WATER_HYACINTH_LAYER.get().defaultBlockState();
                    
                    if (newState.canSurvive(level, targetPos)) {
                        level.setBlock(targetPos, newState, 3);
                        level.playSound(null, targetPos, newState.getSoundType().getPlaceSound(), 
                            SoundSource.BLOCKS, 1.0F, 1.0F);
                        
                        stack.shrink(1);
                        return stack;
                    }
                }
                
                // Nếu không thể đặt, bắn item ra như bình thường
                return super.execute(source, stack);
            }
        };
        
        // Đăng ký behavior cho BunchOfWaterHyacinth item
        DispenserBlock.registerBehavior(VietnamDelightModItems.BUNCH_OF_WATER_HYACINTH.get(), behavior);
    }
}