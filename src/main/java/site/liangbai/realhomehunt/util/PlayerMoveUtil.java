package site.liangbai.realhomehunt.util;

import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

public final class PlayerMoveUtil {
    private static final List<Predicate<Player>> listenerList = new LinkedList<>();

    public static void addListenerOnce(Predicate<Player> playerPredicate) {
        listenerList.add(playerPredicate);
    }

    public static void push(Player player) {
        listenerList.forEach(it -> {
            if (it.test(player)) {
                listenerList.remove(it);
            }
        });
    }
}
