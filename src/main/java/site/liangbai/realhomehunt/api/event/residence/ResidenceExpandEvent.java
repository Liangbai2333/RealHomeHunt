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

package site.liangbai.realhomehunt.api.event.residence;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import site.liangbai.realhomehunt.api.event.EventCancellable;
import site.liangbai.realhomehunt.api.residence.Residence;
import site.liangbai.realhomehunt.common.expand.Expand;

public class ResidenceExpandEvent extends EventCancellable<ResidenceExpandEvent> {
    private final CommandSender operator;
    private final Residence residence;

    public ResidenceExpandEvent(CommandSender operator, Residence residence) {
        this.operator = operator;
        this.residence = residence;
    }

    public static class Pre extends ResidenceExpandEvent {
        private Expand expand;
        private double size;

        public Pre(CommandSender operator, Residence residence, Expand expand, double size) {
            super(operator, residence);
            this.expand = expand;
            this.size = size;
        }

        public Expand getExpand() {
            return expand;
        }

        public double getSize() {
            return size;
        }

        public void setExpand(Expand expand) {
            this.expand = expand;
        }

        public void setSize(double size) {
            this.size = size;
        }
    }

    public static class Post extends  ResidenceExpandEvent {
        private Location changedLeft;
        private Location changedRight;

        private final Expand expand;
        private final double size;

        public Post(CommandSender operator, Residence residence, Location changedLeft, Location changedRight, Expand expand, double size) {
            super(operator, residence);
            this.changedLeft = changedLeft;
            this.changedRight = changedRight;
            this.expand = expand;
            this.size = size;
        }

        public Location getChangedLeft() {
            return changedLeft;
        }

        public Location getChangedRight() {
            return changedRight;
        }

        public void setChangedLeft(Location changedLeft) {
            this.changedLeft = changedLeft;
        }

        public void setChangedRight(Location changedRight) {
            this.changedRight = changedRight;
        }

        public Expand getExpand() {
            return expand;
        }

        public double getSize() {
            return size;
        }
    }

    public CommandSender getOperator() {
        return operator;
    }

    public Residence getResidence() {
        return residence;
    }
}
