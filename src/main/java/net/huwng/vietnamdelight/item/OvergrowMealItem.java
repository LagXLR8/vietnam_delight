package net.huwng.vietnamdelight.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.material.Fluids;
import java.util.ArrayList;
import java.util.List;
import net.huwng.vietnamdelight.init.VietnamDelightModBlocks;
import net.huwng.vietnamdelight.block.FloweringBanyanLeavesBlock;
import net.huwng.vietnamdelight.block.FloweringTamarindLeavesBlock;

public class OvergrowMealItem extends Item {
    public OvergrowMealItem() {
        super(new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        ItemStack stack = context.getItemInHand();
        BlockState state = level.getBlockState(pos);

        boolean success = false;

        if (level instanceof ServerLevel serverLevel) {
            RandomSource random = serverLevel.getRandom();

            if (state.is(VietnamDelightModBlocks.FLOWERING_BANYAN_LEAVES.get())) {
                FloweringBanyanLeavesBlock block = (FloweringBanyanLeavesBlock) VietnamDelightModBlocks.FLOWERING_BANYAN_LEAVES.get();
                block.applyGrowth(serverLevel, pos, random, 6, true);
                success = true;

                BlockPos.betweenClosed(pos.offset(-3, -3, -3), pos.offset(3, 3, 3)).forEach(targetPos -> {
                    if (!targetPos.equals(pos)) {
                        BlockState nearbyState = serverLevel.getBlockState(targetPos);
                        if (nearbyState.is(VietnamDelightModBlocks.FLOWERING_BANYAN_LEAVES.get())) {
                            block.applyGrowth(serverLevel, targetPos, random, 6, true);
                        }
                    }
                });
            }
            else if (state.is(VietnamDelightModBlocks.FLOWERING_TAMARIND_LEAVES.get())) {
                FloweringTamarindLeavesBlock block = (FloweringTamarindLeavesBlock) VietnamDelightModBlocks.FLOWERING_TAMARIND_LEAVES.get();
                block.applyGrowth(serverLevel, pos, random, 6, true);
                success = true;

                BlockPos.betweenClosed(pos.offset(-2, -2, -2), pos.offset(2, 2, 2)).forEach(targetPos -> {
                    if (!targetPos.equals(pos)) {
                        BlockState nearbyState = serverLevel.getBlockState(targetPos);
                        if (nearbyState.is(VietnamDelightModBlocks.FLOWERING_TAMARIND_LEAVES.get())) {
                            block.applyGrowth(serverLevel, targetPos, random, 6, true);
                        }
                    }
                });
            }
            
            // ✅ Water Hyacinth variants → lan rộng vùng 5x5 và mọc 3-9 cây
            else if (state.is(VietnamDelightModBlocks.WATER_HYACINTH.get()) ||
                     state.is(VietnamDelightModBlocks.WATER_HYACINTH_FLOWER.get()) ||
                     state.is(VietnamDelightModBlocks.WATER_HYACINTH_FLOWER_STEM.get()) ||
                     state.is(VietnamDelightModBlocks.WATER_HYACINTH_STEM.get())) {
                
                // Số lượng cây sẽ lan ra (3-9)
                int spreadCount = 3 + random.nextInt(7); // 3 + (0-6) = 3-9
                
                // Danh sách các block có thể được đặt
                List<BlockState> plantVariants = new ArrayList<>();
                plantVariants.add(VietnamDelightModBlocks.WATER_HYACINTH.get().defaultBlockState());
                plantVariants.add(VietnamDelightModBlocks.WATER_HYACINTH_FLOWER.get().defaultBlockState());
                plantVariants.add(VietnamDelightModBlocks.WATER_HYACINTH_FLOWER_STEM.get().defaultBlockState());
                plantVariants.add(VietnamDelightModBlocks.WATER_HYACINTH_STEM.get().defaultBlockState());
                
                // Tìm các vị trí hợp lệ trong vùng 5x5
                List<BlockPos> validPositions = new ArrayList<>();
                for (int x = -2; x <= 2; x++) {
                    for (int z = -2; z <= 2; z++) {
                        if (x == 0 && z == 0) continue; // Bỏ qua vị trí hiện tại
                        
                        BlockPos targetPos = pos.offset(x, 0, z);
                        if (canPlaceWaterHyacinth(serverLevel, targetPos)) {
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
                    serverLevel.setBlock(targetPos, randomPlant, 3);
                }
                
                success = true;
            }
            
            // ✅ Nếu là BonemealableBlock khác → bonemeal gấp đôi
            else if (state.getBlock() instanceof BonemealableBlock growable) {
                if (growable.isBonemealSuccess(level, random, pos, state)) {
                    growable.performBonemeal(serverLevel, random, pos, state);
                    growable.performBonemeal(serverLevel, random, pos, state);
                    success = true;
                }
            }

            // ✅ Particle & hiệu ứng nếu thành công
            if (success) {
                serverLevel.sendParticles(
                    ParticleTypes.END_ROD,
                    pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5,
                    20, 0.3, 0.4, 0.3, 0.02
                );
                level.levelEvent(1505, pos, 0);

                if (!context.getPlayer().getAbilities().instabuild) {
                    stack.shrink(1);
                }
                return InteractionResult.SUCCESS;
            }
        }

        return InteractionResult.PASS;
    }

    // ✅ Kiểm tra xem có thể đặt Water Hyacinth tại vị trí này không
    private boolean canPlaceWaterHyacinth(ServerLevel level, BlockPos pos) {
        BlockState stateAtPos = level.getBlockState(pos);
        BlockState stateBelow = level.getBlockState(pos.below());
        
        // Kiểm tra: vị trí hiện tại phải trống (air) và phía dưới phải là nước
        return stateAtPos.isAir() && 
               stateBelow.getFluidState().getType() == Fluids.WATER &&
               stateBelow.getFluidState().isSource();
    }
}