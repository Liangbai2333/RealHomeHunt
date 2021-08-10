package site.liangbai.realhomehunt.command.subtabcompleter.impl;

import org.bukkit.command.CommandSender;
import site.liangbai.realhomehunt.api.residence.Residence;
import site.liangbai.realhomehunt.api.residence.manager.ResidenceManager;
import site.liangbai.realhomehunt.command.subtabcompleter.ISubTabCompleter;

import java.util.List;
import java.util.stream.Collectors;

public final class InfoTabCompleter implements ISubTabCompleter {
    @Override
    public List<String> handle(CommandSender sender, int length, String[] args) {
        if (length == 2) {
            return ResidenceManager.getResidences()
                    .stream()
                    .map(Residence::getOwner)
                    .collect(Collectors.toList());
        }

        return null;
    }
}
