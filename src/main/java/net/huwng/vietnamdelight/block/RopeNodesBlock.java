package net.huwng.vietnamdelight.block;

import net.huwng.vietnamdelight.block.entity.RopeNodesBlockEntity;
import net.huwng.vietnamdelight.network.ModNetworking;
import net.huwng.vietnamdelight.network.SyncRopeConnectionsPacket;
import net.huwng.vietnamdelight.rope.RopeConnectionManager;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import java.util.List;
import java.util.ArrayList;
import net.minecraft.world.item.BlockItem;

import net.huwng.vietnamdelight.rope.RopeConnection;
import net.huwng.vietnamdelight.rope.RopeSavedData;
import net.huwng.vietnamdelight.network.ModNetworking;
import net.huwng.vietnamdelight.network.SyncRopeConnectionsPacket;
import net.huwng.vietnamdelight.init.VietnamDelightModItems;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.block.Block;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.phys.HitResult;
import net.huwng.vietnamdelight.rope.RopeConnection;
import net.huwng.vietnamdelight.rope.RopeConnectionManager;
import net.huwng.vietnamdelight.init.VietnamDelightModItems;

import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.network.PacketDistributor;

public class RopeNodesBlock extends Block implements EntityBlock {
    public static final DirectionProperty FACING = DirectionalBlock.FACING;

    public RopeNodesBlock() {
        super(BlockBehaviour.Properties.of()
            .instrument(NoteBlockInstrument.BASEDRUM)
            .sound(SoundType.LANTERN)
            .strength(0f, 1f)
            .noOcclusion()
            .isRedstoneConductor((bs, br, bp) -> false));
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            default -> box(5, 5, 0, 11, 11, 2);
            case NORTH -> box(5, 5, 14, 11, 11, 16);
            case EAST -> box(0, 5, 5, 2, 11, 11);
            case WEST -> box(14, 5, 5, 16, 11, 11);
            case UP -> box(5, 0, 5, 11, 2, 11);
            case DOWN -> box(5, 14, 5, 11, 16, 11);
        };
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RopeNodesBlockEntity(pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
	    super.onRemove(state, level, pos, newState, isMoving);
	
	    if (!level.isClientSide && state.getBlock() != newState.getBlock()) {
	        // Tìm các connection chạm node này và thu chúng lại
	        var conns = RopeConnectionManager.getConnections(level);
	        // Lưu index hoặc bản sao connection để xóa, tránh ConcurrentModification
	        List<RopeConnection> toRemove = new ArrayList<>();
	        for (RopeConnection c : conns) {
	            if (c.nodeA.equals(pos) || c.nodeB.equals(pos)) {
	                toRemove.add(c);
	            }
	        }
	
	        // Nếu có connection liên quan -> rơi thêm lantern rope cho mỗi connection (bạn muốn rơi 1 hay nhiều? hiện rơi 1 + connections.size())
	        int baseDrops = 1;
	        int extraDrops = toRemove.size(); // nếu bạn muốn chỉ rơi 1 extra khi có ít nhất 1 connection thay vì per-connection: use (toRemove.isEmpty() ? 0 : 1)
	        int totalDrops = baseDrops + extraDrops;
	
	        // Item reference — đổi tên nếu registry item bạn khác
	        // Thường bạn sẽ có: net.huwng.vietnamdelight.init.VietnamDelightModItems.LANTERN_ROPE
	        for (int i = 0; i < totalDrops; i++) {
	            Block.popResource(level, pos, new ItemStack(net.huwng.vietnamdelight.init.VietnamDelightModItems.LANTERN_ROPE.get()));
	        }
	
	        // Rơi lanterns treo trên các dây bị xóa
	        for (RopeConnection c : toRemove) {
	            for (RopeConnection.LanternEntry le : c.lanterns) {
	                if (le != null && le.state != null && le.state.getBlock() != null) {
	                    Block.popResource(level, pos, new ItemStack(le.state.getBlock()));
	                }
	            }
	            // xóa connection
	            conns.remove(c);
	        }
	
	        // Sync sau khi chỉnh sửa
	        SyncRopeConnectionsPacket packet =
	            new SyncRopeConnectionsPacket(level.dimension(), conns);
	        ModNetworking.CHANNEL.send(net.minecraftforge.network.PacketDistributor.ALL.noArg(), packet);
	
	        // persist saved data
	        if (level instanceof ServerLevel server) {
	            RopeSavedData saved = RopeSavedData.get(server);
	            saved.setData(RopeConnectionManager.serializeConnectionsForDimension(level));
	            saved.setDirty();
	        }
	    }
	}
	
	@Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player) {
        return new ItemStack(VietnamDelightModItems.LANTERN_ROPE.get());
    }
    
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.defaultBlockState().setValue(FACING, context.getClickedFace());
	}

	public BlockState rotate(BlockState state, Rotation rot) {
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}

	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
	}

	@Override
	public MenuProvider getMenuProvider(BlockState state, Level worldIn, BlockPos pos) {
		BlockEntity tileEntity = worldIn.getBlockEntity(pos);
		return tileEntity instanceof MenuProvider menuProvider ? menuProvider : null;
	}

	@Override
	public boolean triggerEvent(BlockState state, Level world, BlockPos pos, int eventID, int eventParam) {
		super.triggerEvent(state, world, pos, eventID, eventParam);
		BlockEntity blockEntity = world.getBlockEntity(pos);
		return blockEntity == null ? false : blockEntity.triggerEvent(eventID, eventParam);
	}
}
