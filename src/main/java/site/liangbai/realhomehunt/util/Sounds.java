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

package site.liangbai.realhomehunt.util;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import site.liangbai.realhomehunt.RealHomeHuntPlugin;

public final class Sounds {
    public static void playSound(Player player, Sound sound, int count, double delaySeconds) {
        new BukkitRunnable() {
            int alreadyCount = 0;

            @Override
            public void run() {
                if (alreadyCount >= count) {
                    cancel();

                    return;
                }

                player.playSound(player.getLocation(), sound, 1.0F, 1.0F);

                alreadyCount++;
            }
        }.runTaskTimer(RealHomeHuntPlugin.INSTANCE.getInst(), 0, (long) delaySeconds * 20);
    }

    public static void playDragonAmbientSound(Player player, int count, double delaySeconds) {
        playSound(player, Sound.ENTITY_ENDER_DRAGON_AMBIENT, count, delaySeconds);
    }

    public static void playLevelUpSound(Player player, int count, double delaySeconds) {
        playSound(player, Sound.ENTITY_PLAYER_LEVELUP, count, delaySeconds);
    }
}
