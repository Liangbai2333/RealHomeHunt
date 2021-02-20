package site.liangbai.realhomehunt.command.subtabcompiler.impl.admin;

import org.bukkit.command.CommandSender;
import site.liangbai.realhomehunt.command.subcommand.impl.admin.AdminCommand;
import site.liangbai.realhomehunt.command.subtabcompiler.ISubTabCompiler;
import site.liangbai.realhomehunt.command.subtabcompiler.impl.admin.subtabcompiler.ImportTabCompiler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class AdminTabCompiler implements ISubTabCompiler {
    private static final Map<String, ISubTabCompiler> map = new HashMap<>();

    public AdminTabCompiler() {
        registerSubCompiler("import", new ImportTabCompiler());
    }

    private void registerSubCompiler(String command, ISubTabCompiler iSubTabCompiler) {
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
