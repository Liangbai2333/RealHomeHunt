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

package site.liangbai.realhomehunt.command.subcommand.impl.admin.subcommand;

import com.bekvon.bukkit.residence.api.ResidenceApi;
import com.bekvon.bukkit.residence.containers.ResidencePlayer;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.bekvon.bukkit.residence.protection.CuboidArea;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import site.liangbai.realhomehunt.api.locale.impl.Locale;
import site.liangbai.realhomehunt.api.residence.Residence;
import site.liangbai.realhomehunt.api.residence.manager.ResidenceManager;
import site.liangbai.realhomehunt.command.subcommand.ISubCommand;
import site.liangbai.realhomehunt.util.Locales;
import site.liangbai.realhomehunt.util.Locations;

/**
 * The type Translate all command.
 *
 * @author Liangbai
 * @since 2021 /08/10 01:38 下午
 */
public class TranslateAllCommand implements ISubCommand {
    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) return;

        if (!Bukkit.getPluginManager().isPluginEnabled("Residence")) return;

        Locale locale = Locales.require(sender);

        int count = 0;

        int sucCount = 0;

        for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
            ResidencePlayer residencePlayer = ResidenceApi.getPlayerManager().getResidencePlayer(sender.getName());

            ClaimedResidence claimedResidence = residencePlayer.getMainResidence();

            if (claimedResidence != null) {
                count++;

                CuboidArea area = claimedResidence.getMainArea();

                if (area != null) {
                    Residence residence = new Residence.Builder().owner(offlinePlayer.getName()).left(area.getHighLocation()).right(area.getLowLocation()).build();

                    Location defaultSpawn = Locations.getAverageLocation(residence.getLeft().getWorld(), residence.getLeft(), residence.getRight());

                    residence.setSpawn(defaultSpawn);

                    ResidenceManager.register(residence);

                    residence.save();

                    sucCount++;
                }
            }
        }

        sender.sendMessage(locale.asString("command.admin.translateAll.info", count, sucCount, count - sucCount));
    }
}
