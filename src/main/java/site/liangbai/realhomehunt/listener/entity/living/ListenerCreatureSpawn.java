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

package site.liangbai.realhomehunt.listener.entity.living;

import org.bukkit.Location;
import org.bukkit.entity.Animals;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import site.liangbai.lrainylib.annotation.Plugin;
import site.liangbai.realhomehunt.manager.ResidenceManager;
import site.liangbai.realhomehunt.residence.Residence;
import site.liangbai.realhomehunt.residence.attribute.IAttributable;
import site.liangbai.realhomehunt.residence.attribute.impl.AnimalsAttribute;
import site.liangbai.realhomehunt.residence.attribute.impl.CreatureAttribute;
import site.liangbai.realhomehunt.residence.attribute.impl.MonsterAttribute;

@Plugin.EventSubscriber
public final class ListenerCreatureSpawn implements Listener {
    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {

        LivingEntity entity = event.getEntity();

        Location spawnLocation = event.getLocation();

        Residence residence = ResidenceManager.getResidenceByLocation(spawnLocation);

        if (residence != null) {
            try {
                IAttributable<Boolean> creatureAttribute = residence.getAttribute(CreatureAttribute.class);

                if (!creatureAttribute.get()) {
                    event.setCancelled(true);

                    return;
                }

                if (entity instanceof Monster) {
                    IAttributable<Boolean> monsterAttribute = residence.getAttribute(MonsterAttribute.class);

                    if (!monsterAttribute.get()) {
                        event.setCancelled(true);

                        return;
                    }
                }

                if (entity instanceof Animals) {
                    IAttributable<Boolean> animalsAttribute = residence.getAttribute(AnimalsAttribute.class);

                    if (!animalsAttribute.get()) {
                        event.setCancelled(true);
                    }
                }
            } catch (Throwable ignored) { }
        }
    }
}
