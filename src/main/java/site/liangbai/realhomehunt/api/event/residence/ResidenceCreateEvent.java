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

public class ResidenceCreateEvent extends EventCancellable<ResidenceCreateEvent> {
    private final Player player;
    private final Residence residence;

    public ResidenceCreateEvent(Player player, Residence residence) {
        this.player = player;
        this.residence = residence;
    }

    public Player getPlayer() {
        return player;
    }

    public Residence getResidence() {
        return residence;
    }

    // Before the block check called.
    public static class Pre extends ResidenceCreateEvent {
        private boolean checkBlock = true;

        public Pre(Player player, Residence residence) {
            super(player, residence);
        }

        public boolean isCheckBlock() {
            return checkBlock;
        }

        public void setCheckBlock(boolean checkBlock) {
            this.checkBlock = checkBlock;
        }
    }

    // Called after block checked.
    public static class Post extends ResidenceCreateEvent {

        public Post(Player player, Residence residence) {
            super(player, residence);
        }
    }
}
