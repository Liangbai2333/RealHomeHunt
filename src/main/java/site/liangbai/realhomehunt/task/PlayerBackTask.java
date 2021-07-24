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

package site.liangbai.realhomehunt.task;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import site.liangbai.realhomehunt.RealHomeHunt;
<<<<<<< HEAD
import site.liangbai.realhomehunt.util.Locations;
=======
>>>>>>> b0e20feb0f34a730ef4c8abed901bfc4e4e16869
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

<<<<<<< HEAD
        Locations.teleportAfterChunkLoaded(player, location);
=======
        if (player.isDead()) return;

        Chunk chunk = location.getChunk();

        if (!chunk.isLoaded()) chunk.load();

        player.teleport(location);
>>>>>>> b0e20feb0f34a730ef4c8abed901bfc4e4e16869

        finishBlock.accept(player);

        if (doneMessage != null) {
            Titles.sendTitle(player, doneMessage, "");
        }

        cancel();
    }
}
