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

package site.liangbai.realhomehuntforge.mixin;

import com.craftingdead.core.util.RayTraceUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import site.liangbai.realhomehuntforge.event.BlockRayTraceEvent;

import java.util.Optional;

/**
 * The type Mixin ray trace util.
 *
 * @author 靓白
 */
@Mixin(RayTraceUtil.class)
public abstract class MixinRayTraceUtil {
    /**
     * @author Liangbai
     * @reason Liangbai low
     */
    @Overwrite(remap = false)
    public static Optional<BlockHitResult> rayTraceBlocks(Vec3 startPos, double distance,
                                                          Vec3 look,
                                                          ClipContext.Block blockMode,
                                                          ClipContext.Fluid fluidMode,
                                                          Level level) {
        var currentPos = startPos;
        final var endPos = startPos.add(look.scale(distance));

        BlockHitResult hitResult = null;
        BlockPos lastBlockPos = null;
        while (true) {
            if (currentPos.distanceTo(startPos) >= distance) {
                break;
            }

            var context = new ClipContext(currentPos, endPos, blockMode, fluidMode, null);
            hitResult = level.clip(context);

            boolean pierceableBlock;

            if (hitResult != null) {
                // Not sure about this one, but I have a concern about inaccuracy of Double which could lead
                // to an endless loop
                var blockPos = hitResult.getBlockPos();
                if (lastBlockPos != null && lastBlockPos.equals(blockPos)) {
                    break;
                }
                lastBlockPos = blockPos;

                BlockState blockState = level.getBlockState(blockPos);
                pierceableBlock = !blockState.canOcclude();

                BlockRayTraceEvent.TryPierceableBlock event = new BlockRayTraceEvent.TryPierceableBlock(level, hitResult, pierceableBlock);
                MinecraftForge.EVENT_BUS.post(event);
                pierceableBlock = event.isPierceable();

                if (pierceableBlock) {
                    var hitPos = hitResult.getLocation();
                    var shape = context.getBlockShape(blockState, level, blockPos);
                    if (!shape.isEmpty()) {
                        var bounds = shape.bounds();
                        var xDist = look.x() < 0.0D
                                ? hitPos.x() - bounds.minX - blockPos.getX()
                                : blockPos.getX() - hitPos.x() + bounds.maxX;
                        var yDist = look.y() < 0.0D
                                ? hitPos.y() - bounds.minY - blockPos.getY()
                                : blockPos.getY() - hitPos.y() + bounds.maxY;
                        var zDist = look.z() < 0.0D
                                ? hitPos.z() - bounds.minZ - blockPos.getZ()
                                : blockPos.getZ() - hitPos.z() + bounds.maxZ;
                        var xRayDist =
                                Math.abs(look.x()) != 0.0D ? xDist / Math.abs(look.x()) : Double.MAX_VALUE;
                        var yRayDist =
                                Math.abs(look.y()) != 0.0D ? yDist / Math.abs(look.y()) : Double.MAX_VALUE;
                        var zRayDist =
                                Math.abs(look.z()) != 0.0D ? zDist / Math.abs(look.z()) : Double.MAX_VALUE;

                        var rayDist = Math.min(xRayDist, Math.min(zRayDist, yRayDist));
                        currentPos = hitPos.add(look.scale(rayDist));
                    }
                }
            }
        }

        return Optional.ofNullable(hitResult);
    }
}
