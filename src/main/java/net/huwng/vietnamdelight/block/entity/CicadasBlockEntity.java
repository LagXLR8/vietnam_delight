package net.huwng.vietnamdelight.block.entity;

import net.huwng.vietnamdelight.init.VietnamDelightModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * BlockEntity của Cicadas (ve sầu), dùng để lưu:
 * - cooldown 5 phút giữa các lần couple_cicadas (varient "couple") sinh ra ve sầu con quanh cây phượng.
 * - mốc thời gian tiếp theo mà happy_cicadas (varient "happy") sẽ ngẫu nhiên bonemeal 1 crop gần đó.
 * Các varient khác (normal/nerd/rich/sad) không dùng tới 2 field này nhưng vẫn dùng chung 1 loại
 * block entity cho cả block để đơn giản hoá.
 */
public class CicadasBlockEntity extends BlockEntity {
    private long cooldownEndTime = 0L;
    private long nextHappyActionTime = 0L;

    public CicadasBlockEntity(BlockPos pos, BlockState state) {
        super(VietnamDelightModBlockEntities.CICADAS.get(), pos, state);
    }

    /** Thời điểm (level.getGameTime(), tick) mà cooldown kết thúc; nếu game time hiện tại &lt; giá trị này thì vẫn đang cooldown. */
    public long getCooldownEndTime() {
        return cooldownEndTime;
    }

    public void setCooldownEndTime(long cooldownEndTime) {
        this.cooldownEndTime = cooldownEndTime;
    }

    /** Thời điểm (tick) tiếp theo mà happy_cicadas sẽ thử bonemeal 1 crop ngẫu nhiên quanh nó. */
    public long getNextHappyActionTime() {
        return nextHappyActionTime;
    }

    public void setNextHappyActionTime(long nextHappyActionTime) {
        this.nextHappyActionTime = nextHappyActionTime;
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.cooldownEndTime = tag.getLong("CooldownEndTime");
        this.nextHappyActionTime = tag.getLong("NextHappyActionTime");
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putLong("CooldownEndTime", this.cooldownEndTime);
        tag.putLong("NextHappyActionTime", this.nextHappyActionTime);
    }
}
