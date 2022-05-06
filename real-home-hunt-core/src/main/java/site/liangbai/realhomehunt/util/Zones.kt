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

package site.liangbai.realhomehunt.util

import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.entity.Player
import site.liangbai.realhomehunt.common.config.Config
import site.liangbai.realhomehunt.common.particle.EffectGroup
import site.liangbai.realhomehunt.util.kt.boundingBoxOf
import taboolib.module.effect.Cube
import taboolib.module.effect.ParticleSpawner
import taboolib.platform.util.toProxyLocation

/**
 * The type Zones.
 * 初中生, 水平受限
 *
 * @author Liangbai
 * @since 2021 /08/12 10:15 上午
 */
object Zones {
    @JvmStatic
    fun startShowWithBlockLocation(player: Player, left: Location, right: Location): EffectGroup {
        require(left.world == right.world) { "the two points' world must be same." }
        val box = boundingBoxOf(left, right, clone = true)
        val world = left.world!!
        val min = box.min.toLocation(world).add(-0.2, -0.2, -0.2)
        val max = box.max.toLocation(world).add(0.2, 0.2, 0.2)

        return useZoneEffectGroup(min, max, step = Config.residence.tool.showParticleStep, isBlockPos = true) {
            withParticle(Config.residence.tool.showParticle)
            withColor(if (Config.residence.tool.particleColor.enabled) Config.residence.tool.particleColor.color else if (Config.residence.tool.showParticle == Particle.REDSTONE) Color.RED else null)
            withPeriod(2)
            overlap(player)
        }.alwaysShowAsync()
    }

    fun useZoneEffectGroup(left: Location, right: Location, step: Double = 0.2, color: Color? = null, isBlockPos: Boolean = false, func: EffectGroup.() -> Unit): EffectGroup {
        require(left.world == right.world) { "the two points' world must be same." }
        val box = boundingBoxOf(left, right, isBlockPos, clone = true)

        val minLoc = box.min.toLocation(left.world!!).toProxyLocation()
        val maxLoc = box.max.toLocation(left.world!!).toProxyLocation()

        val cube = Cube(minLoc, maxLoc, step, object : ParticleSpawner {
            override fun spawn(location: taboolib.common.util.Location) {
                // TODO, empty object for custom.
            }
        })

        return EffectGroup()
            .addEffect(cube)
            .withColor(color)
            .apply(func)
    }
}