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

package site.liangbai.realhomehunt.common.particle;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import site.liangbai.realhomehunt.RealHomeHuntPlugin;
import site.liangbai.realhomehunt.util.Particles;

import java.util.List;

public abstract class ParticleObject {
    private BukkitTask showTask;

    private long period;

    private Particle particle = Particle.REDSTONE;

    private Color color;

    public abstract void show(List<Player> receivers);

    public void showAsync(List<Player> receivers) {
        Bukkit.getScheduler().runTaskAsynchronously(RealHomeHuntPlugin.INSTANCE.getInst(), () -> show(receivers));
    }

    public void alwaysShow(List<Player> receivers) {
        Bukkit.getScheduler().runTaskLater(RealHomeHuntPlugin.INSTANCE.getInst(), () -> {
            showTask = new BukkitRunnable() {
                @Override
                public void run() {
                    show(receivers);
                }
            }.runTaskTimer(RealHomeHuntPlugin.INSTANCE.getInst(), 0L, period);
        }, 2L);
    }

    public void alwaysShowAsync(List<Player> receivers) {
        Bukkit.getScheduler().runTaskLater(RealHomeHuntPlugin.INSTANCE.getInst(), () -> {
            showTask = new BukkitRunnable() {
                @Override
                public void run() {
                    show(receivers);
                }
            }.runTaskTimerAsynchronously(RealHomeHuntPlugin.INSTANCE.getInst(), 0L, period);
        }, 2L);
    }

    public void turnOff() {
        if (showTask != null) {
            showTask.cancel();
        }
    }

    public long getPeriod() {
        return period;
    }

    public void setPeriod(long period) {
        this.period = period;
    }

    public Particle getParticle() {
        return particle;
    }

    public void setParticle(Particle particle) {
        this.particle = particle;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void spawnParticle(List<Player> receivers, Location location) {
        Particle.DustOptions dustOptions = color != null ? new Particle.DustOptions(color, 1) : null;

        Particles.spawnParticle(particle, receivers, location, dustOptions);
    }

    @Override
    public abstract ParticleObject clone();
}
