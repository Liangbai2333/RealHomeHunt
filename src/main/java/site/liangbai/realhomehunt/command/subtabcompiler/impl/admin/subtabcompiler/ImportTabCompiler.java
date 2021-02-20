package site.liangbai.realhomehunt.command.subtabcompiler.impl.admin.subtabcompiler;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import site.liangbai.realhomehunt.RealHomeHunt;
import site.liangbai.realhomehunt.command.subtabcompiler.ISubTabCompiler;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class ImportTabCompiler implements ISubTabCompiler {
    @Override
    public List<String> handle(CommandSender sender, int length, String[] args) {
        if (length == 3) {
            Plugin plugin = RealHomeHunt.plugin;

            File folder = plugin.getDataFolder();

            if (args[2].isEmpty()) {
                String[] files = folder.list();

                if (files == null) return null;

                return Arrays.asList(files);
            } else {
                if (args[2].endsWith("/") || args[2].endsWith("\\")) {
                    String readFolder;

                    char sChar;

                    if (args[2].endsWith("/")) {
                        sChar = '/';

                         readFolder = args[2].substring(0, args[2].lastIndexOf("/"));
                    } else {
                        sChar = '\\';

                        readFolder = args[2].substring(0, args[2].lastIndexOf("\\"));
                    }

                    File read = new File(folder, readFolder);

                    if (read.exists() && read.isDirectory()) {
                        String[] files = read.list();

                        if (files == null) return null;

                        return Arrays.stream(files)
                                .map(it -> readFolder + sChar + it)
                                .collect(Collectors.toList());
                    }
                }
            }
        }

        if (length == 4) {
            return Arrays.asList("true", "false");
        }

        return null;
    }
}
