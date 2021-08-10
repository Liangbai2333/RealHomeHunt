package site.liangbai.realhomehunt.command.subcommand.impl;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import site.liangbai.realhomehunt.api.locale.impl.Locale;
import site.liangbai.realhomehunt.api.residence.Residence;
import site.liangbai.realhomehunt.api.residence.manager.ResidenceManager;
import site.liangbai.realhomehunt.command.subcommand.ISubCommand;
import site.liangbai.realhomehunt.util.Locales;

/**
 * The type Info command.
 *
 * @author Liangbai
 * @since 2021 /08/10 02:30 下午
 */
public final class InfoCommand implements ISubCommand {
    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        Locale locale = Locales.require(sender);

        if (args.length < 2) {
            if (!(sender instanceof Player)) return;

            Residence residence = ResidenceManager.getResidenceByOwner(sender.getName());

            if (residence == null) {
                sender.sendMessage(locale.asString("command.info.self.haveNotResidence"));

                return;
            }

            sender.sendMessage(locale.asString("command.info.self.show", residence.getOwner(), residence.getAdministrators()));
        } else {
            Residence residence = ResidenceManager.getResidenceByOwner(args[1]);

            if (residence == null) {
                sender.sendMessage(locale.asString("command.info.other.haveNotResidence"));

                return;
            }

            sender.sendMessage(locale.asString("command.info.other.show", args[1], residence.getOwner(), residence.getAdministrators()));
        }
    }
}
