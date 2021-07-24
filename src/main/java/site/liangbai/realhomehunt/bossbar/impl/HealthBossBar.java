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

package site.liangbai.realhomehunt.bossbar.impl;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import site.liangbai.realhomehunt.bossbar.IBossBar;

public class HealthBossBar implements IBossBar {
    private final BossBar bossBar;

    private final int greenToYellowMix;
    private final int yellowToRedMix;

    public HealthBossBar(String title, int greenToYellowMix, int yellowToRedMix) {
        if (greenToYellowMix < yellowToRedMix) {
            throw new IllegalStateException("Error setting for HealthBossBar.");
        }

        bossBar = Bukkit.createBossBar(title, BarColor.GREEN, BarStyle.SOLID);

        bossBar.setProgress(1);
        bossBar.setVisible(true);

        this.greenToYellowMix = greenToYellowMix;
        this.yellowToRedMix = yellowToRedMix;
    }

    @Override
    public void update(int current) {
        if (current <= greenToYellowMix && current >= yellowToRedMix) {
            bossBar.setColor(BarColor.YELLOW);
        } else if (current <= yellowToRedMix && current >= 0) {
            bossBar.setColor(BarColor.RED);
        }

        if (current <= 0) {
            bossBar.setProgress(0);

            hide();

            return;
        }

        bossBar.setProgress((double) current / 100);
    }

    @Override
    public void show(Player player) {
        bossBar.addPlayer(player);
    }

    @Override
    public void hide(Player player) {
        bossBar.removePlayer(player);
    }

    @Override
    public void hide() {
        bossBar.removeAll();

        bossBar.setVisible(false);
    }

    @Override
    public BossBar getHandle() {
        return bossBar;
    }
}
