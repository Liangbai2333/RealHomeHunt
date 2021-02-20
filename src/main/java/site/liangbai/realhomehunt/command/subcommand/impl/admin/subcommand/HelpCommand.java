package site.liangbai.realhomehunt.command.subcommand.impl.admin.subcommand;

import org.bukkit.command.CommandSender;
import site.liangbai.realhomehunt.command.subcommand.ISubCommand;
import site.liangbai.realhomehunt.locale.impl.Locale;
import site.liangbai.realhomehunt.util.LocaleUtil;

public final class HelpCommand implements ISubCommand {
    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        Locale locale = LocaleUtil.require(sender);

        sender.sendMessage(locale.asString("command.admin.help", label));
    }
}
