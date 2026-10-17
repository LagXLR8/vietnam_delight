package net.huwng.vietnamdelight.block;

import net.huwng.vietnamdelight.init.VietnamDelightModItems;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.*;

import java.util.List;
import java.util.Random;

public class WildLimeTreeBlock extends FlowerBlock {
    public WildLimeTreeBlock() {
        super(() -> MobEffects.MOVEMENT_SPEED, 100, BlockBehaviour.Properties.of()
            .mapColor(MapColor.PLANT)
            .sound(SoundType.AZALEA)
            .instabreak()
            .noCollission()
            .randomTicks()
            .offsetType(BlockBehaviour.OffsetType.XZ)
            .pushReaction(PushReaction.DESTROY));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        Vec3 offset = state.getOffset(world, pos);
        return Shapes.or(box(0, 5, 0, 16, 16, 16), box(6, 0, 6, 10, 11, 10)).move(offset.x, offset.y, offset.z);
    }

    @Override
    public int getEffectDuration() {
        return 100;
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
    public boolean mayPlaceOn(BlockState groundState, BlockGetter world, BlockPos pos) {
        return groundState.is(Blocks.GRASS_BLOCK)
            || groundState.is(Blocks.DIRT)
            || groundState.is(Blocks.COARSE_DIRT)
            || groundState.is(Blocks.PODZOL)
            || groundState.is(Blocks.ROOTED_DIRT)
            || groundState.is(Blocks.FARMLAND);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        return mayPlaceOn(world.getBlockState(pos.below()), world, pos.below());
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        LevelReader level = context.getLevel();
        BlockPos pos = context.getClickedPos();

        if (!canSurvive(defaultBlockState(), level, pos)) {
            return null;
        }

        return defaultBlockState();
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        entity.makeStuckInBlock(state, new Vec3(0.8D, 0.75D, 0.8D));
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        Random random = new Random();
        int count = 1 + random.nextInt(3); // 1–3 Kumquat Seeds
        return List.of(new ItemStack(VietnamDelightModItems.LIME.get(), count));
    }

    public int tickRate(LevelReader world) {
        return 10;
    }

    @Override
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (!world.isClientSide) {
            world.scheduleTick(pos, this, tickRate(world));
        }
        super.onPlace(state, world, pos, oldState, isMoving);
    }

    @Override
    public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        BlockPos below = pos.below();
        if (world.getBlockState(below).is(Blocks.GRASS_BLOCK)) {
            world.setBlock(below, Blocks.COARSE_DIRT.defaultBlockState(), 3);
        }
    }
}
