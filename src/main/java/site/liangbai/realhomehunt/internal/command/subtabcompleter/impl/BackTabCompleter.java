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

package site.liangbai.realhomehunt.internal.command.subtabcompleter.impl;

import org.bukkit.command.CommandSender;
import site.liangbai.realhomehunt.internal.command.subtabcompleter.ISubTabCompleter;
import site.liangbai.realhomehunt.api.residence.manager.ResidenceManager;
import site.liangbai.realhomehunt.api.residence.Residence;

import java.util.List;
import java.util.stream.Collectors;

public final class BackTabCompleter implements ISubTabCompleter {
    @Override
    public List<String> handle(CommandSender sender, int length, String[] args) {
        if (length == 2) {
            if (sender.hasPermission("rh.unlimited.back")) {
                return ResidenceManager.getResidences()
                        .stream()
                        .map(Residence::getOwner)
                        .collect(Collectors.toList());
            }

            return ResidenceManager.getResidences().stream()
                    .filter(it -> it.isAdministrator(sender.getName()) && !it.isOwner(sender.getName()) && !sender.hasPermission("rh.unlimited.back"))
                    .map(Residence::getOwner)
                    .collect(Collectors.toList());
        }

        return null;
    }
}