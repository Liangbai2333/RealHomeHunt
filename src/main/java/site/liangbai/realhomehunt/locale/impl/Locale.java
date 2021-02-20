package site.liangbai.realhomehunt.locale.impl;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import site.liangbai.realhomehunt.config.Config;
import site.liangbai.realhomehunt.locale.ILocale;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Locale implements ILocale {
    private final Map<String, String> nodes = new HashMap<>();

    public Locale(ConfigurationSection section) {
        section.getKeys(true).stream()
                .filter(it -> section.isString(it) || section.isList(it))
                .forEach(it -> {
                    Object object = section.get(it);

                    if (object instanceof String) {
                        nodes.put(it, translateColoredString((String) object));
                    } else if (object instanceof List) {
                        List<String> list = section.getStringList(it);

                        StringBuilder stringBuilder = new StringBuilder();

                        list.forEach(s -> stringBuilder.append(translateColoredString(s)).append("\n"));

                        nodes.put(it, stringBuilder.toString());
                    }
                });
    }

    private String translateColoredString(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    @NotNull
    @Override
    public String asString(String node, Object... args) {
        String result = nodes.get(node);

        if (result == null) return "null";

        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];

            if (arg != null) {
                result = result.replace("{" + i + "}", arg.toString());
            }
        }

        result = result.replace("{prefix}", Config.prefix);

        return result;
    }
}
