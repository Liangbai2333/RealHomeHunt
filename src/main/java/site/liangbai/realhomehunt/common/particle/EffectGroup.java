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

package site.liangbai.realhomehunt.common.particle;

import com.google.common.collect.Lists;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.util.List;

public class EffectGroup {

    /**
     * 特效表
     */
    private final List<ParticleObject> effectList;

    public EffectGroup() {
        effectList = Lists.newArrayList();
    }

    /**
     * 利用给定的特效列表构造出一个特效组
     *
     * @param effectList 特效列表
     */
    public EffectGroup(List<ParticleObject> effectList) {
        this.effectList = effectList;
    }

    /**
     * 往特效组添加一项特效
     *
     * @param particleObj 特效对象
     */
    public EffectGroup addEffect(ParticleObject particleObj) {
        effectList.add(particleObj);
        return this;
    }

    /**
     * 利用给定的下标, 将特效组里的第 index-1 个特效进行删除
     *
     * @param index 下标
     */
    public EffectGroup removeEffect(int index) {
        effectList.remove(index);
        return this;
    }

    /**
     * 利用给定的数字, 设置每一个特效的循环 tick
     *
     * @param period 循环tick
     */
    public EffectGroup setPeriod(long period) {
        effectList.forEach(effect -> effect.setPeriod(period));
        return this;
    }

    public EffectGroup setParticle(Particle particle) {
        effectList.forEach(effect -> effect.setParticle(particle));

        return this;
    }

    public EffectGroup setColor(Color color) {
        effectList.forEach(effect -> effect.setColor(color));

        return this;
    }

    /**
     * 将特效组内的特效一次性展现出来
     *
     */
    public EffectGroup show(Player... players) {
        show(Lists.newArrayList(players));
        return this;
    }

    public EffectGroup show(List<Player> players) {
        effectList.forEach(it -> it.show(players));
        return this;
    }

    public EffectGroup showAsync(Player... players) {
        show(Lists.newArrayList(players));
        return this;
    }

    public EffectGroup showAsync(List<Player> players) {
        effectList.forEach(it -> it.showAsync(players));
        return this;
    }

    public EffectGroup alwaysShow(Player... players) {
        alwaysShow(Lists.newArrayList(players));
        return this;
    }

    /**
     * 将特效组内的特效一直地展现出来
     *
     */
    public EffectGroup alwaysShow(List<Player> players) {
        effectList.forEach(it -> it.alwaysShow(players));
        return this;
    }

    public EffectGroup alwaysShowAsync(Player... players) {
        alwaysShowAsync(Lists.newArrayList(players));
        return this;
    }

    /**
     * 将特效组内的特效一直且异步地展现出来
     *
     */
    public EffectGroup alwaysShowAsync(List<Player> players) {
        effectList.forEach(it -> it.alwaysShowAsync(players));

        return this;
    }

    public List<ParticleObject> getEffectList() {
        return effectList;
    }

    public EffectGroup turnOff() {
        effectList.forEach(ParticleObject::turnOff);

        return this;
    }
}