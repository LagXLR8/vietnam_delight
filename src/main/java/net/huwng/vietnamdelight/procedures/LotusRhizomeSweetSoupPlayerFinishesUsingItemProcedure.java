package net.huwng.vietnamdelight.procedures;

import net.minecraft.world.entity.Entity;

public class LotusRhizomeSweetSoupPlayerFinishesUsingItemProcedure {
    public static void execute(Entity entity) {
        if (entity == null)
            return;

        if (entity.isOnFire()) {
            entity.clearFire(); // Dập tắt lửa
        }
    }
}
