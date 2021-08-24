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

import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.entity.Player
import site.liangbai.realhomehunt.api.zone.Zone
import site.liangbai.realhomehunt.common.config.Config
import site.liangbai.realhomehunt.common.particle.EffectGroup
import site.liangbai.realhomehunt.util.kt.boundingBoxOf
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
            setPeriod(2)
            overlap(player)
        }.alwaysShowAsync()
    }

    fun useZoneEffectGroup(left: Location, right: Location, step: Double = 0.1, color: Color? = null, isBlockPos: Boolean = false, func: EffectGroup.() -> Unit): EffectGroup {
        val lines = Zone(left.toProxyLocation(), right.toProxyLocation()).releaseLines(isBlockPos)
            .onEach {
                it.step = step
            }

        return EffectGroup()
            .also {
                lines.forEach { line ->
                    it.addEffect(line)
                }

                it.withColor(color)
            }.apply(func)
    }
}