package site.liangbai.realhomehunt.internal.command.subcommand.impl;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import site.liangbai.realhomehunt.api.cache.SelectCache;
import site.liangbai.realhomehunt.api.locale.impl.Locale;
import site.liangbai.realhomehunt.internal.command.subcommand.ISubCommand;
import site.liangbai.realhomehunt.util.Locales;

/**
 * The type Unselect command.
 *
 * @author Liangbai
 * @since 2021 /08/12 02:46 下午
 */
public final class UnselectCommand implements ISubCommand {
    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) return;

        SelectCache.pop(((Player) sender));

        Locale locale = Locales.require(sender);

        sender.sendMessage(locale.asString("command.unselect.success"));
    }
}
