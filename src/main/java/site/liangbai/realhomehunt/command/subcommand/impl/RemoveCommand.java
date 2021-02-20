package site.liangbai.realhomehunt.command.subcommand.impl;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import site.liangbai.realhomehunt.command.subcommand.ISubCommand;
import site.liangbai.realhomehunt.locale.impl.Locale;
import site.liangbai.realhomehunt.locale.manager.LocaleManager;
import site.liangbai.realhomehunt.manager.ResidenceManager;
import site.liangbai.realhomehunt.residence.Residence;

public final class RemoveCommand implements ISubCommand {
    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) return;

        Player player = ((Player) sender);

        Locale locale = LocaleManager.require(player);

        String name = player.getName();

        Residence residence = ResidenceManager.getResidenceByOwner(name);

        if (residence == null) {
            sender.sendMessage(locale.asString("command.remove.haveNotResidence"));

            return;
        }

        residence.remove();

        sender.sendMessage(locale.asString("command.remove.success"));
    }
}
