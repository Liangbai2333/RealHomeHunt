/*
 * RealHomeHunt
 * Copyright (C) 2021  Liangbai
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package site.liangbai.realhomehunt.command.subcommand.impl.admin.subcommand;

import org.bukkit.command.CommandSender;
import site.liangbai.realhomehunt.RealHomeHunt;
import site.liangbai.realhomehunt.command.subcommand.ISubCommand;
import site.liangbai.realhomehunt.config.Config;
import site.liangbai.realhomehunt.api.locale.impl.Locale;
import site.liangbai.realhomehunt.api.residence.manager.ResidenceManager;
import site.liangbai.realhomehunt.api.residence.Residence;
import site.liangbai.realhomehunt.storage.impl.SqliteStorage;
import site.liangbai.realhomehunt.storage.impl.YamlStorage;
import site.liangbai.realhomehunt.util.Locales;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public final class ImportCommand implements ISubCommand {
    private static final Pattern BOOLEAN_PATTERN = Pattern.compile("true|false");

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        Locale locale = Locales.require(sender);

        if (args.length < 4) {
            sender.sendMessage(locale.asString("command.admin.import.usage", label));

            return;
        }

        if (!BOOLEAN_PATTERN.matcher(args[3]).matches()) {
            sender.sendMessage(locale.asString("command.admin.import.unknownParam4"));

            return;
        }

        boolean cleanOld = Boolean.parseBoolean(args[3]);

        File file = new File(RealHomeHunt.getInst().getDataFolder(), args[2]);

        if (!file.exists()) {
            sender.sendMessage(locale.asString("command.admin.import.unknownFile", file.getName()));

            return;
        }

        String storageType = ResidenceManager.getStorageType().name();

        if (file.isFile()) {
            Config.StorageSetting.SqliteSetting setting = new Config.StorageSetting.SqliteSetting();

            setting.onlyInPluginFolder = true;
            setting.databaseFile = args[2];

            SqliteStorage storage = new SqliteStorage(RealHomeHunt.getInst(), setting);

            if (cleanOld) {
                ResidenceManager.getResidences().forEach(Residence::remove);
            }

            List<Residence> residenceList = storage.loadAll();

            residenceList.stream()
                    .filter(Objects::nonNull)
                    .forEach(ResidenceManager::register);

            ResidenceManager.getResidences().forEach(Residence::save);

            sender.sendMessage(locale.asString("command.admin.import.saveToStorage", storageType, residenceList.size(), storage.count()));

            return;
        }

        if (file.isDirectory()) {
            YamlStorage storage = new YamlStorage(RealHomeHunt.getInst(), args[2]);

            if (cleanOld) {
                ResidenceManager.getResidences().forEach(Residence::remove);
            }

            List<Residence> residenceList = storage.loadAll();

            residenceList.forEach(ResidenceManager::register);

            ResidenceManager.getResidences().forEach(Residence::save);

            sender.sendMessage(locale.asString("command.admin.import.saveToStorage", storageType, residenceList.size(), storage.count()));
        }
    }
}
