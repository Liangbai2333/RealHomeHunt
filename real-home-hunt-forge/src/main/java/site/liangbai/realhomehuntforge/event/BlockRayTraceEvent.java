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

package site.liangbai.realhomehuntforge.event;

import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.eventbus.api.Event;

/**
 * The type Ray trace event.
 *
 * @author 靓白
 */
public class BlockRayTraceEvent extends Event {
    private final Level level;
    private final BlockHitResult rayTraceResult;

    public BlockRayTraceEvent(Level level, BlockHitResult rayTraceResult) {
        this.level = level;
        this.rayTraceResult = rayTraceResult;
    }

    public Level getLevel() {
        return level;
    }

    public BlockHitResult getRayTraceResult() {
        return rayTraceResult;
    }

    /**
     * 在RayTrace时, 遇到类似玻璃这样的可刺穿方块, 默认为不能打击
     * 所以你可以通过这个事件让玻璃变得可以打击以及修改其他的方块是否可以被打击.
     * 如果pierceable被设置为false, 那么打击的方块即为当前方块
     * 这个事件可能在同一tick内触发非常多次
     *
     * @author 靓白
     */
    public static class TryPierceableBlock extends BlockRayTraceEvent {
        private boolean pierceable;

        public TryPierceableBlock(Level level, BlockHitResult rayTraceResult, boolean pierceable) {
            super(level, rayTraceResult);
            this.pierceable = pierceable;
        }

        public boolean isPierceable() {
            return pierceable;
        }

        public void setPierceable(boolean pierceable) {
            this.pierceable = pierceable;
        }
    }
}
