package site.liangbai.realhomehunt.actionbar;

import org.bukkit.entity.Player;

public interface IActionBar {
    void show(Player player);

    void show(Player player, long mills);

    void clear(Player player);
}
