package net.huwng.vietnamdelight.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractCandleBlock;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import net.huwng.vietnamdelight.VietnamDelightMod;
import net.huwng.vietnamdelight.init.VietnamDelightModMobEffects;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Mod.EventBusSubscriber(modid = VietnamDelightMod.MODID)
public class CandleEffectHandler {
    public static final double EFFECT_RADIUS = 8.0D;

    private static final Map<ResourceKey<Level>, Map<BlockPos, CandleType>> ACTIVE_CANDLES = new ConcurrentHashMap<>();

    // Phantasm tracking
    private static final Map<UUID, Integer> PHANTASM_PROGRESS = new ConcurrentHashMap<>();
    private static final Map<UUID, Long> PHANTASM_LAST_SEEN = new ConcurrentHashMap<>();

    // Arid Candle tracking
    public static class AridState {
        public int healTimer = 0;
        public int igniteTimer = 0;
        public boolean hasActiveCooker = false;
    }
    private static final Map<BlockPos, AridState> ARID_STATES = new ConcurrentHashMap<>();

    // Sunburst Candle tracking
    public static class SunburstState {
        public int charges = 0;
        public int chargeAccumulator = 0;
        public int weaknessCooldown = 0;
        public int burnCooldown = 0;
    }
    private static final Map<BlockPos, SunburstState> SUNBURST_STATES = new ConcurrentHashMap<>();

    public static void registerCandle(Level level, BlockPos pos, CandleType type) {
        ACTIVE_CANDLES.computeIfAbsent(level.dimension(), k -> new ConcurrentHashMap<>()).put(pos.immutable(), type);
    }

    public static void unregisterCandle(Level level, BlockPos pos) {
        Map<BlockPos, CandleType> map = ACTIVE_CANDLES.get(level.dimension());
        if (map != null) {
            map.remove(pos);
        }
        ARID_STATES.remove(pos);
        SUNBURST_STATES.remove(pos);
    }

    public static boolean isNearLitCandle(Level level, double x, double y, double z, CandleType type, double radius) {
        Map<BlockPos, CandleType> map = ACTIVE_CANDLES.get(level.dimension());
        if (map == null || map.isEmpty()) return false;

        double r2 = radius * radius;
        for (Map.Entry<BlockPos, CandleType> entry : map.entrySet()) {
            if (entry.getValue() == type) {
                BlockPos p = entry.getKey();
                double dx = p.getX() + 0.5D - x;
                double dy = p.getY() + 0.5D - y;
                double dz = p.getZ() + 0.5D - z;
                if (dx * dx + dy * dy + dz * dz <= r2) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isNearLitCandle(Level level, BlockPos pos, CandleType type, double radius) {
        return isNearLitCandle(level, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, type, radius);
    }

    public static boolean isNight(Level level) {
        long dayTime = level.getDayTime() % 24000L;
        return dayTime >= 12500L && dayTime <= 23500L;
    }

    public static void handleCandleTick(Block block, BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!state.getValue(AbstractCandleBlock.LIT)) {
            unregisterCandle(level, pos);
            return;
        }

        CandleType type = null;
        if (block instanceof BaseCustomCandleBlock candle) {
            type = candle.getCandleType();
        } else if (block instanceof BaseCandleHolderBlock holder) {
            type = holder.getCandleType();
        }
        if (type == null) return;

        registerCandle(level, pos, type);
        AABB aabb = new AABB(pos).inflate(EFFECT_RADIUS);

        switch (type) {
            case PURIFYING -> {
                List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, aabb);
                for (LivingEntity e : entities) {
                    if (!e.getActiveEffects().isEmpty()) {
                        e.removeAllEffects();
                    }
                }
            }
            case RAGE -> {
                // Handled via LivingHurtEvent
            }
            case ARID -> {
                AridState arid = ARID_STATES.computeIfAbsent(pos.immutable(), k -> new AridState());
                arid.healTimer += 10;
                arid.igniteTimer += 10;

                // Check for active cooker every 60 ticks (3s) or on heal trigger
                if (arid.igniteTimer >= 60 || arid.healTimer >= 400) {
                    arid.hasActiveCooker = hasActiveCookerNearby(level, pos, EFFECT_RADIUS);
                }

                // 1. Every 20s (400 ticks): if active cooker present, heal 1 heart (2.0F) and add 1 drumstick (2 food)
                if (arid.healTimer >= 400) {
                    arid.healTimer = 0;
                    if (arid.hasActiveCooker) {
                        List<Player> players = level.getEntitiesOfClass(Player.class, aabb);
                        for (Player p : players) {
                            if (p.isAlive()) {
                                p.heal(2.0F); // 1 heart
                                p.getFoodData().eat(2, 0.4F); // 1 drumstick (2 hunger points)
                                level.playSound(null, p.blockPosition(), SoundEvents.GENERIC_EAT, SoundSource.PLAYERS, 0.5F, 1.2F);
                            }
                        }
                    }
                }

                // 2. Fire ignition: checked every 3 seconds, low chance (10%), targets living entities (not just players),
                // at most 1 entity per trigger, and ignores entities already on fire.
                if (arid.igniteTimer >= 60) {
                    arid.igniteTimer = 0;
                    if (arid.hasActiveCooker && random.nextFloat() < 0.10F) {
                        List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, aabb,
                                e -> e.isAlive() && !e.isOnFire() && !e.fireImmune());
                        if (!targets.isEmpty()) {
                            LivingEntity chosen = targets.get(random.nextInt(targets.size()));
                            chosen.setSecondsOnFire(3);
                            level.playSound(null, chosen.blockPosition(), SoundEvents.FIRECHARGE_USE, SoundSource.BLOCKS, 0.8F, 1.0F);
                        }
                    }
                }
            }
            case SUNBURST -> {
                SunburstState sunburst = SUNBURST_STATES.computeIfAbsent(pos.immutable(), k -> new SunburstState());

                if (sunburst.weaknessCooldown > 0) {
                    sunburst.weaknessCooldown -= 10;
                }
                if (sunburst.burnCooldown > 0) {
                    sunburst.burnCooldown -= 10;
                }

                boolean isDay = !isNight(level);

                if (isDay) {
                    // Daytime: accumulate up to 5 fire charges
                    if (sunburst.charges < 5) {
                        sunburst.chargeAccumulator += 10;
                        if (sunburst.chargeAccumulator >= 300) { // 1 charge per 15s of daytime
                            sunburst.chargeAccumulator = 0;
                            sunburst.charges++;
                            level.sendParticles(ParticleTypes.FLAME, pos.getX() + 0.5D, pos.getY() + 0.65D, pos.getZ() + 0.5D, 3, 0.05D, 0.05D, 0.05D, 0.02D);
                        }
                    } else {
                        sunburst.chargeAccumulator = 0;
                    }
                } else {
                    // Nighttime: if charges > 0 and burnCooldown <= 0, burn nearby monsters!
                    // If a monster is already on fire, skips it and chooses another.
                    if (sunburst.charges > 0 && sunburst.burnCooldown <= 0) {
                        List<LivingEntity> enemies = level.getEntitiesOfClass(LivingEntity.class, aabb,
                                e -> e.isAlive() && (e instanceof Enemy || e instanceof Monster) && !e.isOnFire() && !e.fireImmune());
                        if (!enemies.isEmpty()) {
                            LivingEntity target = enemies.get(0);
                            target.setSecondsOnFire(5);
                            sunburst.charges--;
                            sunburst.burnCooldown = 30; // 1.5s interval
                            level.playSound(null, target.blockPosition(), SoundEvents.FIRECHARGE_USE, SoundSource.BLOCKS, 0.8F, 1.2F);
                            level.sendParticles(ParticleTypes.SMALL_FLAME, target.getX(), target.getY() + 0.5D, target.getZ(), 8, 0.2D, 0.3D, 0.2D, 0.03D);
                        }
                    }
                }

                // Weakness effect: weakens 1 enemy in light range for 30s, followed by a 2-minute (2400 ticks) cooldown
                if (sunburst.weaknessCooldown <= 0) {
                    List<LivingEntity> enemies = level.getEntitiesOfClass(LivingEntity.class, aabb,
                            e -> e.isAlive() && (e instanceof Enemy || e instanceof Monster) && !e.hasEffect(MobEffects.WEAKNESS));
                    if (!enemies.isEmpty()) {
                        LivingEntity target = enemies.get(0);
                        target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 600, 0, false, true)); // 30s
                        sunburst.weaknessCooldown = 2400; // 2 minutes cooldown
                        level.playSound(null, target.blockPosition(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 0.6F, 0.8F);
                    }
                }
            }
            case PHANTASM -> {
                // Extinguish if daytime
                if (!isNight(level)) {
                    AbstractCandleBlock.extinguish(null, state, level, pos);
                    unregisterCandle(level, pos);
                    return;
                }

                // Players in light area gain progress towards ghost state
                List<Player> players = level.getEntitiesOfClass(Player.class, aabb);
                long now = System.currentTimeMillis();
                for (Player p : players) {
                    if (p.hasEffect(VietnamDelightModMobEffects.PHANTASM.get())) {
                        continue; // Already a ghost, no accumulation
                    }
                    UUID id = p.getUUID();
                    PHANTASM_LAST_SEEN.put(id, now);
                    int currentTicks = PHANTASM_PROGRESS.getOrDefault(id, 0) + 10;

                    if (currentTicks >= 400) { // 20 seconds!
                        PHANTASM_PROGRESS.remove(id);
                        PHANTASM_LAST_SEEN.remove(id);
                        p.addEffect(new MobEffectInstance(VietnamDelightModMobEffects.PHANTASM.get(), 600, 0, false, false, true));
                    } else {
                        PHANTASM_PROGRESS.put(id, currentTicks);
                    }
                }
            }
        }
    }

    private static boolean hasActiveCookerNearby(ServerLevel level, BlockPos candlePos, double radius) {
        int r = (int) Math.ceil(radius);
        int r2 = (int) (radius * radius);
        for (int x = -r; x <= r; x++) {
            for (int z = -r; z <= r; z++) {
                if (x * x + z * z > r2) continue;
                for (int y = -2; y <= 2; y++) {
                    BlockPos checkPos = candlePos.offset(x, y, z);
                    BlockEntity be = level.getBlockEntity(checkPos);
                    if (be != null && isCookerActive(be)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static boolean isCookerActive(BlockEntity be) {
        if (be == null) return false;
        BlockState state = be.getBlockState();
        if (be instanceof AbstractFurnaceBlockEntity) {
            return state.hasProperty(AbstractFurnaceBlock.LIT) && state.getValue(AbstractFurnaceBlock.LIT);
        } else if (be instanceof CampfireBlockEntity) {
            return state.hasProperty(CampfireBlock.LIT) && state.getValue(CampfireBlock.LIT);
        } else if (state.hasProperty(BlockStateProperties.LIT)) {
            return state.getValue(BlockStateProperties.LIT);
        }
        return false;
    }

    // ==========================================
    // FORGE EVENT HANDLERS
    // ==========================================

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        LivingEntity target = event.getEntity();
        Level level = target.level();

        // 1. Phantasm Ghost immunity: ghost cannot be harmed
        if (target.hasEffect(VietnamDelightModMobEffects.PHANTASM.get())) {
            event.setCanceled(true);
            return;
        }

        // 2. Arid Candle: double fire damage in light area
        if (event.getSource().is(DamageTypeTags.IS_FIRE)) {
            if (isNearLitCandle(level, target.getX(), target.getY(), target.getZ(), CandleType.ARID, EFFECT_RADIUS)) {
                event.setAmount(event.getAmount() * 2.0F);
            }
        }

        // 3. Rage Candle: +25% attack damage dealt, +10% damage taken
        if (event.getSource().getEntity() instanceof LivingEntity attacker) {
            if (isNearLitCandle(level, attacker.getX(), attacker.getY(), attacker.getZ(), CandleType.RAGE, EFFECT_RADIUS)) {
                event.setAmount(event.getAmount() * 1.25F);
            }
        }
        if (isNearLitCandle(level, target.getX(), target.getY(), target.getZ(), CandleType.RAGE, EFFECT_RADIUS)) {
            event.setAmount(event.getAmount() * 1.10F);
        }
    }

    @SubscribeEvent
    public static void onMobEffectApplicable(MobEffectEvent.Applicable event) {
        LivingEntity entity = event.getEntity();
        // Purifying Candle: cannot receive any effect (good or bad)
        if (isNearLitCandle(entity.level(), entity.getX(), entity.getY(), entity.getZ(), CandleType.PURIFYING, EFFECT_RADIUS)) {
            event.setResult(Event.Result.DENY);
        }
    }

    @SubscribeEvent
    public static void onCheckSpawn(MobSpawnEvent.PositionCheck event) {
        if (event.getEntity() instanceof Phantom) {
            if (event.getLevel() instanceof Level level) {
                if (isNearLitCandle(level, event.getX(), event.getY(), event.getZ(), CandleType.SUNBURST, 16.0D)) {
                    event.setResult(Event.Result.DENY);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onFinalizeSpawn(MobSpawnEvent.FinalizeSpawn event) {
        if (event.getEntity() instanceof Phantom) {
            if (event.getLevel() instanceof Level level) {
                if (isNearLitCandle(level, event.getX(), event.getY(), event.getZ(), CandleType.SUNBURST, 16.0D)) {
                    event.setResult(Event.Result.DENY);
                    event.setCanceled(true);
                }
            }
        }
    }

    // ==========================================
    // PHANTASM GHOST NON-INTERACTION RULES
    // ==========================================

    @SubscribeEvent
    public static void onAttackEntity(AttackEntityEvent event) {
        if (event.getEntity().hasEffect(VietnamDelightModMobEffects.PHANTASM.get())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (event.getEntity().hasEffect(VietnamDelightModMobEffects.PHANTASM.get())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onEntityInteractSpecific(PlayerInteractEvent.EntityInteractSpecific event) {
        if (event.getEntity().hasEffect(VietnamDelightModMobEffects.PHANTASM.get())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.getEntity().hasEffect(VietnamDelightModMobEffects.PHANTASM.get())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        if (event.getEntity().hasEffect(VietnamDelightModMobEffects.PHANTASM.get())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (event.getPlayer().hasEffect(VietnamDelightModMobEffects.PHANTASM.get())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (event.getEntity() instanceof Player player && player.hasEffect(VietnamDelightModMobEffects.PHANTASM.get())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onItemPickup(EntityItemPickupEvent event) {
        if (event.getEntity().hasEffect(VietnamDelightModMobEffects.PHANTASM.get())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onItemToss(ItemTossEvent event) {
        if (event.getPlayer().hasEffect(VietnamDelightModMobEffects.PHANTASM.get())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onMobTarget(LivingChangeTargetEvent event) {
        if (event.getNewTarget() instanceof Player player && player.hasEffect(VietnamDelightModMobEffects.PHANTASM.get())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (PHANTASM_PROGRESS.isEmpty()) return;

        // Clean up or decay phantasm progress for players no longer standing in candle light
        long now = System.currentTimeMillis();
        for (UUID id : new ArrayList<>(PHANTASM_PROGRESS.keySet())) {
            Long lastSeen = PHANTASM_LAST_SEEN.get(id);
            if (lastSeen == null || (now - lastSeen) > 1500) {
                PHANTASM_PROGRESS.computeIfPresent(id, (k, val) -> {
                    int next = val - 5;
                    return next <= 0 ? null : next;
                });
            }
        }
    }
}
