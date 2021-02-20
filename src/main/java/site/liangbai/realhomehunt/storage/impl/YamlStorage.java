package site.liangbai.realhomehunt.storage.impl;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import site.liangbai.realhomehunt.residence.Residence;
import site.liangbai.realhomehunt.storage.IStorage;

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
        if (!dataFolder.exists()) {
            if (!dataFolder.mkdirs()) throw new IllegalStateException("can not create new folder: " + dataFolder.getAbsolutePath());
        }

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
    public int count() {
        return playerData.length;
    }
}
