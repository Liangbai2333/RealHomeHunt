package site.liangbai.realhomehunt.command.subcommand.impl;

import org.bukkit.command.CommandSender;
import site.liangbai.realhomehunt.command.subcommand.ISubCommand;
import site.liangbai.realhomehunt.locale.impl.Locale;
import site.liangbai.realhomehunt.util.Locales;

public final class HelpCommand implements ISubCommand {
    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        Locale locale = Locales.require(sender);

        sender.sendMessage(locale.asString("command.help.common", label));

        if (sender.hasPermission("rh.reload")) {
            sender.sendMessage(locale.asString("command.help.reload", label));
        }
    }
}
