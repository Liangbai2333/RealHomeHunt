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
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.scheduler.BukkitRunnable;
import site.liangbai.realhomehunt.RealHomeHuntPlugin;
import site.liangbai.realhomehunt.api.residence.Residence;
import site.liangbai.realhomehunt.common.config.Config;
import site.liangbai.realhomehunt.util.Blocks;

/**
 * The type Auto fix block task.
 *
 * @author Liangbai
 * @since 2021 /08/10 11:32 上午
 */
public final class AutoFixBlockTask extends BukkitRunnable {
    private final Residence residence;
    private final BlockState blockState;
    private final BlockData blockData;
    private final Location location;

    public AutoFixBlockTask(Residence residence, BlockState blockState, BlockData blockData, Location location) {
        this.residence = residence;
        this.blockState = blockState;
        this.blockData = blockData;
        this.location = location;
    }

    @Override
    public void run() {
        if (Config.autoFixResidence.ignoreEnemy || !residence.hasEnemyIn()) {
            Block block = location.getBlock();

            if (needFixed(block)) {
                block.setBlockData(blockData);

                Blocks.applyPlaceBlock(block);

                Blocks.applyBlockState(block.getState(), blockState);
            }
        } else
            submit(residence, blockState, blockData, location);
    }

    private static boolean needFixed(Block block) {
        Material type = block.getType();

        return type.isAir() || (!type.isSolid() && !type.isItem());
    }

    public static void submit(Residence residence, BlockState snapshotState, BlockData blockData, Location location) {
        new AutoFixBlockTask(residence, snapshotState, blockData, location).runTaskLater(RealHomeHuntPlugin.getInst(), Config.autoFixResidence.perBlockFixedMills);
    }
}
