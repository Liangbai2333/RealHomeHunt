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

package site.liangbai.realhomehunt.listener.forge.player;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import site.liangbai.forgeeventbridge.event.EventHolder;
import site.liangbai.forgeeventbridge.wrapper.EventWrapper;
import site.liangbai.forgeeventbridge.wrapper.ObjectWrapper;
import site.liangbai.forgeeventbridge.wrapper.creator.WrapperCreators;
import site.liangbai.realhomehunt.processor.Processors;

public class EventHolderGunHitBlock implements EventHolder<EventHolderGunHitBlock.GunHitBlockEventObject> {
    @Override
    public void handle(EventWrapper<GunHitBlockEventObject> eventWrapper) {
        GunHitBlockEventObject eventObject = eventWrapper.as(GunHitBlockEventObject.class);

        ObjectWrapper livingWrapper = WrapperCreators.OBJECT.create(eventObject.getLiving());

        Entity entity = livingWrapper.invokeWrapper("getEntity", WrapperCreators.ENTITY).asEntity();

        if (!(entity instanceof Player)) return;

        Player player = ((Player) entity);

        ItemStack gun = eventObject.getItemStack();

        World world = eventObject.getWorld();

        Block block = world.getBlockAt(eventObject.getBlockPos());

        Processors.GUN_HIT_BLOCK_PROCESSOR.processGunHitBlock(player, gun, block);
    }

    public static abstract class GunHitBlockEventObject extends EventWrapper.EventObject {
        public abstract Object getLiving();

        public abstract Location getBlockPos();

        public abstract ItemStack getItemStack();

        public abstract World getWorld();
    }
}
