package site.liangbai.realhomehunt.util;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class TitleUtil {
    public static void sendTitle(@NotNull Player player, @Nullable String title, @Nullable String subTitle) {
        player.sendTitle(title, subTitle, 10, 70, 20);
    }

    public static void sendFastTitle(@NotNull Player player, @Nullable String title, @Nullable String subTitle) {
        player.sendTitle(title, subTitle, 0, 40, 10);
    }
}
