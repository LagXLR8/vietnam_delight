package net.huwng.vietnamdelight.item;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.huwng.vietnamdelight.entity.DurianShellProjectileEntity;

public class DurianShellItem extends Item {

    public DurianShellItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        world.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.SNOWBALL_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F));

        if (!world.isClientSide) {
            DurianShellProjectileEntity shell = new DurianShellProjectileEntity(world, player);
            shell.setItem(itemstack);

            // Đặt vị trí xuất phát hơi chếch về phía trước mặt người chơi để không bao giờ bị đụng vào người chơi
            Vec3 look = player.getLookAngle();
            shell.setPos(player.getX() + look.x * 0.5D, player.getEyeY() - 0.1D + look.y * 0.2D, player.getZ() + look.z * 0.5D);
            shell.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1.0F);
            world.addFreshEntity(shell);
        }

        player.awardStat(Stats.ITEM_USED.get(this));
        if (!player.getAbilities().instabuild) {
            itemstack.shrink(1);
        }

        return InteractionResultHolder.sidedSuccess(itemstack, world.isClientSide());
    }
}
