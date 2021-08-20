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

package site.liangbai.realhomehunt.api.zone

import org.bukkit.Location
import org.bukkit.util.BoundingBox
import site.liangbai.realhomehunt.common.particle.Line
import site.liangbai.realhomehunt.util.kt.boundingBoxOf

class Zone(val left: Location, val right: Location) {
    fun releaseLines(useBlockPos: Boolean): List<Line> {
        require(left.world == right.world) { "the two points' world must be same." }
        val world = left.world
        val box = boundingBoxOf(left, right, isBlockPos = useBlockPos, clone = true)
        val minX = box.minX
        val minY = box.minY
        val minZ = box.minZ
        val maxX = box.maxX
        val maxY = box.maxY
        val maxZ = box.maxZ
        val pos1 = Location(world, minX, minY, minZ)
        val pos2 = Location(world, minX, minY, maxZ)
        val pos3 = Location(world, minX, maxY, minZ)
        val pos4 = Location(world, maxX, minY, minZ)
        val pos5 = Location(world, maxX, maxY, minZ)
        val pos6 = Location(world, maxX, maxY, maxZ)
        val pos7 = Location(world, maxX, minY, maxZ)
        val pos8 = Location(world, minX, maxY, maxZ)
        val line1 = Line(pos1, pos2)
        val line2 = line1.clone().setStart(pos1).setEnd(pos3)
        val line3 = line1.clone().setStart(pos1).setEnd(pos4)
        val line4 = line1.clone().setStart(pos6).setEnd(pos5)
        val line5 = line1.clone().setStart(pos6).setEnd(pos7)
        val line6 = line1.clone().setStart(pos6).setEnd(pos8)
        val line7 = line1.clone().setStart(pos3).setEnd(pos5)
        val line8 = line1.clone().setStart(pos3).setEnd(pos8)
        val line9 = line1.clone().setStart(pos7).setEnd(pos4)
        val line10 = line1.clone().setStart(pos7).setEnd(pos2)
        val line11 = line1.clone().setStart(pos4).setEnd(pos5)
        val line12 = line1.clone().setStart(pos2).setEnd(pos8)

        return listOf(line1, line2, line3, line4, line5, line6, line7, line8, line9, line10, line11, line12)
    }
}