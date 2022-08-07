/*
 * RealHomeHunt
 * Copyright (C) 2022  Liangbai
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

package site.liangbai.realhomehunt.internal.processor;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import site.liangbai.realhomehunt.api.residence.Residence;
import site.liangbai.realhomehunt.internal.processor.block.IBlockBreakProcessor;
import site.liangbai.realhomehunt.internal.processor.block.impl.AutoFixBlockProcessor;
import site.liangbai.realhomehunt.internal.processor.block.impl.RobChestProcessor;
import site.liangbai.realhomehunt.internal.processor.gun.IGunHitBlockProcessor;
import site.liangbai.realhomehunt.internal.processor.gun.impl.GunHitBlockProcessorImpl;
import site.liangbai.realhomehunt.util.callback.ICallback;
import site.liangbai.realhomehunt.util.callback.impl.BooleanCallback;

import java.util.ArrayList;
import java.util.List;

public final class Processors {
    // Gun
    public static final IGunHitBlockProcessor GUN_HIT_BLOCK_PROCESSOR = new GunHitBlockProcessorImpl();

    // Block
    public static final List<IBlockBreakProcessor> BLOCK_BREAK_PROCESSOR_LIST = new ArrayList<>();

    static {
        BLOCK_BREAK_PROCESSOR_LIST.add(new AutoFixBlockProcessor());
        BLOCK_BREAK_PROCESSOR_LIST.add(new RobChestProcessor());
    }

    public static ICallback<Boolean> submitBlockProcessors(Residence residence, Player player, ItemStack gun, Block block, BlockState snapshotState, BlockData blockData) {
        ICallback<Boolean> callBack = new BooleanCallback();
        callBack.set(true);
        BLOCK_BREAK_PROCESSOR_LIST.stream()
                .filter(IBlockBreakProcessor::isEnabled)
                .forEach(it -> it.process(callBack, residence, player, gun, block, snapshotState, blockData));

        return callBack;
    }
}
