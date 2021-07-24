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
<<<<<<< HEAD
import org.bukkit.entity.Player;
=======
>>>>>>> b0e20feb0f34a730ef4c8abed901bfc4e4e16869
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import site.liangbai.realhomehunt.command.subtabcompiler.ISubTabCompiler;
import site.liangbai.realhomehunt.command.subtabcompiler.impl.AdministratorTabCompiler;
import site.liangbai.realhomehunt.command.subtabcompiler.impl.BackTabCompiler;
import site.liangbai.realhomehunt.command.subtabcompiler.impl.SetTabCompiler;
import site.liangbai.realhomehunt.command.subtabcompiler.impl.admin.AdminTabCompiler;
<<<<<<< HEAD
import site.liangbai.realhomehunt.confirm.ConfirmModule;
=======
>>>>>>> b0e20feb0f34a730ef4c8abed901bfc4e4e16869

import java.util.HashMap;
import java.util.List;
import java.util.Map;
<<<<<<< HEAD
import java.util.stream.Collectors;
=======
>>>>>>> b0e20feb0f34a730ef4c8abed901bfc4e4e16869

public final class CommandTabCompiler implements TabCompleter {
    private static final Map<String, ISubTabCompiler> map = new HashMap<>();

    public CommandTabCompiler() {
        registerSubCompiler("administrator", new AdministratorTabCompiler());

        registerSubCompiler("back", new BackTabCompiler());

        registerSubCompiler("set", new SetTabCompiler());

        registerSubCompiler("admin", new AdminTabCompiler());
    }

    private void registerSubCompiler(String command, ISubTabCompiler iSubTabCompiler) {
        map.put(command.toLowerCase(), iSubTabCompiler);
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (strings.length == 1) {
<<<<<<< HEAD
            return CommandHandler.getSubCommands().stream()
                    .filter(it -> {
                        if (it.equals("confirm")) {
                            return commandSender instanceof Player && ConfirmModule.hasConfirmCache((Player) commandSender);
                        }

                        return true;
                    }).collect(Collectors.toList());
=======
            return CommandHandler.getSubCommands();
>>>>>>> b0e20feb0f34a730ef4c8abed901bfc4e4e16869
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
