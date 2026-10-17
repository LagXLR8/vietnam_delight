package net.huwng.vietnamdelight.block;

import net.huwng.vietnamdelight.init.VietnamDelightModItems;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.CollisionContext;

import java.util.List;
import java.util.Random;

public class WildGarlicChivesBlock extends FlowerBlock {
    public WildGarlicChivesBlock() {
        super(() -> MobEffects.MOVEMENT_SPEED, 100, BlockBehaviour.Properties.of()
            .mapColor(MapColor.PLANT)
            .sound(SoundType.GRASS)
            .instabreak()
            .noCollission()
            .randomTicks()
            .offsetType(BlockBehaviour.OffsetType.XZ)
            .pushReaction(PushReaction.DESTROY));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        Vec3 offset = state.getOffset(world, pos);
        return box(2, 0, 2, 14, 14, 14).move(offset.x, offset.y, offset.z);
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
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player) {
        return new ItemStack(VietnamDelightModItems.WILD_GARLIC_CHIVES.get());
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

    public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        BlockPos below = pos.below();
        if (world.getBlockState(below).is(Blocks.GRASS_BLOCK)) {
            world.setBlock(below, Blocks.COARSE_DIRT.defaultBlockState(), 3);
        }
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        Random random = new Random();
        int count = 1 + random.nextInt(2);
        return List.of(new ItemStack(VietnamDelightModItems.GARLIC_CHIVES.get(), count));
    }
}
