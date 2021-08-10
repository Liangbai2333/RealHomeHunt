package site.liangbai.realhomehunt.command.subtabcompleter.impl.admin.subtabcompleter;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import site.liangbai.realhomehunt.api.residence.manager.ResidenceManager;
import site.liangbai.realhomehunt.command.subtabcompleter.ISubTabCompleter;

import java.util.List;
import java.util.stream.Collectors;

public class CreateTabCompleter implements ISubTabCompleter {
    @Override
    public List<String> handle(CommandSender sender, int length, String[] args) {
        if (length == 3) {
            return Bukkit.getOnlinePlayers().stream()
                    .parallel()
                    .map(Player::getName)
                    .filter(it -> ResidenceManager.getResidenceByOwner(it) == null)
                    .collect(Collectors.toList());
        }

        return null;
    }
}
