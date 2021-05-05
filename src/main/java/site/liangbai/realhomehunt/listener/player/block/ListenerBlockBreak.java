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

package site.liangbai.realhomehunt.listener.player.block;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.jetbrains.annotations.NotNull;
import site.liangbai.lrainylib.annotation.Plugin;
import site.liangbai.realhomehunt.residence.Residence;
import site.liangbai.realhomehunt.config.Config;
import site.liangbai.realhomehunt.manager.ResidenceManager;

@Plugin.EventSubscriber
public final class ListenerBlockBreak implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (!ResidenceManager.isOpened(event.getPlayer().getWorld())) return;

        Residence residence = ResidenceManager.getResidenceByLocation(event.getBlock().getLocation());

        if (residence == null) return;

        if (!residence.isAdministrator(event.getPlayer()) && !event.getPlayer().hasPermission("rh.break")) event.setCancelled(true);

        Config.BlockSetting.BlockIgnoreSetting.IgnoreBlockInfo ignoreBlockInfo = Config.block.ignore.getByMaterial(event.getBlock().getType());

        if (ignoreBlockInfo != null) {
            Residence.IgnoreBlockInfo info = residence.getIgnoreBlockInfo(ignoreBlockInfo);

            if (info.getCount() > 0) {
                info.deleteCount();

                residence.save();
            }
        } else {

            Block upBlock = event.getBlock().getRelative(BlockFace.UP);

            saveUpBlock(upBlock, residence);
        }
    }

    public static void saveUpBlock(@NotNull Block block, Residence residence) {
        Material upType = block.getType();

        Config.BlockSetting.BlockIgnoreSetting.IgnoreBlockInfo info = Config.block.ignore.getByMaterial(upType);

        if (info != null) {
            if (!info.isUpBreak()) return;

            Residence.IgnoreBlockInfo residenceIgnoreBlockInfo = residence.getIgnoreBlockInfo(info);

            if (residenceIgnoreBlockInfo.getCount() > 0) {
                residenceIgnoreBlockInfo.deleteCount();

                residence.save();
            }
        }
    }
}
