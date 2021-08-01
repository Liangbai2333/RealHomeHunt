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

package site.liangbai.realhomehunt.api.event.residence;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import site.liangbai.realhomehunt.api.event.EventCancellable;
import site.liangbai.realhomehunt.api.residence.Residence;
import site.liangbai.realhomehunt.api.cache.DamageCachePool;

public class ResidenceHurtEvent extends EventCancellable<ResidenceHurtEvent> {
    private final Player source;
    private final Residence residence;

    private final Block hitBlock;
    private final ItemStack gun;
    private final DamageCachePool.DamageCache damageCache;

    public ResidenceHurtEvent(Player source, Residence residence, Block hitBlock, ItemStack gun, DamageCachePool.DamageCache damageCache, double blockHardness) {
        this.source = source;
        this.residence = residence;
        this.hitBlock = hitBlock;
        this.gun = gun;
        this.damageCache = damageCache;
    }

    public Player getSource() {
        return source;
    }

    public Residence getResidence() {
        return residence;
    }

    public Block getHitBlock() {
        return hitBlock;
    }

    public Location getHitPos() {
        return getHitBlock().getLocation().clone();
    }

    public ItemStack getGun() {
        return gun;
    }

    public DamageCachePool.DamageCache getDamageCache() {
        return damageCache;
    }

    public double getBlockHardness() {
        return damageCache.getHardness();
    }

    public void setBlockHardness(double blockHardness) {
        damageCache.setHardness(blockHardness);
    }
}
