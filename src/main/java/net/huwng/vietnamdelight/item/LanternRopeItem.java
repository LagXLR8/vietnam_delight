package net.huwng.vietnamdelight.item;

import net.huwng.vietnamdelight.block.RopeNodesBlock;
import net.huwng.vietnamdelight.network.ModNetworking;
import net.huwng.vietnamdelight.network.SyncRopeConnectionsPacket;
import net.huwng.vietnamdelight.rope.RopeConnection;
import net.huwng.vietnamdelight.rope.RopeConnectionManager;
import net.huwng.vietnamdelight.rope.RopeSavedData;
import net.huwng.vietnamdelight.init.VietnamDelightModBlocks;
import net.huwng.vietnamdelight.init.VietnamDelightModItems;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.world.phys.Vec3;
import net.minecraft.nbt.CompoundTag;

public class LanternRopeItem extends Item {
    public LanternRopeItem() {
        super(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON));
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        Level level = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();
        Player player = ctx.getPlayer();
        ItemStack stack = ctx.getItemInHand();
        BlockPos placePos = pos.relative(ctx.getClickedFace());

        BlockState clickedState = level.getBlockState(pos);

        // 1. Click vào RopeNode
        if (clickedState.getBlock() instanceof RopeNodesBlock) {
            CompoundTag tag = stack.getOrCreateTag();

            if (!tag.contains("FirstNode")) {
                tag.putLong("FirstNode", pos.asLong());
                if (player != null && !level.isClientSide) {
					player.displayClientMessage(Component.literal("First node selected").withStyle(ChatFormatting.GREEN), true);
                }
                // Ghost rope hiển thị client
                if (player != null && level.isClientSide) {
                    net.huwng.vietnamdelight.client.RopeRenderEvents.ghostFirstNode = pos;
                }
                return InteractionResult.SUCCESS;
            } else {
                BlockPos firstNode = BlockPos.of(tag.getLong("FirstNode"));
                if (!firstNode.equals(pos)) {
                    double dist = Vec3.atCenterOf(firstNode).distanceTo(Vec3.atCenterOf(pos));
                    if (dist > 15.0) {
                        if (player != null && !level.isClientSide) {
							player.displayClientMessage(Component.literal("First node selected").withStyle(ChatFormatting.GREEN), true);
                            player.playSound(SoundEvents.VILLAGER_NO, 1f, 1f);
                        }
                        return InteractionResult.SUCCESS;
                    }

                    RopeConnection conn = new RopeConnection(firstNode, pos);
                    if (!level.isClientSide) {
                        RopeConnectionManager.addConnection(level, conn);

                        if (level instanceof ServerLevel server) {
                            RopeSavedData saved = RopeSavedData.get(server);
                            saved.setData(RopeConnectionManager.serializeConnectionsForDimension(level));
                            saved.setDirty();
                        }

                        ModNetworking.CHANNEL.send(
                                net.minecraftforge.network.PacketDistributor.ALL.noArg(),
                                new SyncRopeConnectionsPacket(level.dimension(), RopeConnectionManager.getConnections(level))
                        );
                    }

                    if (player != null && !player.isCreative()) stack.shrink(1);
                }

                tag.remove("FirstNode");
                // Xóa ghost rope client
                if (player != null && level.isClientSide) {
                    net.huwng.vietnamdelight.client.RopeRenderEvents.ghostFirstNode = null;
                }

                return InteractionResult.SUCCESS;
            }
        }

        // 2. Click vào block thường => đặt RopeNode
        if (!level.isClientSide) {
            BlockState targetState = level.getBlockState(placePos);
            if (targetState.isAir() || targetState.canBeReplaced()) {
                Block nodes = VietnamDelightModBlocks.ROPE_NODES.get();
                BlockState nodesState = nodes.defaultBlockState().setValue(RopeNodesBlock.FACING, ctx.getClickedFace());
                level.setBlock(placePos, nodesState, 3);

                if (player != null && !player.isCreative()) stack.shrink(1);
                player.playSound(SoundEvents.WOOD_PLACE, 1f, 1f);

                return InteractionResult.SUCCESS;
            }
        }

        return InteractionResult.PASS;
    }
}
