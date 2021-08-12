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

package site.liangbai.realhomehunt.internal.command.subtabcompleter.impl.admin;

import org.bukkit.command.CommandSender;
import site.liangbai.realhomehunt.internal.command.subcommand.impl.admin.AdminCommand;
import site.liangbai.realhomehunt.internal.command.subtabcompleter.ISubTabCompleter;
import site.liangbai.realhomehunt.internal.command.subtabcompleter.impl.admin.subtabcompleter.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class AdminTabCompleter implements ISubTabCompleter {
    private static final Map<String, ISubTabCompleter> map = new HashMap<>();

    public AdminTabCompleter() {
        registerSubCompiler("import", new ImportTabCompleter());

        registerSubCompiler("remove", new RemoveTabCompleter());

        registerSubCompiler("create", new CreateTabCompleter());

        registerSubCompiler("translate", new TranslateTabCompleter());

        registerSubCompiler("set", new SetTabCompleter());

        registerSubCompiler("expand", new ExpandTabCompleter());

        registerSubCompiler("reelect", new ReelectTabCompleter());
    }

    private void registerSubCompiler(String command, ISubTabCompleter iSubTabCompiler) {
        map.put(command.toLowerCase(), iSubTabCompiler);
    }

    @Override
    public List<String> handle(CommandSender sender, int length, String[] args) {
        if (length == 2) {
            return AdminCommand.getSubCommands();
        }

        if (length > 2) {
            String commandName = args[1].toLowerCase();

            if (map.containsKey(commandName)) {
                return map.get(commandName).handle(sender, length, args);
            }
        }

        return null;
    }
}
