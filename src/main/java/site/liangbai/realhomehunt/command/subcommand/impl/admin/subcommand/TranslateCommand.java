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
import org.bukkit.command.CommandSender;
import site.liangbai.realhomehunt.api.locale.impl.Locale;
import site.liangbai.realhomehunt.api.residence.Residence;
import site.liangbai.realhomehunt.api.residence.manager.ResidenceManager;
import site.liangbai.realhomehunt.command.subcommand.ISubCommand;
import site.liangbai.realhomehunt.util.Locales;
import site.liangbai.realhomehunt.util.Locations;

/**
 * The type Translate command.
 *
 * @author Liangbai
 * @since 2021 /08/10 04:03 下午
 */
public final class TranslateCommand implements ISubCommand {
    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!Bukkit.getPluginManager().isPluginEnabled("Residence")) return;

        Locale locale = Locales.require(sender);

        if (args.length < 3) {
            sender.sendMessage(locale.asString("command.admin.translate.usage", label));

            return;
        }

        ResidencePlayer residencePlayer = ResidenceApi.getPlayerManager().getResidencePlayer(args[2]);

        ClaimedResidence claimedResidence = residencePlayer.getMainResidence();

        CuboidArea area;

        if (claimedResidence == null || (area = claimedResidence.getMainArea()) == null) {
            sender.sendMessage(locale.asString("command.admin.translate.failed", args[2]));

            return;
        }

        Residence residence = new Residence.Builder().owner(args[2]).left(area.getHighLocation()).right(area.getLowLocation()).build();

        Location defaultSpawn = Locations.getAverageLocation(residence.getLeft().getWorld(), residence.getLeft(), residence.getRight());

        residence.setSpawn(defaultSpawn);

        ResidenceManager.register(residence);

        residence.save();

        sender.sendMessage(locale.asString("command.admin.translate.success", args[2]));
    }
}
