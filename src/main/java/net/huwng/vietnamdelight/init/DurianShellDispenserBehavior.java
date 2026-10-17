package net.huwng.vietnamdelight.init;

import net.minecraft.core.Position;
import net.minecraft.core.dispenser.AbstractProjectileDispenseBehavior;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.huwng.vietnamdelight.entity.DurianShellProjectileEntity;
import net.huwng.vietnamdelight.entity.ExplosiveDurianShellProjectileEntity;

public class DurianShellDispenserBehavior {
    public static void register() {
        DispenserBlock.registerBehavior(VietnamDelightModItems.DURIAN_SHELL.get(), new AbstractProjectileDispenseBehavior() {
            @Override
            protected Projectile getProjectile(Level level, Position pos, ItemStack stack) {
                DurianShellProjectileEntity projectile = new DurianShellProjectileEntity(VietnamDelightModEntities.DURIAN_SHELL_PROJECTILE.get(), level);
                projectile.setPos(pos.x(), pos.y(), pos.z());
                projectile.setItem(stack);
                return projectile;
            }
        });

        DispenserBlock.registerBehavior(VietnamDelightModItems.EXPLOSIVE_DURIAN_SHELL.get(), new AbstractProjectileDispenseBehavior() {
            @Override
            protected Projectile getProjectile(Level level, Position pos, ItemStack stack) {
                ExplosiveDurianShellProjectileEntity projectile = new ExplosiveDurianShellProjectileEntity(VietnamDelightModEntities.EXPLOSIVE_DURIAN_SHELL_PROJECTILE.get(), level);
                projectile.setPos(pos.x(), pos.y(), pos.z());
                projectile.setItem(stack);
                return projectile;
            }
        });
    }
}
