package site.liangbai.realhomehunt.command.subtabcompleter.impl.admin.subtabcompleter;

import org.bukkit.command.CommandSender;
import site.liangbai.realhomehunt.api.residence.Residence;
import site.liangbai.realhomehunt.api.residence.manager.ResidenceManager;
import site.liangbai.realhomehunt.command.subtabcompleter.ISubTabCompleter;

import java.util.List;
import java.util.stream.Collectors;

/**
 * The type Remove tab completer.
 *
 * @author Liangbai
 * @since 2021 /08/10 02:51 下午
 */
public class RemoveTabCompleter implements ISubTabCompleter {
    @Override
    public List<String> handle(CommandSender sender, int length, String[] args) {
        if (length == 3) {
            return ResidenceManager.getResidences()
                    .stream()
                    .map(Residence::getOwner)
                    .collect(Collectors.toList());
        }

        return null;
    }
}
