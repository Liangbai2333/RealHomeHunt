package site.liangbai.realhomehunt.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import site.liangbai.realhomehunt.command.subtabcompiler.ISubTabCompiler;
import site.liangbai.realhomehunt.command.subtabcompiler.impl.AdministratorTabCompiler;
import site.liangbai.realhomehunt.command.subtabcompiler.impl.BackTabCompiler;
import site.liangbai.realhomehunt.command.subtabcompiler.impl.SetTabCompiler;
import site.liangbai.realhomehunt.command.subtabcompiler.impl.admin.AdminTabCompiler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            return CommandHandler.getSubCommands();
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
