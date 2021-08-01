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

package site.liangbai.realhomehunt.gamemode.manager;

import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import site.liangbai.realhomehunt.api.cache.DamageCachePool;
import site.liangbai.realhomehunt.gamemode.IGameMode;
import site.liangbai.realhomehunt.gamemode.impl.AutoFixGameMode;
import site.liangbai.realhomehunt.gamemode.impl.RobChestGameMode;
import site.liangbai.realhomehunt.api.residence.Residence;
import site.liangbai.realhomehunt.util.callback.ICallBack;
import site.liangbai.realhomehunt.util.callback.impl.BooleanCallBack;

import java.util.ArrayList;
import java.util.List;

public final class GameModeManager {
    private static final List<IGameMode> GAME_MODES = new ArrayList<>();

    static {
        GAME_MODES.add(new AutoFixGameMode());
        GAME_MODES.add(new RobChestGameMode());
    }

    public static ICallBack<Boolean> submit(Residence residence, Player player, ItemStack gun, Block block, BlockData blockData, DamageCachePool.DamageCache damageCache) {
        ICallBack<Boolean> callBack = new BooleanCallBack();

        GAME_MODES.stream()
                .filter(IGameMode::isEnabled)
                .forEach(it -> it.process(callBack, residence, player, gun, block, blockData, damageCache));

        return callBack;
    }
}