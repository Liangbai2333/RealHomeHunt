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

package site.liangbai.realhomehunt.internal.storage.impl;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import site.liangbai.realhomehunt.api.residence.Residence;
import site.liangbai.realhomehunt.internal.storage.IStorage;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class YamlStorage implements IStorage {
    private final File dataFolder;

    private final File[] playerData;

    public YamlStorage(Plugin plugin, String path) {
        dataFolder = new File(plugin.getDataFolder(), path);

        if (!dataFolder.exists()) {
            if (!dataFolder.mkdirs()) throw new IllegalStateException("can not create new folder: " + dataFolder.getAbsolutePath());
        }

        playerData = dataFolder.listFiles();

        if (playerData == null) throw new IllegalStateException("can not load data in file.");
    }

    public File getResidenceFile(@NotNull Residence residence) throws IOException {
        File file = new File(dataFolder, residence.getOwner() + ".yml");

        if (!file.exists()) {
            if (!file.createNewFile()) {
                throw new IllegalStateException("can not create new residence file: " + residence.getOwner() + ".yml");
            }
        }

        return file;
    }

    @Override
    public void save(Residence residence) {
        try {
            File file = getResidenceFile(residence);

            YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);

            yamlConfiguration.set("residence", residence);

            yamlConfiguration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void remove(Residence residence) {
        try {
            File file = getResidenceFile(residence);

            if (!file.delete()) {
                throw new IllegalStateException("can not delete residence file: " + residence.getOwner() + ".yml");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Residence> loadAll() {
        return Arrays.stream(playerData)
                .filter(it -> it.getName().endsWith(".yml"))
                .map(it -> {
                    YamlConfiguration config = new YamlConfiguration();

                    try {
                        config.load(it);
                    } catch (IOException | InvalidConfigurationException ignored) { }

                    return config;
                })
                .filter(it -> it.contains("residence"))
                .map(it -> it.getObject("residence", Residence.class))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public long count() {
        return playerData.length;
    }

    @Override
    public void close() {
        // TODO
    }
}
