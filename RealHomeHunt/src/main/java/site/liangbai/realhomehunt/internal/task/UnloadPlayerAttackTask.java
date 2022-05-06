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

package site.liangbai.realhomehunt.internal.task;

import org.bukkit.scheduler.BukkitRunnable;
import site.liangbai.realhomehunt.api.residence.Residence;

public final class UnloadPlayerAttackTask extends BukkitRunnable {
    private final Residence residence;

    private final String attack;

    public UnloadPlayerAttackTask(Residence residence, String attack) {
        this.residence = residence;
        this.attack = attack;
    }

    @Override
    public void run() {
        residence.removeAttack(attack);
    }
}
