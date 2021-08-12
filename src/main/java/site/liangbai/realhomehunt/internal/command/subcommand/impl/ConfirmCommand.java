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
import site.liangbai.realhomehunt.common.confirm.IConfirmProcessor;
import site.liangbai.realhomehunt.internal.command.subcommand.ISubCommand;
import site.liangbai.realhomehunt.common.confirm.ConfirmModule;
import site.liangbai.realhomehunt.api.locale.impl.Locale;
import site.liangbai.realhomehunt.api.locale.manager.LocaleManager;
import site.liangbai.realhomehunt.api.residence.Residence;
import site.liangbai.realhomehunt.util.Pair;

public final class ConfirmCommand implements ISubCommand {
    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) return;

        Player player = ((Player) sender);

        if (!ConfirmModule.hasConfirmCache(player)) {
            Locale locale = LocaleManager.require(player);

            player.sendMessage(locale.asString("command.confirm.nothing"));

            return;
        }

        Pair<Residence, IConfirmProcessor> pair = ConfirmModule.getConfirmProcessor(player);

        pair.getValue().process(player, pair.getKey());
    }
}
