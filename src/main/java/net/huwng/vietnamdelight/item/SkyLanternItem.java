package net.huwng.vietnamdelight.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.core.BlockPos;

import net.huwng.vietnamdelight.entity.SkyLanternsEntity;
import net.huwng.vietnamdelight.init.VietnamDelightModEntities;

public class SkyLanternItem extends Item {
    public SkyLanternItem() {
        super(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON));
    }
	
	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
	    ItemStack stack = player.getItemInHand(hand);
	
	    // Nếu người chơi đang crouch (shift)
	    if (player.isShiftKeyDown()) {
	        if (!level.isClientSide) {
	            int count = stack.getCount(); // số lượng lantern trong tay
	            int radius = 64;
	
	            for (int i = 0; i < count; i++) {
	                // random vị trí quanh player
	                int dx = player.getBlockX() + level.random.nextInt(radius * 2) - radius;
	                int dz = player.getBlockZ() + level.random.nextInt(radius * 2) - radius;
	
	                // tìm mặt đất tại (dx, dz)
	                BlockPos basePos = level.getHeightmapPos(
	                        net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
	                        new BlockPos(dx, player.getBlockY(), dz)
	                );
	
	                // check khoảng trống phía trên (để lantern không spawn trong block)
	                BlockPos spawnPos = basePos.above();
	                if (level.isEmptyBlock(spawnPos) && level.isEmptyBlock(spawnPos.above())) {
	                    SkyLanternsEntity lantern = VietnamDelightModEntities.SKY_LANTERNS.get().create(level);
	                    if (lantern != null) {
	                        lantern.moveTo(
	                                spawnPos.getX() + 0.5D,
	                                spawnPos.getY(),
	                                spawnPos.getZ() + 0.5D,
	                                level.random.nextFloat() * 360F,
	                                0.0F
	                        );
	                        level.addFreshEntity(lantern);
	                    }
	                }
	            }
	
	            // trừ hết stack nếu không phải chế độ sáng tạo
	            if (!player.getAbilities().instabuild) {
	                stack.shrink(count);
	            }
	        }
	
	        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
	    }
	
	    // --- bình thường: spawn 1 lantern ngay block người chơi nhìn ---
	    HitResult hit = player.pick(5.0D, 0.0F, false);
	    if (hit.getType() == HitResult.Type.BLOCK) {
	        BlockHitResult bhr = (BlockHitResult) hit;
	        BlockPos pos = bhr.getBlockPos().above();
	
	        if (!level.isClientSide) {
	            SkyLanternsEntity lantern = VietnamDelightModEntities.SKY_LANTERNS.get().create(level);
	            if (lantern != null) {
	                lantern.moveTo(
	                        pos.getX() + 0.5D,
	                        pos.getY(),
	                        pos.getZ() + 0.5D,
	                        player.getYRot(),
	                        0.0F
	                );
	                level.addFreshEntity(lantern);
	            }
	
	            if (!player.getAbilities().instabuild) {
	                stack.shrink(1);
	            }
	        }
	        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
	    }
	
	    return InteractionResultHolder.pass(stack);
	}

}
