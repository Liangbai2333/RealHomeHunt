package site.liangbai.realhomehunt.task;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import site.liangbai.realhomehunt.RealHomeHunt;
import site.liangbai.realhomehunt.util.Players;
import site.liangbai.realhomehunt.util.Titles;

import java.util.function.Consumer;

public final class PlayerBackTask extends BukkitRunnable {
    private final Player player;

    private final Location location;

    private final String doneMessage;

    private final String titleMessage;

    private final String teleportFormatSubTitleMessage;

    private final Consumer<Player> finishBlock;

    private boolean cancel;

    private int count;

    private final long seconds;

    public static void tryTeleportPlayer(Player player, Location location, String doneMessage, String denyMessage, String titleMessage, String teleportFormatSubTitleMessage, long seconds, Consumer<Player> finishBlock) {
        new PlayerBackTask(player, location, doneMessage, denyMessage, titleMessage, teleportFormatSubTitleMessage, seconds, finishBlock).runTaskTimer(RealHomeHunt.plugin, 0, 20);
    }

    public PlayerBackTask(Player player, Location location, String doneMessage, String denyMessage, String titleMessage, String teleportFormatSubTitleMessage, long seconds, Consumer<Player> finishBlock) {
        this.player = player;

        this.location = location.clone();

        this.doneMessage = doneMessage;

        this.seconds = seconds;

        this.titleMessage = titleMessage;

        this.teleportFormatSubTitleMessage = teleportFormatSubTitleMessage;

        this.finishBlock = finishBlock;

        Players.addListenerOnce(it -> {
            if (it.equals(player)) {
                if (cancel) return true;

                cancel();

                finishBlock.accept(player);

                if (denyMessage != null) {
                    Titles.sendTitle(player, denyMessage, "");
                }

                return true;
            }

            return cancel;
        });
    }

    @Override
    public void run() {
        if (count < seconds) {
            Titles.sendFastTitle(player, titleMessage, String.format(teleportFormatSubTitleMessage, (seconds - count)));

            count++;

            return;
        }

        this.cancel = true;

        if (player.isDead()) return;

        Chunk chunk = location.getChunk();

        if (!chunk.isLoaded()) chunk.load();

        player.teleport(location);

        finishBlock.accept(player);

        if (doneMessage != null) {
            Titles.sendTitle(player, doneMessage, "");
        }

        cancel();
    }
}
