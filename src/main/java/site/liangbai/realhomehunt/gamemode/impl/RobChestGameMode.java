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

package site.liangbai.realhomehunt.gamemode.impl;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import site.liangbai.realhomehunt.cache.DamageCachePool;
import site.liangbai.realhomehunt.config.Config;
import site.liangbai.realhomehunt.gamemode.IGameMode;
import site.liangbai.realhomehunt.residence.Residence;
import site.liangbai.realhomehunt.task.NaturallyDropItemTask;
import site.liangbai.realhomehunt.util.Chances;
import site.liangbai.realhomehunt.util.callback.ICallBack;

import java.util.ArrayList;
import java.util.List;

public class RobChestGameMode implements IGameMode {
    @Override
    public boolean isEnabled() {
        return Config.robChestMode.enabled;
    }

    @Override
    public void process(ICallBack<Boolean> dropBlockItem, Residence residence, Player player, ItemStack gun, Block block, BlockData blockData, DamageCachePool.DamageCache damageCache) {
        BlockState blockState = block.getState();

        if (!(blockState instanceof Container)) return;

        Container container = ((Container) blockState);

        Inventory inventory = container.getSnapshotInventory();

        container.getInventory().clear();

        List<ItemStack> dropItems = new ArrayList<>();

        inventory.forEach(itemStack -> {
            if (itemStack == null || itemStack.getType().isAir()) return;

            double chance = Config.robChestMode.dropItem.getChance(itemStack);

            if (Chances.hasChance(chance)) {
                dropItems.add(itemStack);
            }
        });

        Location dropLocation = damageCache.getBlock().getLocation();

        NaturallyDropItemTask.setup(dropItems, dropLocation);

        dropBlockItem.set(false);
    }
}
