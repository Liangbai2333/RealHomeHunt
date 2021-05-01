package site.liangbai.realhomehunt.util;

import net.minecraft.item.ItemStack;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class ItemStackUtil {
    private static Method minecraftItemStackGetter;

    public static ItemStack getMinecraftItemStack(org.bukkit.inventory.ItemStack itemStack) {
        if (minecraftItemStackGetter == null) {
            try {
                minecraftItemStackGetter = CraftItemStack.class.getMethod("asNMSCopy", org.bukkit.inventory.ItemStack.class);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }

        try {
            return (ItemStack) minecraftItemStackGetter.invoke(null, itemStack);
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }
}
