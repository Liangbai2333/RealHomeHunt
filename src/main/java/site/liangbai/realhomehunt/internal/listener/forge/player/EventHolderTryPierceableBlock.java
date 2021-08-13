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

package site.liangbai.realhomehunt.internal.listener.forge.player;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import site.liangbai.forgeeventbridge.event.EventHolder;
import site.liangbai.forgeeventbridge.wrapper.EventWrapper;
import site.liangbai.forgeeventbridge.wrapper.WrapperTransformer;
import site.liangbai.realhomehunt.common.config.Config;
import site.liangbai.realhomehuntforge.event.BlockRayTraceEvent;

public class EventHolderTryPierceableBlock implements EventHolder<EventWrapper.EventObject> {
    @Override
    public void handle(EventWrapper<EventWrapper.EventObject> eventWrapper) {
        BlockRayTraceEvent.TryPierceableBlock event = (BlockRayTraceEvent.TryPierceableBlock) eventWrapper.getObject();

        World world = (World) WrapperTransformer.require(World.class, event.getLevel());

        Block block = world.getBlockAt((Location) WrapperTransformer.require(Location.class, event.getRayTraceResult().getBlockPos()));

        event.setPierceable(Config.block.ignore.isPierceable(block.getType()));
    }
}
