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

package site.liangbai.realhomehunt.api.event.residence

import org.bukkit.Location
import org.bukkit.command.CommandSender
import site.liangbai.realhomehunt.api.event.EventCancellable
import site.liangbai.realhomehunt.api.residence.Residence
import site.liangbai.realhomehunt.common.expand.Expand

open class ResidenceExpandEvent(val operator: CommandSender?, val residence: Residence?) :
    EventCancellable<ResidenceExpandEvent>() {

    class Pre(operator: CommandSender, residence: Residence, var expand: Expand, var size: Double) :
        ResidenceExpandEvent(operator, residence)

    class Post(
        operator: CommandSender,
        residence: Residence,
        var changedLeft: Location,
        var changedRight: Location,
        val expand: Expand,
        val size: Double
    ) : ResidenceExpandEvent(operator, residence)
}