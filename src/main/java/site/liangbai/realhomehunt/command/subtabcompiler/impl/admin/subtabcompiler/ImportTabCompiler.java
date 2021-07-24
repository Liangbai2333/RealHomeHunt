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
