package site.liangbai.realhomehunt.command.subtabcompiler.impl;

import org.bukkit.command.CommandSender;
import site.liangbai.realhomehunt.command.subtabcompiler.ISubTabCompiler;
import site.liangbai.realhomehunt.manager.ResidenceManager;
import site.liangbai.realhomehunt.residence.Residence;

import java.util.List;
import java.util.stream.Collectors;

public final class BackTabCompiler implements ISubTabCompiler {
    @Override
    public List<String> handle(CommandSender sender, int length, String[] args) {
        if (length == 2) {
            return ResidenceManager.getResidences().stream()
                    .filter(it -> it.isAdministrator(sender.getName()) && !it.isOwner(sender.getName()) && !sender.hasPermission("rh.unlimited.back"))
                    .map(Residence::getOwner)
                    .collect(Collectors.toList());
        }

        return null;
    }
}
