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

package site.liangbai.realhomehunt.command.subcommand.impl;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import site.liangbai.realhomehunt.api.event.residence.ResidenceAdministratorEvent;
import site.liangbai.realhomehunt.command.subcommand.ISubCommand;
import site.liangbai.realhomehunt.api.locale.impl.Locale;
import site.liangbai.realhomehunt.api.locale.manager.LocaleManager;
import site.liangbai.realhomehunt.api.residence.manager.ResidenceManager;
import site.liangbai.realhomehunt.api.residence.Residence;

import java.util.regex.Pattern;

public final class AdministratorCommand implements ISubCommand {
    private static final Pattern booleanPattern = Pattern.compile("true|false");

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) return;

        Player player = ((Player) sender);

        Locale locale = LocaleManager.require(player);

        if (args.length < 3) {
            sender.sendMessage(locale.asString("command.administrator.usage", label));

            return;
        }

        String admin = args[1];

        if (!booleanPattern.matcher(args[2]).matches()) {
            sender.sendMessage(locale.asString("command.administrator.unknownParam"));

            return;
        }

        boolean param = Boolean.parseBoolean(args[2]);

        String name = player.getName();

        Residence residence = ResidenceManager.getResidenceByOwner(name);

        if (residence == null) {
            sender.sendMessage(locale.asString("command.administrator.haveNotResidence"));

            return;
        }

        if (param) {
            if (residence.isAdministrator(admin)) {
                sender.sendMessage(locale.asString("command.administrator.alreadyIsAdministrator", admin));

                return;
            }

            if (!new ResidenceAdministratorEvent.Give(residence, player, admin).callEvent()) return;

            residence.addAdministrator(admin);

            sender.sendMessage(locale.asString("command.administrator.successGivePermission", admin));
        } else {
            if (!residence.isAdministrator(admin)) {
                sender.sendMessage(locale.asString("command.administrator.notIsAdministrator", admin));

                return;
            }

            if (!new ResidenceAdministratorEvent.Remove(residence, player, admin).callEvent()) return;

            residence.removeAdministrator(admin);

            sender.sendMessage(locale.asString("command.administrator.successDeletePermission", admin));
        }

        residence.save();
    }
}
