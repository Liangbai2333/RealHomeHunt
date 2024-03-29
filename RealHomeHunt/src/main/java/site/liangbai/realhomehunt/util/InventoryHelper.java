/*
 * RealHomeHunt
 * Copyright (C) 2022  Liangbai
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

import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public final class InventoryHelper {
    /**
     * Copy to list.
     *
     * @param copyTo   the copy to
     * @param copyFrom the copy from
     * @return 剩余无法放置的物品堆
     */
    public static List<ItemStack> copyTo(Inventory copyTo, Inventory copyFrom) {
        List<ItemStack> list = Lists.newArrayList();

        for (int i = 0; i < copyFrom.getSize(); i++) {
            ItemStack itemStack = copyFrom.getItem(i);

            if (itemStack == null) continue;

            itemStack = itemStack.clone();

            ItemStack original = i >= copyTo.getSize() ? null : copyTo.getItem(i);

            if (i >= copyTo.getSize() || original != null) {
                int firstEmpty = copyTo.firstEmpty();

                if (firstEmpty == -1) {
                    for (ItemStack to : copyTo) {
                        if (to != null) {
                            if (to.isSimilar(itemStack)) {
                                int amount = to.getAmount();

                                int last = to.getMaxStackSize() - amount;

                                if (last <= 0) continue;

                                int add = itemStack.getAmount();

                                if (add > last) {
                                    to.setAmount(amount + last);

                                    itemStack.setAmount(add - last);
                                } else {
                                    to.setAmount(amount + add);

                                    itemStack = null;

                                    break;
                                }
                            }
                        }
                    }
                } else {
                    copyTo.setItem(firstEmpty, itemStack.clone());

                    itemStack = null;
                }
            } else {
                copyTo.setItem(i, itemStack.clone());

                itemStack = null;
            }

            if (itemStack != null) {
                list.add(itemStack);
            }
        }

        return list;
    }

    public static Inventory clone(Inventory fromInventory) {
        try {
            Inventory copyTo;

            if (fromInventory.getType() == InventoryType.CHEST) {
                copyTo = Bukkit.createInventory(fromInventory.getHolder(), fromInventory.getSize());
            } else {
                copyTo = Bukkit.createInventory(fromInventory.getHolder(), fromInventory.getType());
            }

            copyTo.setContents(fromInventory.getContents());

            return copyTo;
        } catch (Throwable throwable) {
            return null;
        }
    }
}
