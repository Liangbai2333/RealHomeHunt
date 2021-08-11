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

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import site.liangbai.realhomehunt.internal.command.subcommand.ISubCommand;
import site.liangbai.realhomehunt.common.config.Config;
import site.liangbai.realhomehunt.api.locale.impl.Locale;
import site.liangbai.realhomehunt.api.locale.manager.LocaleManager;
import site.liangbai.realhomehunt.api.residence.manager.ResidenceManager;
import site.liangbai.realhomehunt.api.residence.Residence;
import site.liangbai.realhomehunt.internal.task.PlayerBackTask;
import site.liangbai.realhomehunt.util.Locations;

import java.util.HashSet;
import java.util.Set;

public final class BackCommand implements ISubCommand {
    private final Set<String> teleportPlayers = new HashSet<>();

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) return;

        Player player = ((Player) sender);

        if (teleportPlayers.contains(player.getName())) return;

        Locale locale = LocaleManager.require(player);

        String name = player.getName();

        Residence residence;

        if (args.length > 1) {
            String other = args[1];

            residence = ResidenceManager.getResidenceByOwner(other);

            if (residence == null) {
                sender.sendMessage(locale.asString("command.back.other.notHaveResidence"));

                return;
            }

            if (!residence.isAdministrator(name) && !player.hasPermission("rh.unlimited.back")) {
                sender.sendMessage(locale.asString("command.back.other.notHavePermission"));

                return;
            }

        } else {
            residence = ResidenceManager.getResidenceByOwner(name);

            if (residence == null) {
                sender.sendMessage(locale.asString("command.back.self.notHaveResidence", label));

                return;
            }
        }

        if (residence.getSpawn() == null) {
            sender.sendMessage(locale.asString("command.back.self.notHaveSpawnPoint"));

            return;
        }

        if (player.hasPermission("rh.unlimited.back")) {
            Locations.teleportAfterChunkLoaded(player, residence.getSpawn());

            return;
        }

        String doneMessage = locale.asString("command.back.teleport.doneTitle", residence.getOwner());

        String denyMessage = locale.asString("command.back.teleport.denyTitle", residence.getOwner());

        String titleMessage = locale.asString("command.back.teleport.waitTitle", residence.getOwner());

        String teleportFormatSubTitleMessage = locale.asString("command.back.teleport.waitSubTitle", "%s");

        long seconds = Config.teleportMills / 20;

        teleportPlayers.add(player.getName());

        PlayerBackTask.tryTeleportPlayer(player, residence.getSpawn(), doneMessage, denyMessage, titleMessage, teleportFormatSubTitleMessage, seconds, it -> teleportPlayers.remove(it.getName()));
    }
}
