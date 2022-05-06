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

package site.liangbai.realhomehunt.common.particle

import com.google.common.collect.Lists
import org.bukkit.Color
import org.bukkit.Particle
import org.bukkit.entity.Player
import site.liangbai.realhomehunt.util.Particles
import taboolib.common.util.Location
import taboolib.module.effect.ParticleObj
import taboolib.module.effect.ParticleSpawner
import taboolib.platform.util.toBukkitLocation

class EffectGroup {
    /**
     * 特效表
     */
    private val effectList: MutableList<ParticleObj>

    private var particle = Particle.REDSTONE

    private var color: Color? = null


    constructor() {
        effectList = Lists.newArrayList()
    }

    /**
     * 利用给定的特效列表构造出一个特效组
     *
     * @param effectList 特效列表
     */
    constructor(effectList: MutableList<ParticleObj>) {
        this.effectList = effectList
    }

    /**
     * 往特效组添加一项特效
     *
     * @param particleObj 特效对象
     */
    fun addEffect(particleObj: ParticleObj): EffectGroup {
        effectList.add(particleObj)
        return this
    }

    /**
     * 利用给定的下标, 将特效组里的第 index-1 个特效进行删除
     *
     * @param index 下标
     */
    fun removeEffect(index: Int): EffectGroup {
        effectList.removeAt(index)
        return this
    }

    /**
     * 利用给定的数字, 设置每一个特效的循环 tick
     *
     * @param period 循环tick
     */
    fun withPeriod(period: Long): EffectGroup {
        effectList.forEach { it.period = period }
        return this
    }

    /**
     * 将特效组内的特效一次性展现出来
     *
     */
    fun show(): EffectGroup {
        effectList.forEach { it.show() }

        return this
    }

    fun alwaysShow(): EffectGroup {
        effectList.forEach { it.alwaysShow() }

        return this
    }

    /**
     * 将特效组内的特效一直且异步地展现出来
     *
     */
    fun alwaysShowAsync(): EffectGroup {
        effectList.forEach { it.alwaysShowAsync() }

        return this
    }

    fun getEffectList(): List<ParticleObj> {
        return effectList
    }

    fun overlap(receivers: List<Player>): EffectGroup {
        val spawner = InternalParticleSpawner(this, receivers)
        effectList.forEach { it.spawner = spawner }

        return this
    }

    fun overlap(vararg players: Player): EffectGroup {
        overlap(players.toList())

        return this
    }

    fun withParticle(particle: Particle): EffectGroup {
        this.particle = particle

        return this
    }

    fun withColor(color: Color?): EffectGroup {
        this.color = color

        return this
    }

    fun turnOff(): EffectGroup {
        effectList.forEach { it.turnOffTask() }

        return this
    }

    internal class InternalParticleSpawner(private val effectGroup: EffectGroup, private val receivers: List<Player>) : ParticleSpawner {
        override fun spawn(location: Location) {
            val color = effectGroup.color

            val dustOptions = if (color != null) Particle.DustOptions(color, 1.0F) else null

            Particles.spawnParticle(effectGroup.particle, receivers, location.toBukkitLocation(), dustOptions)
        }
    }
}