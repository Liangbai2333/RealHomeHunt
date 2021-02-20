package site.liangbai.realhomehunt.command.subcommand.impl;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import site.liangbai.realhomehunt.command.subcommand.ISubCommand;
import site.liangbai.realhomehunt.config.Config;
import site.liangbai.realhomehunt.locale.impl.Locale;
import site.liangbai.realhomehunt.locale.manager.LocaleManager;
import site.liangbai.realhomehunt.manager.ResidenceManager;
import site.liangbai.realhomehunt.residence.Residence;
import site.liangbai.realhomehunt.task.PlayerBackTask;

import java.util.HashSet;
import java.util.Set;

public final class BackCommand implements ISubCommand {
    private final Set<String> teleportPlayers = new HashSet<>();

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) return;

        Player player = ((Player) sender);

        if (teleportPlayers.contains(player.getName())) return;

        Locale locale = LocaleManager.require(player);

        String name = player.getName();

        Residence residence;

        if (args.length > 1) {
            String other = args[1];

            residence = ResidenceManager.getResidenceByOwner(other);

            if (residence == null) {
                sender.sendMessage(locale.asString("command.back.other.notHaveResidence"));

                return;
            }

            if (!residence.isAdministrator(name) && !player.hasPermission("rh.unlimited.back")) {
                sender.sendMessage(locale.asString("command.back.other.notHavePermission"));

                return;
            }

        } else {
            residence = ResidenceManager.getResidenceByOwner(name);

            if (residence == null) {
                sender.sendMessage(locale.asString("command.back.self.notHaveResidence", label));

                return;
            }
        }

        if (residence.getSpawn() == null) {
            sender.sendMessage(locale.asString("command.back.self.notHaveSpawnPoint"));

            return;
        }

        String doneMessage = locale.asString("command.back.teleport.doneTitle", residence.getOwner());

        String denyMessage = locale.asString("command.back.teleport.denyTitle", residence.getOwner());

        String titleMessage = locale.asString("command.back.teleport.waitTitle", residence.getOwner());

        String teleportFormatSubTitleMessage = locale.asString("command.back.teleport.waitSubTitle", "%s");

        long seconds = Config.teleportMills / 20;

        teleportPlayers.add(player.getName());

        PlayerBackTask.tryTeleportPlayer(player, residence.getSpawn(), doneMessage, denyMessage, titleMessage, teleportFormatSubTitleMessage, seconds, it -> teleportPlayers.remove(it.getName()));
    }
}
