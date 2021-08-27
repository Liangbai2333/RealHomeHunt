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

package site.liangbai.realhomehunt.util

import org.bukkit.Location
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import site.liangbai.realhomehunt.api.residence.Residence
import site.liangbai.realhomehunt.util.kt.boundingBoxOf
import site.liangbai.realhomehunt.util.kt.minus
import java.util.*
import kotlin.math.max
import kotlin.math.min

object Locations {

    @JvmStatic
    fun toBlockLocation(location: Location): Location {
        return Location(
            location.world, location.blockX.toDouble(), location.blockY
                .toDouble(), location.blockZ.toDouble(), location.yaw, location.pitch
        )
    }

    fun getAverageLocation(world: World?, left: Location, right: Location): Location {
        val defaultX = (left.blockX + right.blockX) / 2
        val defaultY = (left.blockY + right.blockY) / 2
        val defaultZ = (left.blockZ + right.blockZ) / 2
        return Location(world, defaultX.toDouble(), defaultY.toDouble(), defaultZ.toDouble())
    }

    fun countDistanceInfo(left: Location, right: Location): Vector {
        val box = boundingBoxOf(left, right, isBlockPos = true)

        val min = box.min
        val max = box.max

        return max - min
    }

    fun getRegionBlocks(left: Location, right: Location): List<Block> {
        check(left.world == right.world) { "left point's world must be same as the right's." }
        val blockList: MutableList<Block> = LinkedList()
        val x1 = left.blockX
        val x2 = right.blockX
        val y1 = left.blockY
        val y2 = right.blockY
        val z1 = left.blockZ
        val z2 = right.blockZ
        for (x in min(x1, x2)..max(x1, x2)) {
            for (y in min(y1, y2)..max(y1, y2)) {
                for (z in min(z1, z2)..max(z1, z2)) {
                    blockList.add(left.world!!.getBlockAt(x, y, z))
                }
            }
        }
        return blockList
    }

    fun teleportAfterChunkLoaded(player: Player, location: Location) {
        if (player.isDead) return
        val chunk = location.chunk
        if (!chunk.isLoaded) chunk.load()
        player.teleport(location)
    }
}

operator fun Residence.contains(location: Location) = location in left to right

operator fun kotlin.Pair<Location, Location>.contains(location: Location) = boundingBoxOf(first, second, isBlockPos = true).contains(location.toVector())