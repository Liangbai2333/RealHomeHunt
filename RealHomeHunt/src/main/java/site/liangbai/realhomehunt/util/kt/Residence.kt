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

package site.liangbai.realhomehunt.util.kt

import site.liangbai.realhomehunt.api.residence.Residence

fun Residence.getAdministratorListString(): String {
    val stringBuilder = StringBuilder()
    if (administrators.isEmpty()) return ""
    administrators.forEach {
        stringBuilder.append(
            " "
        ).append("-").append(" ").append(it).append("\n")
    }
    return stringBuilder.substring(0, stringBuilder.lastIndexOf("\n"))
}

fun Residence.getAttributeListString(): String {
    val stringBuilder = StringBuilder()
    if (attributes.isEmpty()) return ""
    attributes.forEach {
        stringBuilder.append(
            " "
        ).append("-").append(" ").append(it.name).append(":").append(" ").append(it.get()).append("\n")
    }
    return stringBuilder.substring(0, stringBuilder.lastIndexOf("\n"))
}