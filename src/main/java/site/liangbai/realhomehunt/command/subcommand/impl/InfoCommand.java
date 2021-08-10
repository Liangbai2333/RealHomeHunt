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
import site.liangbai.realhomehunt.api.locale.impl.Locale;
import site.liangbai.realhomehunt.api.residence.Residence;
import site.liangbai.realhomehunt.api.residence.manager.ResidenceManager;
import site.liangbai.realhomehunt.command.subcommand.ISubCommand;
import site.liangbai.realhomehunt.util.Locales;

/**
 * The type Info command.
 *
 * @author Liangbai
 * @since 2021 /08/10 02:30 下午
 */
public final class InfoCommand implements ISubCommand {
    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        Locale locale = Locales.require(sender);

        if (args.length < 2) {
            if (!(sender instanceof Player)) return;

            Residence residence = ResidenceManager.getResidenceByOwner(sender.getName());

            if (residence == null) {
                sender.sendMessage(locale.asString("command.info.self.haveNotResidence"));

                return;
            }

            sender.sendMessage(locale.asString("command.info.self.show", residence.getOwner(), residence.getAdministrators()));
        } else {
            Residence residence = ResidenceManager.getResidenceByOwner(args[1]);

            if (residence == null) {
                sender.sendMessage(locale.asString("command.info.other.haveNotResidence"));

                return;
            }

            sender.sendMessage(locale.asString("command.info.other.show", args[1], residence.getOwner(), residence.getAdministrators()));
        }
    }
}
