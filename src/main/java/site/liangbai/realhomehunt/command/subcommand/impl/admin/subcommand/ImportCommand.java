package site.liangbai.realhomehunt.command.subcommand.impl.admin.subcommand;

import org.bukkit.command.CommandSender;
import site.liangbai.realhomehunt.RealHomeHunt;
import site.liangbai.realhomehunt.command.subcommand.ISubCommand;
import site.liangbai.realhomehunt.locale.impl.Locale;
import site.liangbai.realhomehunt.manager.ResidenceManager;
import site.liangbai.realhomehunt.residence.Residence;
import site.liangbai.realhomehunt.storage.impl.SqliteStorage;
import site.liangbai.realhomehunt.storage.impl.YamlStorage;
import site.liangbai.realhomehunt.util.LocaleUtil;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public final class ImportCommand implements ISubCommand {
    private static final Pattern booleanPattern = Pattern.compile("true|false");

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        Locale locale = LocaleUtil.require(sender);

        if (args.length < 5) {
            sender.sendMessage(locale.asString("command.admin.import.usage", label));

            return;
        }

        if (!booleanPattern.matcher(args[3]).matches()) {
            sender.sendMessage(locale.asString("command.admin.import.unknownParam4"));

            return;
        }

        if (!booleanPattern.matcher(args[4]).matches()) {
            sender.sendMessage(locale.asString("command.admin.import.unknownParam5"));

            return;
        }

        boolean replaceOld = Boolean.parseBoolean(args[3]);

        boolean cleanOld = Boolean.parseBoolean(args[4]);

        File file = new File(RealHomeHunt.plugin.getDataFolder(), args[2]);

        if (!file.exists()) {
            sender.sendMessage(locale.asString("command.admin.import.unknownFile", file.getName()));

            return;
        }

        String storageType = ResidenceManager.getStorageType().name();

        if (file.isFile()) {
            SqliteStorage storage = new SqliteStorage(RealHomeHunt.plugin, file.getName());

            if (cleanOld) {
                ResidenceManager.getResidences().forEach(Residence::remove);
            }

            List<Residence> residenceList = storage.loadAll();

            residenceList.stream()
                    .filter(Objects::nonNull)
                    .forEach(it -> ResidenceManager.register(it, replaceOld));

            ResidenceManager.getResidences().forEach(Residence::save);

            sender.sendMessage(locale.asString("command.admin.import.saveToStorage", storageType, residenceList.size(), storage.count()));
        }

        if (file.isDirectory()) {
            YamlStorage storage = new YamlStorage(RealHomeHunt.plugin, file.getName());

            if (cleanOld) {
                ResidenceManager.getResidences().forEach(Residence::remove);
            }

            List<Residence> residenceList = storage.loadAll();

            residenceList.forEach(it -> ResidenceManager.register(it, replaceOld));

            ResidenceManager.getResidences().forEach(Residence::save);

            sender.sendMessage(locale.asString("command.admin.import.saveToStorage", storageType, residenceList.size(), storage.count()));
        }
    }
}
