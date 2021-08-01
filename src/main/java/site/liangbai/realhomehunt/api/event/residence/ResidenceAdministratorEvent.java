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

import org.bukkit.entity.Player;
import site.liangbai.realhomehunt.api.event.EventCancellable;
import site.liangbai.realhomehunt.api.residence.Residence;

public class ResidenceAdministratorEvent extends EventCancellable<ResidenceAdministratorEvent> {
    private final Residence residence;
    private final Player operator;
    private final String targetName;

    public ResidenceAdministratorEvent(Residence residence, Player operator, String targetName) {
        this.residence = residence;
        this.operator = operator;
        this.targetName = targetName;
    }

    public Residence getResidence() {
        return residence;
    }

    public Player getOperator() {
        return operator;
    }

    public String getTargetName() {
        return targetName;
    }

    public static class Give extends ResidenceAdministratorEvent {

        public Give(Residence residence, Player operator, String targetName) {
            super(residence, operator, targetName);
        }
    }

    public static class Remove extends ResidenceAdministratorEvent {

        public Remove(Residence residence, Player operator, String targetName) {
            super(residence, operator, targetName);
        }
    }
}
