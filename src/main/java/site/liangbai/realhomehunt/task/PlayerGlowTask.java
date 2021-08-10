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

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import site.liangbai.dynamic.event.EventSubscriber;
import site.liangbai.realhomehunt.api.residence.Residence;
import site.liangbai.realhomehunt.api.residence.attribute.impl.GlowAttribute;
import site.liangbai.realhomehunt.api.residence.manager.ResidenceManager;
import site.liangbai.realhomehunt.util.Locations;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * The type Player glow task.
 *
 * @author Liangbai
 * @since 2021 /08/10 11:07 上午
 */
@EventSubscriber
public final class PlayerGlowTask extends BukkitRunnable implements Listener {
    // 防止和其他需要设置glow的插件冲突
    private static final Set<UUID> glowing = new CopyOnWriteArraySet<>();

    public static void setup(Plugin plugin) {
        new PlayerGlowTask().runTaskTimerAsynchronously(plugin, 1, 1);
    }

    @Override
    public void run() {
        Bukkit.getOnlinePlayers().stream()
                .filter(it -> !it.isDead() && ResidenceManager.isOpened(it.getWorld()))
                .forEach(it -> {
                    Location location = Locations.toBlockLocation(it.getLocation());

                    Residence residence = ResidenceManager.getResidenceByLocation(location);

                    if (residence != null) {
                        if (residence.checkBooleanAttribute(GlowAttribute.class) && !it.isGlowing()) {
                            it.setGlowing(true);

                            glowing.add(it.getUniqueId());
                        } else if (!residence.checkBooleanAttribute(GlowAttribute.class) && it.isGlowing()) {
                            it.setGlowing(false);

                            glowing.remove(it.getUniqueId());
                        }
                    } else if (it.isGlowing()) {
                        if (glowing.contains(it.getUniqueId())) {
                            it.setGlowing(false);

                            glowing.remove(it.getUniqueId());
                        }
                    } else glowing.remove(it.getUniqueId());
                });
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (event.getPlayer().isGlowing() && glowing.contains(event.getPlayer().getUniqueId())) {
            event.getPlayer().setGlowing(false);

            glowing.remove(event.getPlayer().getUniqueId());
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (event.getPlayer().isGlowing()) {
            event.getPlayer().setGlowing(false);
        }
    }
}
