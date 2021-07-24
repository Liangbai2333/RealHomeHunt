/*
 * RealHomeHunt
 * Copyright (C) 2021  Liangbai
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package site.liangbai.realhomehunt.util;

<<<<<<< HEAD
import com.craftingdead.core.world.item.GunItem;
=======
import com.craftingdead.core.item.GunItem;
>>>>>>> b0e20feb0f34a730ef4c8abed901bfc4e4e16869
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import site.liangbai.realhomehunt.config.Config;

public final class Guns {
    public static int countBlockSit(double count, double hardness) {
<<<<<<< HEAD
        int realHardness = (int) (getHardnessMix(count, hardness) * 9);
=======
        int realHardness = (int) ((count / hardness) * 9);
>>>>>>> b0e20feb0f34a730ef4c8abed901bfc4e4e16869

        return realHardness > 9 || realHardness < 0 ? -1 : realHardness;
    }

<<<<<<< HEAD
    public static double getHardnessMix(double count, double hardness) {
        return count / hardness;
    }

    public static int getHardnessMixPercent(double count, double hardness) {
        int percent = (int) (getHardnessMix(count, hardness) * 100);

        if (percent < 0) {
            percent = 0;
        } else if (percent > 100) {
            percent = 100;
        }

        return percent;
    }

=======
>>>>>>> b0e20feb0f34a730ef4c8abed901bfc4e4e16869
    public static double countDamage(@NotNull ItemStack gun) {
        net.minecraft.item.ItemStack itemStack = ItemStacks.getMinecraftItemStack(gun);

        if (itemStack == null) return 0.0D;

        if (!isGun(itemStack)) return 0.0D;

        GunItem gunItem = (GunItem) itemStack.getItem();

        int powerLevel = gun.getEnchantmentLevel(Enchantment.ARROW_DAMAGE);

        return (gunItem.getGunType().getDamage() + (Config.perPowerLevelDamage * powerLevel)) / Config.gunDamageMultiple;
    }

    public static boolean isGun(net.minecraft.item.ItemStack itemStack) {
        return itemStack.getItem() instanceof GunItem;
    }
}
