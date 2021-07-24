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

<<<<<<< HEAD
import com.craftingdead.core.event.GunEvent;
=======
import org.bukkit.Location;
>>>>>>> b0e20feb0f34a730ef4c8abed901bfc4e4e16869
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import site.liangbai.forgeeventbridge.event.EventHolder;
import site.liangbai.forgeeventbridge.wrapper.EventWrapper;
<<<<<<< HEAD
import site.liangbai.forgeeventbridge.wrapper.LocationWrapper;
=======
>>>>>>> b0e20feb0f34a730ef4c8abed901bfc4e4e16869
import site.liangbai.forgeeventbridge.wrapper.ObjectWrapper;
import site.liangbai.forgeeventbridge.wrapper.creator.WrapperCreators;
import site.liangbai.realhomehunt.processor.Processors;

public class EventHolderGunHitBlock implements EventHolder<EventHolderGunHitBlock.GunHitBlockEventObject> {
    @Override
    public void handle(EventWrapper<GunHitBlockEventObject> eventWrapper) {
<<<<<<< HEAD
        GunEvent.HitBlock event = (GunEvent.HitBlock) eventWrapper.getObject();
        
        LocationWrapper locationWrapper = WrapperCreators.LOCATION.create(event.getRayTraceResult().getBlockPos());

=======
>>>>>>> b0e20feb0f34a730ef4c8abed901bfc4e4e16869
        GunHitBlockEventObject eventObject = eventWrapper.as(GunHitBlockEventObject.class);

        ObjectWrapper livingWrapper = WrapperCreators.OBJECT.create(eventObject.getLiving());

        Entity entity = livingWrapper.invokeWrapper("getEntity", WrapperCreators.ENTITY).asEntity();

        if (!(entity instanceof Player)) return;

        Player player = ((Player) entity);

        ItemStack gun = eventObject.getItemStack();

<<<<<<< HEAD
        World world = eventObject.getLevel();

        Block block = world.getBlockAt(locationWrapper.asLocation());
=======
        World world = eventObject.getWorld();

        Block block = world.getBlockAt(eventObject.getBlockPos());
>>>>>>> b0e20feb0f34a730ef4c8abed901bfc4e4e16869

        Processors.GUN_HIT_BLOCK_PROCESSOR.processGunHitBlock(player, gun, block);
    }

    public static abstract class GunHitBlockEventObject extends EventWrapper.EventObject {
        public abstract Object getLiving();

<<<<<<< HEAD
        public abstract ItemStack getItemStack();

        public abstract World getLevel();
=======
        public abstract Location getBlockPos();

        public abstract ItemStack getItemStack();

        public abstract World getWorld();
>>>>>>> b0e20feb0f34a730ef4c8abed901bfc4e4e16869
    }
}
