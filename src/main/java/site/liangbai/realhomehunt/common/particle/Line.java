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

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.List;

public class Line extends ParticleObject {
    private Vector vector;
    private Location start;
    private Location end;
    /**
     * 步长
     */
    private double step;

    /**
     * 向量长度
     */
    private double length;

    public Line(Location start, Location end) {
        this(start, end, 0.1);
    }

    /**
     * 构造一个线
     *
     * @param start 线的起点
     * @param end   线的终点
     * @param step  每个粒子之间的间隔 (也即步长)
     */
    public Line(Location start, Location end, double step) {
        this(start, end, step, 20L);
    }

    /**
     * 构造一个线
     *
     * @param start  线的起点
     * @param end    线的终点
     * @param step   每个粒子之间的间隔 (也即步长)
     * @param period 特效周期(如果需要可以使用)
     */
    public Line(Location start, Location end, double step, long period) {
        this.start = start;
        this.end = end;
        this.step = step;
        setPeriod(period);

        // 对向量进行重置
        resetVector();
    }

    @Override
    public void show(List<Player> receivers) {
        for (double i = 0; i < length; i += step) {
            Vector vectorTemp = vector.clone().multiply(i);
            spawnParticle(receivers, start.clone().add(vectorTemp));
        }
    }

    @Override
    public Line clone() {
        Line line = new Line(start, end, step, getPeriod());

        line.setParticle(getParticle());
        line.setColor(getColor());

        return line;
    }


    public Location getStart() {
        return start;
    }

    public Line setStart(Location start) {
        this.start = start;
        resetVector();
        return this;
    }

    public Location getEnd() {
        return end;
    }

    public Line setEnd(Location end) {
        this.end = end;
        resetVector();
        return this;
    }

    public double getStep() {
        return step;
    }

    public Line setStep(double step) {
        this.step = step;
        resetVector();
        return this;
    }

    public void resetVector() {
        vector = end.clone().subtract(start).toVector();
        length = vector.length();
        vector.normalize();
    }
}
