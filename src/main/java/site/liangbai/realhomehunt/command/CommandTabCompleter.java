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

package site.liangbai.realhomehunt.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import site.liangbai.realhomehunt.command.subtabcompleter.ISubTabCompleter;
import site.liangbai.realhomehunt.command.subtabcompleter.impl.AdministratorTabCompleter;
import site.liangbai.realhomehunt.command.subtabcompleter.impl.BackTabCompleter;
import site.liangbai.realhomehunt.command.subtabcompleter.impl.InfoTabCompleter;
import site.liangbai.realhomehunt.command.subtabcompleter.impl.SetTabCompleter;
import site.liangbai.realhomehunt.command.subtabcompleter.impl.admin.AdminTabCompleter;
import site.liangbai.realhomehunt.confirm.ConfirmModule;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@site.liangbai.dynamic.command.CommandTabCompleter(command = "rh")
public final class CommandTabCompleter implements TabCompleter {
    private static final Map<String, ISubTabCompleter> map = new HashMap<>();

    public CommandTabCompleter() {
        registerSubCompiler("administrator", new AdministratorTabCompleter());

        registerSubCompiler("back", new BackTabCompleter());

        registerSubCompiler("set", new SetTabCompleter());

        registerSubCompiler("info", new InfoTabCompleter());

        registerSubCompiler("admin", new AdminTabCompleter());
    }

    private void registerSubCompiler(String command, ISubTabCompleter iSubTabCompiler) {
        map.put(command.toLowerCase(), iSubTabCompiler);
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (strings.length == 1) {
            return CommandHandler.getSubCommands().stream()
                    .filter(it -> {
                        if (it.equals("confirm")) {
                            return commandSender instanceof Player && ConfirmModule.hasConfirmCache((Player) commandSender);
                        }

                        return true;
                    }).collect(Collectors.toList());
        }

        if (strings.length > 1) {
            String commandName = strings[0].toLowerCase();

            if (map.containsKey(commandName)) {
                return map.get(commandName).handle(commandSender, strings.length, strings);
            }
        }

        return null;
    }
}
