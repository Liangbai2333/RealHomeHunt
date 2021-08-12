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

package site.liangbai.realhomehunt.internal.listener.player.residence;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import site.liangbai.dynamic.event.EventSubscriber;
import site.liangbai.realhomehunt.api.event.residence.ResidenceRemoveEvent;
import site.liangbai.realhomehunt.common.particle.EffectGroup;
import site.liangbai.realhomehunt.internal.command.subcommand.impl.ShowCommand;

@EventSubscriber
public class ListenerResidenceRemove implements Listener {
    @EventHandler
    public void onResidenceRemove(ResidenceRemoveEvent event) {
        String owner = event.getResidence().getOwner();

        if (ShowCommand.SHOW_CACHES.containsKey(owner)) {
            EffectGroup effectGroup = ShowCommand.SHOW_CACHES.get(owner);

            effectGroup.turnOff();

            ShowCommand.SHOW_CACHES.remove(owner);
        }
    }
}
