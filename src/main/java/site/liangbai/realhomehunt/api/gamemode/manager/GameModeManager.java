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

package site.liangbai.realhomehunt.api.gamemode.manager;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import site.liangbai.realhomehunt.api.residence.Residence;
import site.liangbai.realhomehunt.api.gamemode.IGameMode;
import site.liangbai.realhomehunt.api.gamemode.impl.AutoFixGameMode;
import site.liangbai.realhomehunt.api.gamemode.impl.RobChestGameMode;
import site.liangbai.realhomehunt.util.callback.ICallback;
import site.liangbai.realhomehunt.util.callback.impl.BooleanCallback;

import java.util.ArrayList;
import java.util.List;

public final class GameModeManager {
    private static final List<IGameMode> GAME_MODES = new ArrayList<>();

    static {
        GAME_MODES.add(new AutoFixGameMode());
        GAME_MODES.add(new RobChestGameMode());
    }

    public static void registerGameMode(IGameMode gameMode) {
        GAME_MODES.add(gameMode);
    }

    public static void unregisterGameMode(IGameMode gameMode) {
        GAME_MODES.remove(gameMode);
    }

    public static ICallback<Boolean> submit(Residence residence, Player player, ItemStack gun, Block block, BlockState snapshotState, BlockData blockData) {
        ICallback<Boolean> callBack = new BooleanCallback();

        callBack.set(true);

        GAME_MODES.stream()
                .filter(IGameMode::isEnabled)
                .forEach(it -> it.process(callBack, residence, player, gun, block, snapshotState, blockData));

        return callBack;
    }
}
