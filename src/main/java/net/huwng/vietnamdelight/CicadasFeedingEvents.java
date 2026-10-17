package net.huwng.vietnamdelight;

import net.huwng.vietnamdelight.init.VietnamDelightModItems;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Cho phép cầm item Cicadas (ve sầu) cho Gà / Ếch / Vẹt ăn trực tiếp bằng chuột phải,
 * y hệt cách cầm hạt (seed) cho gà ăn để nó bắt đầu đẻ:
 * <p>
 * - Gà, Ếch (đã trưởng thành, chưa trong love mode): ăn ve sầu -> vào "love mode" giống hệt
 *   khi ăn đúng thức ăn sinh sản vanilla của chúng (gà: hạt giống, ếch: slimeball), tự đi tìm
 *   nhau để sinh sản (gà đẻ con/ trứng, ếch đẻ trứng ếch xuống nước gần đó).
 * - Vẹt (chưa được thuần hoá): ăn ve sầu có 1/10 cơ hội được thuần, giống việc cho vẹt ăn hạt
 *   giống trong vanilla (vẹt không sinh sản được nên chỉ có thể thuần hoá).
 * <p>
 * Không cần sửa isFood()/thẻ (tag) của các mob vanilla - chặn thẳng sự kiện chuột phải lên
 * entity khi vật phẩm đang cầm là Cicadas rồi tự xử lý logic tương ứng.
 */
@Mod.EventBusSubscriber(modid = "vietnam_delight")
public class CicadasFeedingEvents {

    @SubscribeEvent
    public static void onFeedWithCicadas(PlayerInteractEvent.EntityInteract event) {
        ItemStack stack = event.getItemStack();
        if (!stack.is(VietnamDelightModItems.CICADAS.get())) return;
        if (event.getLevel().isClientSide()) return;

        Player player = event.getEntity();

        if (event.getTarget() instanceof Chicken chicken) {
            feedForLove(chicken, player, stack, event);
        } else if (event.getTarget() instanceof Frog frog) {
            feedForLove(frog, player, stack, event);
        } else if (event.getTarget() instanceof Parrot parrot && !parrot.isTame()) {
            feedParrotForTame(parrot, player, stack, event);
        }
    }

    private static void feedForLove(Animal animal, Player player, ItemStack stack, PlayerInteractEvent.EntityInteract event) {
        if (animal.isBaby() || animal.isInLove()) return;

        if (!player.isCreative()) {
            stack.shrink(1);
        }
        // setInLove kích hoạt luôn hiệu ứng trái tim + toàn bộ logic sinh sản gốc của mob
        // (gà đẻ/ nở con, ếch đi tìm nước để đẻ trứng ếch...) mà không cần đụng tới isFood().
        animal.setInLove(player);
        animal.level().playSound(null, animal.blockPosition(), SoundEvents.GENERIC_EAT, SoundSource.NEUTRAL, 1.0F, 1.0F);

        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
    }

    private static void feedParrotForTame(Parrot parrot, Player player, ItemStack stack, PlayerInteractEvent.EntityInteract event) {
        if (!player.isCreative()) {
            stack.shrink(1);
        }

        if (parrot.level() instanceof ServerLevel serverLevel) {
            if (parrot.getRandom().nextInt(10) == 0) {
                parrot.tame(player);
                serverLevel.sendParticles(ParticleTypes.HEART, parrot.getX(), parrot.getY() + 0.5, parrot.getZ(), 5, 0.3, 0.3, 0.3, 0.0);
            } else {
                serverLevel.sendParticles(ParticleTypes.SMOKE, parrot.getX(), parrot.getY() + 0.5, parrot.getZ(), 5, 0.3, 0.3, 0.3, 0.0);
            }
        }
        parrot.level().playSound(null, parrot.blockPosition(), SoundEvents.GENERIC_EAT, SoundSource.NEUTRAL, 1.0F, 1.0F);

        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
    }
}
