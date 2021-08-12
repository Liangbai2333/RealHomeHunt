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
import org.bukkit.entity.Player;
import site.liangbai.realhomehunt.api.event.EventCancellable;
import site.liangbai.realhomehunt.api.residence.Residence;

public class ResidenceReelectEvent extends EventCancellable<ResidenceReelectEvent> {
    private final Player operator;
    private final Residence residence;

    private Location changedLeft;
    private Location changedRight;

    public ResidenceReelectEvent(Player operator, Residence residence, Location changedLeft, Location changedRight) {
        this.operator = operator;
        this.residence = residence;
        this.changedLeft = changedLeft;
        this.changedRight = changedRight;
    }

    public Player getOperator() {
        return operator;
    }

    public Residence getResidence() {
        return residence;
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
}
