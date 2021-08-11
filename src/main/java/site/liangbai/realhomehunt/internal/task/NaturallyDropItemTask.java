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

package site.liangbai.realhomehunt.internal.task;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import site.liangbai.realhomehunt.RealHomeHunt;

import java.util.List;

public class NaturallyDropItemTask extends BukkitRunnable {
    private final List<ItemStack> dropItems;
    private final Location dropLocation;

    public NaturallyDropItemTask(List<ItemStack> dropItems, Location dropLocation) {
        this.dropItems = dropItems;
        this.dropLocation = dropLocation;
    }

    @Override
    public void run() {
        World world = dropLocation.getWorld();

        dropItems.forEach(itemStack -> world.dropItem(dropLocation, itemStack));
    }

    public static void setup(List<ItemStack> dropItems, Location dropLocation) {
        new NaturallyDropItemTask(dropItems, dropLocation).runTaskLater(RealHomeHunt.getInst(), 1);
    }
}
