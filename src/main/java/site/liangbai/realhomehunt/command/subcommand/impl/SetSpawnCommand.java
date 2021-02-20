package site.liangbai.realhomehunt.command.subcommand.impl;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import site.liangbai.realhomehunt.command.subcommand.ISubCommand;
import site.liangbai.realhomehunt.locale.impl.Locale;
import site.liangbai.realhomehunt.locale.manager.LocaleManager;
import site.liangbai.realhomehunt.manager.ResidenceManager;
import site.liangbai.realhomehunt.residence.Residence;
import site.liangbai.realhomehunt.util.LocationUtil;

public final class SetSpawnCommand implements ISubCommand {
    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) return;

        Player player = ((Player) sender);

        Locale locale = LocaleManager.require(player);

        String name = player.getName();

        Residence residence = ResidenceManager.getResidenceByName(name);

        if (residence == null) {
            sender.sendMessage(locale.asString("command.setspawn.haveNotResidence"));

            return;
        }

        if (!residence.isOwner(name)) {
            sender.sendMessage(locale.asString("command.setspawn.isNotOwner"));

            return;
        }

        Location location = player.getLocation();

        Location blockLocation = LocationUtil.toBlockLocation(location);

        if (!ResidenceManager.isInResidence(blockLocation, residence)) {
            sender.sendMessage(locale.asString("command.setspawn.notInResidence"));

            return;
        }

        residence.setSpawn(location);

        residence.save();

        sender.sendMessage(locale.asString("command.setspawn.success"));
    }
}
