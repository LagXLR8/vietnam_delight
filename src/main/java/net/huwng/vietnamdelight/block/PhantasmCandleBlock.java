package net.huwng.vietnamdelight.block;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;

public class PhantasmCandleBlock extends BaseCustomCandleBlock {
    public PhantasmCandleBlock() {
        super(MapColor.COLOR_PURPLE);
    }

    @Override
    public CandleType getCandleType() {
        return CandleType.PHANTASM;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack itemstack = player.getItemInHand(hand);
        if ((itemstack.is(Items.FLINT_AND_STEEL) || itemstack.is(Items.FIRE_CHARGE)) && !state.getValue(LIT)) {
            if (!CandleEffectHandler.isNight(level)) {
                if (level.isClientSide) {
                    player.displayClientMessage(Component.translatable("message.vietnam_delight.phantasm_candle.day_warning"), true);
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }
        return super.use(state, level, pos, player, hand, hit);
    }

    @Override
    public void onProjectileHit(Level level, BlockState state, BlockHitResult hit, Projectile projectile) {
        if (!level.isClientSide && projectile.isOnFire() && !state.getValue(LIT) && !state.getValue(WATERLOGGED) && CandleEffectHandler.isNight(level)) {
            level.setBlock(hit.getBlockPos(), state.setValue(LIT, true), 11);
        }
    }
}
