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

package site.liangbai.realhomehunt.command.subtabcompleter.impl.admin.subtabcompleter;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import site.liangbai.realhomehunt.api.residence.manager.ResidenceManager;
import site.liangbai.realhomehunt.command.subtabcompleter.ISubTabCompleter;

import java.util.List;
import java.util.stream.Collectors;

/**
 * The type Create tab completer.
 *
 * @author Liangbai
 * @since 2021 /08/10 04:03 下午
 */
public class CreateTabCompleter implements ISubTabCompleter {
    @Override
    public List<String> handle(CommandSender sender, int length, String[] args) {
        if (length == 3) {
            return Bukkit.getOnlinePlayers().stream()
                    .parallel()
                    .map(Player::getName)
                    .filter(it -> ResidenceManager.getResidenceByOwner(it) == null)
                    .collect(Collectors.toList());
        }

        return null;
    }
}
