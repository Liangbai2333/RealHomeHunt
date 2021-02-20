package site.liangbai.realhomehunt.cache;

import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;

public final class SelectCache {
    private static final Map<String, Location> firstCaches = new HashMap<>();
    private static final Map<String, Location> secondCaches = new HashMap<>();

    public static void push(SelectType selectType, String player, Location location) {
        if (selectType == SelectType.FIRST) {
            firstCaches.put(player, location.clone());
        }

        if (selectType == SelectType.SECOND) {
            secondCaches.put(player, location.clone());
        }
    }

    public static void pop(SelectType selectType, String player) {
        if (selectType == SelectType.FIRST) {
            firstCaches.remove(player);
        }

        if (selectType == SelectType.SECOND) {
            secondCaches.remove(player);
        }
    }

    public static void pop(String player) {
        pop(SelectType.FIRST, player);

        pop(SelectType.SECOND, player);
    }

    public static Location require(SelectType selectType, String player) {
        if (selectType == SelectType.FIRST) {
            return firstCaches.get(player);
        }

        if (selectType == SelectType.SECOND) {
            return secondCaches.get(player);
        }

        return null;
    }

    public enum SelectType {
        FIRST,
        SECOND
    }
}
