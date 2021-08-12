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

package site.liangbai.realhomehunt.internal.command.subcommand.impl.admin;

import org.bukkit.command.CommandSender;
import site.liangbai.realhomehunt.internal.command.subcommand.ISubCommand;
import site.liangbai.realhomehunt.internal.command.subcommand.impl.admin.subcommand.*;
import site.liangbai.realhomehunt.api.locale.impl.Locale;
import site.liangbai.realhomehunt.util.Locales;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The type Admin command.
 *
 * @author Liangbai
 * @since 2021 /08/10 01:49 下午
 */
public final class AdminCommand implements ISubCommand {
    private static final Map<String, ISubCommand> map = new HashMap<>();

    public AdminCommand() {
        registerSubCommand("help", new HelpCommand());

        registerSubCommand("import", new ImportCommand());

        registerSubCommand("translate", new TranslateCommand());

        registerSubCommand("translateAll", new TranslateAllCommand());

        registerSubCommand("create", new CreateCommand());

        registerSubCommand("remove", new RemoveCommand());

        registerSubCommand("set", new SetCommand());

        registerSubCommand("expand", new ExpandCommand());

        registerSubCommand("reelect", new ReelectCommand());
    }

    private void registerSubCommand(String command, ISubCommand ISubCommand) {
        map.put(command.toLowerCase(), ISubCommand);
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (args.length < 2) {
            map.get("help").execute(sender, label, args);

            return;
        }

        String commandName = args[1].toLowerCase();

        Locale locale = Locales.require(sender);

        if (!map.containsKey(commandName)) {
            sender.sendMessage(locale.asString("command.common.unknownSubCommand", label + " " + args[0]));

            return;
        }

        String permission = "rh.command.admin." + commandName;

        if (!sender.hasPermission(permission)) {
            sender.sendMessage(locale.asString("command.common.haveNotPermission", permission));

            return;
        }

        map.get(commandName).execute(sender, label, args);
    }

    public static List<String> getSubCommands() {
        return new ArrayList<>(map.keySet());
    }
}
