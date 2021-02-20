package site.liangbai.realhomehunt.command.subcommand.impl;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import site.liangbai.realhomehunt.command.subcommand.ISubCommand;
import site.liangbai.realhomehunt.locale.impl.Locale;
import site.liangbai.realhomehunt.locale.manager.LocaleManager;
import site.liangbai.realhomehunt.manager.ResidenceManager;
import site.liangbai.realhomehunt.residence.Residence;

import java.util.regex.Pattern;

public final class AdministratorCommand implements ISubCommand {
    private static final Pattern booleanPattern = Pattern.compile("true|false");

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) return;

        Player player = ((Player) sender);

        Locale locale = LocaleManager.require(player);

        if (args.length < 3) {
            sender.sendMessage(locale.asString("command.administrator.usage", label));

            return;
        }

        String admin = args[1];

        if (!booleanPattern.matcher(args[2]).matches()) {
            sender.sendMessage(locale.asString("command.administrator.unknownParam"));

            return;
        }

        boolean param = Boolean.parseBoolean(args[2]);

        String name = player.getName();

        Residence residence = ResidenceManager.getResidenceByName(name);

        if (residence == null) {
            sender.sendMessage(locale.asString("command.administrator.haveNotResidence"));

            return;
        }

        if (param) {
            if (residence.isAdministrator(admin)) {
                sender.sendMessage(locale.asString("command.administrator.alreadyIsAdministrator", admin));

                return;
            }

            residence.addAdministrator(admin);

            sender.sendMessage(locale.asString("command.administrator.successGivePermission", admin));
        } else {
            if (!residence.isAdministrator(admin)) {
                sender.sendMessage(locale.asString("command.administrator.notIsAdministrator", admin));

                return;
            }

            residence.removeAdministrator(admin);

            sender.sendMessage(locale.asString("command.administrator.successDeletePermission", admin));
        }

        residence.save();
    }
}
