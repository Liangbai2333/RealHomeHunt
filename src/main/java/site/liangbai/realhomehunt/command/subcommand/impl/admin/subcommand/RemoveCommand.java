package site.liangbai.realhomehunt.command.subcommand.impl.admin.subcommand;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import site.liangbai.realhomehunt.api.event.residence.ResidenceRemoveEvent;
import site.liangbai.realhomehunt.api.locale.impl.Locale;
import site.liangbai.realhomehunt.api.residence.Residence;
import site.liangbai.realhomehunt.api.residence.manager.ResidenceManager;
import site.liangbai.realhomehunt.command.subcommand.ISubCommand;
import site.liangbai.realhomehunt.util.Locales;

/**
 * The type Remove command.
 *
 * @author Liangbai
 * @since 2021 /08/10 02:51 下午
 */
public final class RemoveCommand implements ISubCommand {
    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        Locale locale = Locales.require(sender);

        if (args.length < 3) {
            sender.sendMessage(locale.asString("command.admin.remove.usage", label));

            return;
        }

        Residence residence = ResidenceManager.getResidenceByOwner(args[2]);

        if (residence == null) {
            sender.sendMessage(locale.asString("command.admin.remove.haveNotResidence"));

            return;
        }

        if (sender instanceof Player) {
            if (!new ResidenceRemoveEvent(residence, ((Player) sender)).callEvent()) return;
        }

        residence.remove();

        sender.sendMessage(locale.asString("command.admin.remove.success", args[2]));
    }
}
