package site.liangbai.realhomehunt.util;

import com.craftingdead.core.item.GunItem;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import site.liangbai.realhomehunt.config.Config;

public final class Guns {
    public static int countBlockSit(double count, double hardness) {
        int realHardness = (int) ((count / hardness) * 9);

        return realHardness > 9 || realHardness < 0 ? -1 : realHardness;
    }

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
