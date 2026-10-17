package net.huwng.vietnamdelight.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.BlockPos;

import net.huwng.vietnamdelight.entity.BananaBoateEntity;
import net.huwng.vietnamdelight.init.VietnamDelightModEntities;

public class BananaBoatItem extends Item {
	public BananaBoatItem() {
		super(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);

		HitResult hit = getPlayerPOVHitResult(level, player, ClipContext.Fluid.ANY);
		if (hit.getType() == HitResult.Type.BLOCK) {
			BlockHitResult bhr = (BlockHitResult) hit;
			BlockPos pos = bhr.getBlockPos();

			if (level.getFluidState(pos).isSource()) {
				if (!level.isClientSide) {
					BananaBoateEntity boat = new BananaBoateEntity(VietnamDelightModEntities.BANANA_BOATE.get(), level);
					Vec3 spawn = Vec3.atBottomCenterOf(pos.above());
					boat.moveTo(spawn.x, spawn.y, spawn.z, player.getYRot(), 0.0F);
					boat.setYRot(player.getYRot());
					boat.setXRot(0.0F);
					float yawRad = (float) Math.toRadians(player.getYRot());
					double speed = 0.10D;
					double vx = -Math.sin(yawRad) * speed;
					double vz =  Math.cos(yawRad) * speed;
					boat.setDeltaMovement(vx, 0.0D, vz);

					level.addFreshEntity(boat);

					if (!player.getAbilities().instabuild) {
						stack.shrink(1);
					}
				}
				return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
			}
		}
		return InteractionResultHolder.pass(stack);
	}
}
