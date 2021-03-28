package site.liangbai.realhomehunt.util;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import site.liangbai.craftingdeadapi.api.gun.Gun;
import site.liangbai.realhomehunt.config.Config;

public final class GunUtil {
    public static int countBlockSit(double count, double hardness) {
        int realHardness = (int) ((count / hardness) * 9);

        return realHardness > 9 || realHardness < 0 ? -1 : realHardness;
    }

    public static double countDamage(@NotNull ItemStack gun) {
        Gun gunApi = new Gun(gun);

        if (!gunApi.isGun()) return 0.0D;

        int powerLevel = gun.getEnchantmentLevel(Enchantment.ARROW_DAMAGE);

        return (gunApi.getFinalDamage() + (Config.perPowerLevelDamage * powerLevel)) / Config.gunDamageMultiple;
    }
}
