package site.liangbai.realhomehunt.command.subtabcompiler.impl;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import site.liangbai.realhomehunt.command.subtabcompiler.ISubTabCompiler;
import site.liangbai.realhomehunt.manager.ResidenceManager;
import site.liangbai.realhomehunt.residence.Residence;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class AdministratorTabCompiler implements ISubTabCompiler {
    @Override
    public List<String> handle(CommandSender sender, int length, String[] args) {
        if (length == 2) {
            Residence residence = ResidenceManager.getResidenceByOwner(sender.getName());

            return Bukkit.getOnlinePlayers()
                    .stream()
                    .map(Player::getName)
                    .filter(it -> {
                        if (residence != null) return !residence.isAdministrator(it);

                        return true;
                    })
                    .collect(Collectors.toList());
        }

        if (length == 3) {
            return Arrays.asList("true", "false");
        }

        return null;
    }
}
