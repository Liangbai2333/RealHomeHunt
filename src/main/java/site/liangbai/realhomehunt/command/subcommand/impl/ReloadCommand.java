package site.liangbai.realhomehunt.command.subcommand.impl;

import org.bukkit.command.CommandSender;
import site.liangbai.realhomehunt.RealHomeHunt;
import site.liangbai.realhomehunt.command.subcommand.ISubCommand;
import site.liangbai.realhomehunt.config.Config;
import site.liangbai.realhomehunt.locale.impl.Locale;
import site.liangbai.realhomehunt.locale.manager.LocaleManager;
import site.liangbai.realhomehunt.manager.ResidenceManager;
import site.liangbai.realhomehunt.storage.StorageType;
import site.liangbai.realhomehunt.util.LocaleUtil;

public final class ReloadCommand implements ISubCommand {
    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        Locale locale = LocaleUtil.require(sender);

        if (!sender.hasPermission("rh.reload")) {
            sender.sendMessage(locale.asString("command.reload.haveNotPermission", "rh.reload"));

            return;
        }

        Config.init(RealHomeHunt.plugin);

        StorageType storageType = StorageType.matchStorageType(Config.storageType);

        ResidenceManager.init(RealHomeHunt.plugin, storageType);

        LocaleManager.init(RealHomeHunt.plugin);

        sender.sendMessage(locale.asString("command.reload.success"));
    }
}
