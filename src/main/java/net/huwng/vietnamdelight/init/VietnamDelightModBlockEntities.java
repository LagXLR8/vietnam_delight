package net.huwng.vietnamdelight.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.Block;

import net.huwng.vietnamdelight.block.entity.RopeNodesBlockEntity;
import net.huwng.vietnamdelight.block.entity.FishTrapBlockEntity;
import net.huwng.vietnamdelight.block.entity.CeramicTilesStairsBlockEntity;
import net.huwng.vietnamdelight.block.entity.CeramicTilesSlabBlockEntity;
import net.huwng.vietnamdelight.block.entity.CeramicTilesBlockEntity;
import net.huwng.vietnamdelight.block.entity.CeramicPlateBlockEntity;
import net.huwng.vietnamdelight.block.entity.CeramicClayVaseBlockEntity;
import net.huwng.vietnamdelight.block.entity.CeramicClayStairsBlockEntity;
import net.huwng.vietnamdelight.block.entity.CeramicClaySlabBlockEntity;
import net.huwng.vietnamdelight.block.entity.CeramicClayPotBlockEntity;
import net.huwng.vietnamdelight.block.entity.CeramicClayLidBlockEntity;
import net.huwng.vietnamdelight.block.entity.CeramicClayBlockBlockEntity;
import net.huwng.vietnamdelight.block.entity.BloomingGoldenFlamboyantLeavesBlockEntity;
import net.huwng.vietnamdelight.block.entity.FloweringGoldenFlamboyantLeavesBlockEntity;
import net.huwng.vietnamdelight.block.entity.CicadasBlockEntity;
import net.huwng.vietnamdelight.VietnamDelightMod;

public class VietnamDelightModBlockEntities {
	public static final DeferredRegister<BlockEntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, VietnamDelightMod.MODID);
	public static final RegistryObject<BlockEntityType<?>> CERAMIC_CLAY_BLOCK = register("ceramic_clay_block", VietnamDelightModBlocks.CERAMIC_CLAY_BLOCK, CeramicClayBlockBlockEntity::new);
	public static final RegistryObject<BlockEntityType<?>> CERAMIC_CLAY_STAIRS = register("ceramic_clay_stairs", VietnamDelightModBlocks.CERAMIC_CLAY_STAIRS, CeramicClayStairsBlockEntity::new);
	public static final RegistryObject<BlockEntityType<?>> CERAMIC_CLAY_SLAB = register("ceramic_clay_slab", VietnamDelightModBlocks.CERAMIC_CLAY_SLAB, CeramicClaySlabBlockEntity::new);
	public static final RegistryObject<BlockEntityType<?>> CERAMIC_TILES = register("ceramic_tiles", VietnamDelightModBlocks.CERAMIC_TILES, CeramicTilesBlockEntity::new);
	public static final RegistryObject<BlockEntityType<?>> CERAMIC_TILES_STAIRS = register("ceramic_tiles_stairs", VietnamDelightModBlocks.CERAMIC_TILES_STAIRS, CeramicTilesStairsBlockEntity::new);
	public static final RegistryObject<BlockEntityType<?>> CERAMIC_TILES_SLAB = register("ceramic_tiles_slab", VietnamDelightModBlocks.CERAMIC_TILES_SLAB, CeramicTilesSlabBlockEntity::new);
	public static final RegistryObject<BlockEntityType<?>> CERAMIC_PLATE = register("ceramic_plate", VietnamDelightModBlocks.CERAMIC_PLATE, CeramicPlateBlockEntity::new);
	public static final RegistryObject<BlockEntityType<?>> CERAMIC_CLAY_POT = register("ceramic_clay_pot", VietnamDelightModBlocks.CERAMIC_CLAY_POT, CeramicClayPotBlockEntity::new);
	public static final RegistryObject<BlockEntityType<?>> CERAMIC_CLAY_LID = register("ceramic_clay_lid", VietnamDelightModBlocks.CERAMIC_CLAY_LID, CeramicClayLidBlockEntity::new);
	public static final RegistryObject<BlockEntityType<?>> CERAMIC_CLAY_VASE = register("ceramic_clay_vase", VietnamDelightModBlocks.CERAMIC_CLAY_VASE, CeramicClayVaseBlockEntity::new);
	public static final RegistryObject<BlockEntityType<?>> ROPE_NODES = register("rope_nodes", VietnamDelightModBlocks.ROPE_NODES, RopeNodesBlockEntity::new);
	public static final RegistryObject<BlockEntityType<?>> FISH_TRAP = register("fish_trap", VietnamDelightModBlocks.FISH_TRAP, FishTrapBlockEntity::new);
	public static final RegistryObject<BlockEntityType<?>> BLOOMING_GOLDEN_FLAMBOYANT_LEAVES = register("blooming_golden_flamboyant_leaves", VietnamDelightModBlocks.BLOOMING_GOLDEN_FLAMBOYANT_LEAVES, BloomingGoldenFlamboyantLeavesBlockEntity::new);
	public static final RegistryObject<BlockEntityType<?>> FLOWERING_GOLDEN_FLAMBOYANT_LEAVES = register("flowering_golden_flamboyant_leaves", VietnamDelightModBlocks.FLOWERING_GOLDEN_FLAMBOYANT_LEAVES, FloweringGoldenFlamboyantLeavesBlockEntity::new);
	public static final RegistryObject<BlockEntityType<?>> CICADAS = register("cicadas", VietnamDelightModBlocks.CICADAS, CicadasBlockEntity::new);

	// Start of user code block custom block entities
	public static final RegistryObject<BlockEntityType<net.huwng.vietnamdelight.block.entity.DurianSignBlockEntity>> DURIAN_SIGN = REGISTRY.register("durian_sign",
			() -> BlockEntityType.Builder.of(net.huwng.vietnamdelight.block.entity.DurianSignBlockEntity::new, VietnamDelightModBlocks.DURIAN_SIGN.get(), VietnamDelightModBlocks.DURIAN_WALL_SIGN.get()).build(null));
	public static final RegistryObject<BlockEntityType<net.huwng.vietnamdelight.block.entity.DurianHangingSignBlockEntity>> DURIAN_HANGING_SIGN = REGISTRY.register("durian_hanging_sign",
			() -> BlockEntityType.Builder.of(net.huwng.vietnamdelight.block.entity.DurianHangingSignBlockEntity::new, VietnamDelightModBlocks.DURIAN_HANGING_SIGN.get(), VietnamDelightModBlocks.DURIAN_WALL_HANGING_SIGN.get()).build(null));
	public static final RegistryObject<BlockEntityType<net.huwng.vietnamdelight.block.entity.FlamboyantSignBlockEntity>> FLAMBOYANT_SIGN = REGISTRY.register("flamboyant_sign",
			() -> BlockEntityType.Builder.of(net.huwng.vietnamdelight.block.entity.FlamboyantSignBlockEntity::new, VietnamDelightModBlocks.FLAMBOYANT_SIGN.get(), VietnamDelightModBlocks.FLAMBOYANT_WALL_SIGN.get()).build(null));
	public static final RegistryObject<BlockEntityType<net.huwng.vietnamdelight.block.entity.FlamboyantHangingSignBlockEntity>> FLAMBOYANT_HANGING_SIGN = REGISTRY.register("flamboyant_hanging_sign",
			() -> BlockEntityType.Builder.of(net.huwng.vietnamdelight.block.entity.FlamboyantHangingSignBlockEntity::new, VietnamDelightModBlocks.FLAMBOYANT_HANGING_SIGN.get(), VietnamDelightModBlocks.FLAMBOYANT_WALL_HANGING_SIGN.get()).build(null));
	public static final RegistryObject<BlockEntityType<?>> CERAMIC_CLAY_WALL = register("ceramic_clay_wall", VietnamDelightModBlocks.CERAMIC_CLAY_WALL, net.huwng.vietnamdelight.block.entity.CeramicClayWallBlockEntity::new);
	public static final RegistryObject<BlockEntityType<?>> POLISHED_CERAMIC_CLAY = register("polished_ceramic_clay", VietnamDelightModBlocks.POLISHED_CERAMIC_CLAY, net.huwng.vietnamdelight.block.entity.PolishedCeramicClayBlockEntity::new);
	public static final RegistryObject<BlockEntityType<?>> POLISHED_CERAMIC_CLAY_STAIRS = register("polished_ceramic_clay_stairs", VietnamDelightModBlocks.POLISHED_CERAMIC_CLAY_STAIRS, net.huwng.vietnamdelight.block.entity.PolishedCeramicClayStairsBlockEntity::new);
	public static final RegistryObject<BlockEntityType<?>> POLISHED_CERAMIC_CLAY_SLAB = register("polished_ceramic_clay_slab", VietnamDelightModBlocks.POLISHED_CERAMIC_CLAY_SLAB, net.huwng.vietnamdelight.block.entity.PolishedCeramicClaySlabBlockEntity::new);
	public static final RegistryObject<BlockEntityType<?>> POLISHED_CERAMIC_CLAY_WALL = register("polished_ceramic_clay_wall", VietnamDelightModBlocks.POLISHED_CERAMIC_CLAY_WALL, net.huwng.vietnamdelight.block.entity.PolishedCeramicClayWallBlockEntity::new);
	public static final RegistryObject<BlockEntityType<?>> CERAMIC_TILES_WALL = register("ceramic_tiles_wall", VietnamDelightModBlocks.CERAMIC_TILES_WALL, net.huwng.vietnamdelight.block.entity.CeramicTilesWallBlockEntity::new);
	// End of user code block custom block entities
	private static RegistryObject<BlockEntityType<?>> register(String registryname, RegistryObject<Block> block, BlockEntityType.BlockEntitySupplier<?> supplier) {
		return REGISTRY.register(registryname, () -> BlockEntityType.Builder.of(supplier, block.get()).build(null));
	}
}
