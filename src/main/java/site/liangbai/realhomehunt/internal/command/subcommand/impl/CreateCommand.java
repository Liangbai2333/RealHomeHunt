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

package site.liangbai.realhomehunt.internal.command.subcommand.impl;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import site.liangbai.realhomehunt.api.cache.SelectCache;
import site.liangbai.realhomehunt.api.event.residence.ResidenceCreateEvent;
import site.liangbai.realhomehunt.common.config.Config;
import site.liangbai.realhomehunt.util.*;
import site.liangbai.realhomehunt.internal.command.subcommand.ISubCommand;
import site.liangbai.realhomehunt.api.locale.impl.Locale;
import site.liangbai.realhomehunt.api.locale.manager.LocaleManager;
import site.liangbai.realhomehunt.api.residence.manager.ResidenceManager;
import site.liangbai.realhomehunt.api.residence.Residence;

import java.util.Objects;

public final class CreateCommand implements ISubCommand {
    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) return;

        Player player = ((Player) sender);

        Locale locale = LocaleManager.require(player);

        if (!ResidenceManager.isOpened(player.getWorld())) {
            sender.sendMessage(locale.asString("command.create.notOpened"));

            return;
        }

        String name = sender.getName();

        Residence old = ResidenceManager.getResidenceByOwner(name);

        if (old != null) {
            SelectCache.pop(player);

            sender.sendMessage(locale.asString("command.create.hasOld"));

            return;
        }

        Location loc1 = SelectCache.require(SelectCache.SelectType.FIRST, name);
        Location loc2 = SelectCache.require(SelectCache.SelectType.SECOND, name);

        if (loc1 == null || loc2 == null) {
            sender.sendMessage(locale.asString("command.create.notSelectZone"));

            return;
        }

        if (!Objects.equals(loc1.getWorld(), loc2.getWorld())) {
            sender.sendMessage(locale.asString("command.create.pointsNotInSameWorld"));

            return;
        }

        if (!ResidenceManager.isOpened(loc1.getWorld())) {
            sender.sendMessage(locale.asString("command.create.notOpened"));

            return;
        }

        Locations.DistanceInfo distanceInfo = Locations.countDistanceInfo(loc1, loc2);

        Config.ResidenceSetting.ResidenceSizeSetting sizeSetting = Config.residence.sizeLimit;

        if (!sender.hasPermission("rh.unlimited.create")) {
            if (distanceInfo.getX() > sizeSetting.x ||
                            distanceInfo.getY() > sizeSetting.y ||
                            distanceInfo.getZ() > sizeSetting.z
            ) {
                sender.sendMessage(locale.asString("command.create.tooLargeZone", sizeSetting.x, sizeSetting.y, sizeSetting.z));

                return;
            }
        }

        if (ResidenceManager.containsResidence(loc1, loc2)) {
            SelectCache.pop(player);

            sender.sendMessage(locale.asString("command.create.containsOther"));

            return;
        }

        Residence residence = new Residence.Builder().owner(player).left(loc1).right(loc2).build();

        ResidenceCreateEvent.Pre preEvent = new ResidenceCreateEvent.Pre(player, residence);

        if (!preEvent.callEvent()) return;

        if (!sender.hasPermission("rh.unlimited.create") && !preEvent.isCheckBlock()) {
            for (Config.BlockSetting.BlockIgnoreSetting.IgnoreBlockInfo info : Config.block.ignore.ignoreBlockInfoList) {
                long count = Blocks.containsBlockAndReturnCount(info, residence);

                if (count > info.amount) {
                    sender.sendMessage(locale.asString("command.create.containsIgnoreBlock", info.full != null ? info.full : (info.suffix.isEmpty() ? info.prefix : info.suffix)));

                    return;
                }
            }
        }

        Location defaultSpawn = Locations.getAverageLocation(loc1.getWorld(), loc1, loc2);

        residence.setSpawn(defaultSpawn);

        if (!new ResidenceCreateEvent.Post(player, residence).callEvent()) return;

        ResidenceManager.register(residence);

        residence.save();

        SelectCache.pop(player);

        Sounds.playLevelUpSound(player, 1, 0);

        Titles.sendTitle(player, locale.asString("command.create.successTitle"), locale.asString("command.create.successSubTitle"));

        Messages.sendToAll("command.create.sendToAll", name);
    }
}
