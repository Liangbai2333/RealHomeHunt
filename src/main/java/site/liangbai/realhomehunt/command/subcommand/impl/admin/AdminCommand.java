package site.liangbai.realhomehunt.command.subcommand.impl.admin;

import org.bukkit.command.CommandSender;
import site.liangbai.realhomehunt.command.subcommand.ISubCommand;
import site.liangbai.realhomehunt.command.subcommand.impl.admin.subcommand.HelpCommand;
import site.liangbai.realhomehunt.command.subcommand.impl.admin.subcommand.ImportCommand;
import site.liangbai.realhomehunt.locale.impl.Locale;
import site.liangbai.realhomehunt.util.Locales;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class AdminCommand implements ISubCommand {
    private static final Map<String, ISubCommand> map = new HashMap<>();

    public AdminCommand() {
        registerSubCommand("help", new HelpCommand());

        registerSubCommand("import", new ImportCommand());
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
