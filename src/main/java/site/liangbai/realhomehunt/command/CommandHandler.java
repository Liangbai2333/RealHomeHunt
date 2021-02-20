package site.liangbai.realhomehunt.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import site.liangbai.realhomehunt.command.subcommand.ISubCommand;
import site.liangbai.realhomehunt.command.subcommand.impl.*;
import site.liangbai.realhomehunt.command.subcommand.impl.admin.AdminCommand;
import site.liangbai.realhomehunt.locale.impl.Locale;
import site.liangbai.realhomehunt.util.LocaleUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@site.liangbai.lrainylib.annotation.command.CommandHandler(value = "rh", aliases = {"realhomehunt", "rhh"})
public final class CommandHandler implements CommandExecutor {
    private static final Map<String, ISubCommand> map = new HashMap<>();

    public CommandHandler() {
        registerSubCommand("help", new HelpCommand());

        registerSubCommand("create", new CreateCommand());

        registerSubCommand("remove", new RemoveCommand());

        registerSubCommand("setSpawn", new SetSpawnCommand());

        registerSubCommand("back", new BackCommand());

        registerSubCommand("administrator", new AdministratorCommand());

        registerSubCommand("reload", new ReloadCommand());

        registerSubCommand("set", new SetCommand());

        registerSubCommand("admin", new AdminCommand());
    }

    private void registerSubCommand(String command, ISubCommand ISubCommand) {
        map.put(command.toLowerCase(), ISubCommand);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, String[] strings) {
        if (strings.length < 1) {
            map.get("help").execute(commandSender, s, strings);

            return true;
        }

        String commandName = strings[0].toLowerCase();

        Locale locale = LocaleUtil.require(commandSender);

        if (!map.containsKey(commandName)) {
            commandSender.sendMessage(locale.asString("command.common.unknownSubCommand", s));

            return true;
        }

        String permission = "rh.command." + commandName;

        if (!commandSender.hasPermission(permission)) {
            commandSender.sendMessage(locale.asString("command.common.haveNotPermission", permission));

            return true;
        }

        map.get(commandName).execute(commandSender, s, strings);

        return true;
    }

    public static List<String> getSubCommands() {
        return new ArrayList<>(map.keySet());
    }
}
