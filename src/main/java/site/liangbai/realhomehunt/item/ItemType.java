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

package site.liangbai.realhomehunt.item;

import com.craftingdead.core.world.item.ClothingItem;
import com.craftingdead.core.world.item.GunItem;
import com.craftingdead.core.world.item.MagazineItem;
import com.craftingdead.core.world.item.StorageItem;
import net.minecraft.item.ItemStack;
import site.liangbai.realhomehunt.config.Config;
import site.liangbai.realhomehunt.util.ItemStacks;

import java.util.Arrays;
import java.util.function.Predicate;

public enum ItemType {
    GUN(it -> it.getItem() instanceof GunItem, 0.1),
    MAGAZINE(it -> it.getItem() instanceof MagazineItem, 0.3),
    STORAGE(it -> it.getItem() instanceof StorageItem, 0.3),
    CLOTHING(it -> it.getItem() instanceof ClothingItem, 0.3),
    // Must be last.
    GLOBAL(it -> true, 0.5);

    private final Predicate<ItemStack> matcher;
    private final double defaultChance;

    ItemType(Predicate<ItemStack> matcher, double defaultChance) {
        this.matcher = matcher;
        this.defaultChance = defaultChance;
    }

    public boolean isType(ItemStack itemStack) {
        return matcher.test(itemStack);
    }

    public double getChance() {
        Config.RobChestModeSetting.DropItemSetting setting = Config.robChestMode.dropItem;

        if (setting.itemTypeToDoubleChanceEnumMap.containsKey(this)) {
            return setting.itemTypeToDoubleChanceEnumMap.get(this);
        }

        return defaultChance;
    }

    public static ItemType matches(org.bukkit.inventory.ItemStack itemStack) {
        ItemStack copy = ItemStacks.getMinecraftItemStack(itemStack);

        return Arrays.stream(values())
                .filter(it -> it.isType(copy))
                .findFirst()
                .orElse(GLOBAL);

    }

    public static ItemType matchType(String type) {
        return Arrays.stream(values())
                .filter(it -> it.name().equalsIgnoreCase(type))
                .findFirst()
                .orElse(null);
    }
}
