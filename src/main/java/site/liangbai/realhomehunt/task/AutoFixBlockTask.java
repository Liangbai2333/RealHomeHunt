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

package site.liangbai.realhomehunt.task;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.scheduler.BukkitRunnable;
import site.liangbai.realhomehunt.RealHomeHunt;
import site.liangbai.realhomehunt.config.Config;
import site.liangbai.realhomehunt.api.residence.Residence;

public final class AutoFixBlockTask extends BukkitRunnable {
    private final Residence residence;
    private final BlockData blockData;
    private final Location location;

    public AutoFixBlockTask(Residence residence, BlockData blockData, Location location) {
        this.residence = residence;
        this.blockData = blockData;
        this.location = location;
    }

    @Override
    public void run() {
        if (Config.autoFixResidence.ignoreEnemy || !residence.hasEnemyIn()) {
            Block block = location.getBlock();
            
            if (needFixed(block)) block.setBlockData(blockData);
        } else
            submit(residence, blockData, location);
    }

    private static boolean needFixed(Block block) {
        Material type = block.getType();

        return type.isAir() || (!type.isSolid() && !type.isItem());
    }

    public static void submit(Residence residence, BlockData blockData, Location location) {
        new AutoFixBlockTask(residence, blockData, location).runTaskLater(RealHomeHunt.getInst(), Config.autoFixResidence.perBlockFixedMills);
    }
}
