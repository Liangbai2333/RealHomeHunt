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
import site.liangbai.realhomehunt.api.residence.attribute.IAttributable;

public class ResidenceSetAttributeEvent extends EventCancellable<ResidenceSetAttributeEvent> {
    private final Player owner;
    private final Residence residence;
    private final IAttributable<?> attributable;

    private String value;

    public ResidenceSetAttributeEvent(Player owner, Residence residence, IAttributable<?> attributable, String value) {
        this.owner = owner;
        this.residence = residence;
        this.attributable = attributable;
        this.value = value;
    }

    public Player getOwner() {
        return owner;
    }

    public Residence getResidence() {
        return residence;
    }

    public IAttributable<?> getAttributable() {
        return attributable;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
