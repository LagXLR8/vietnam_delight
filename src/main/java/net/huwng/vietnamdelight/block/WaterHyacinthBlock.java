package net.huwng.vietnamdelight.block;

import net.minecraftforge.common.PlantType;

import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.WaterlilyBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.util.RandomSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;

import net.huwng.vietnamdelight.init.VietnamDelightModItems;
import net.huwng.vietnamdelight.init.VietnamDelightModBlocks;

import java.util.ArrayList;
import java.util.List;

public class WaterHyacinthBlock extends WaterlilyBlock implements BonemealableBlock {
    public WaterHyacinthBlock() {
        super(BlockBehaviour.Properties.of()
            .mapColor(MapColor.PLANT)
            .sound(SoundType.LILY_PAD)
            .instabreak()
            .noCollission()
            .offsetType(BlockBehaviour.OffsetType.XZ)
            .pushReaction(PushReaction.DESTROY));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return box(0, 0, 0, 16, 1.5, 16);
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
        return 100;
    }

    @Override
    public int getFireSpreadSpeed(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
        return 60;
    }
    
    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player) {
        return new ItemStack(VietnamDelightModItems.BUNCH_OF_WATER_HYACINTH.get());
    }

    @Override
    public PlantType getPlantType(BlockGetter world, BlockPos pos) {
        return PlantType.WATER;
    }

    // Bonemeal implementation
    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state, boolean isClient) {
        return true;
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        // Số lượng cây sẽ lan ra (1-3)
        int spreadCount = 1 + random.nextInt(3);
        
        // Danh sách các block có thể được đặt
        List<BlockState> plantVariants = new ArrayList<>();
        plantVariants.add(VietnamDelightModBlocks.WATER_HYACINTH.get().defaultBlockState());
        plantVariants.add(VietnamDelightModBlocks.WATER_HYACINTH_FLOWER.get().defaultBlockState());
        plantVariants.add(VietnamDelightModBlocks.WATER_HYACINTH_FLOWER_STEM.get().defaultBlockState());
        plantVariants.add(VietnamDelightModBlocks.WATER_HYACINTH_STEM.get().defaultBlockState());
        
        // Tìm các vị trí hợp lệ trong vùng 3x3
        List<BlockPos> validPositions = new ArrayList<>();
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                if (x == 0 && z == 0) continue; // Bỏ qua vị trí hiện tại
                
                BlockPos targetPos = pos.offset(x, 0, z);
                if (canPlaceAt(level, targetPos)) {
                    validPositions.add(targetPos);
                }
            }
        }
        
        // Đặt cây ngẫu nhiên vào các vị trí hợp lệ
        for (int i = 0; i < spreadCount && !validPositions.isEmpty(); i++) {
            int randomIndex = random.nextInt(validPositions.size());
            BlockPos targetPos = validPositions.remove(randomIndex);
            
            // Chọn ngẫu nhiên một loại cây
            BlockState randomPlant = plantVariants.get(random.nextInt(plantVariants.size()));
            level.setBlock(targetPos, randomPlant, 3);
        }
    }
    
    // Kiểm tra xem có thể đặt cây tại vị trí này không
    private boolean canPlaceAt(Level level, BlockPos pos) {
        BlockState stateAtPos = level.getBlockState(pos);
        BlockState stateBelow = level.getBlockState(pos.below());
        
        // Kiểm tra: vị trí hiện tại phải trống (air) và phía dưới phải là nước
        return stateAtPos.isAir() && 
               stateBelow.getFluidState().getType() == Fluids.WATER &&
               stateBelow.getFluidState().isSource();
    }
}