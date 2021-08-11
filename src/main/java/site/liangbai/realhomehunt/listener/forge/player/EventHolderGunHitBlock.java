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

import com.craftingdead.core.event.GunEvent;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import site.liangbai.forgeeventbridge.event.EventHolder;
import site.liangbai.forgeeventbridge.wrapper.EventWrapper;
import site.liangbai.forgeeventbridge.wrapper.WrapperTransformer;
import site.liangbai.realhomehunt.processor.Processors;
import site.liangbai.realhomehunt.util.Living;

/**
 * The type Event holder gun hit block.
 *
 * @author Liangbai
 * @since 2021 /08/11 02:37 下午
 */
public class EventHolderGunHitBlock implements EventHolder<EventWrapper.EventObject> {
    @Override
    public void handle(EventWrapper<EventWrapper.EventObject> eventWrapper) {
        GunEvent.HitBlock event = (GunEvent.HitBlock) eventWrapper.getObject();

        Entity entity = Living.asEntity(event.getLiving());

        if (!(entity instanceof Player)) return;

        Player player = ((Player) entity);

        ItemStack gun = (ItemStack) WrapperTransformer.require(ItemStack.class, event.getItemStack());

        World world = (World) WrapperTransformer.require(World.class, event.getLevel());

        Block block = world.getBlockAt((Location) WrapperTransformer.require(Location.class, event.getRayTraceResult().getBlockPos()));

        Processors.GUN_HIT_BLOCK_PROCESSOR.processGunHitBlock(player, gun, block);
    }
}
