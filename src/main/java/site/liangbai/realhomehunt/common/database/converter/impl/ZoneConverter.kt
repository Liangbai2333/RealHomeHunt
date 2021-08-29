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

package site.liangbai.realhomehunt.common.database.converter.impl

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import site.liangbai.realhomehunt.api.zone.Zone
import site.liangbai.realhomehunt.common.database.converter.IJsonEntity
import site.liangbai.realhomehunt.common.database.converter.impl.LocationConverter.Companion.asJsonObject
import taboolib.platform.util.toBukkitLocation
import taboolib.platform.util.toProxyLocation

// Pre for 1.5
class ZoneConverter : IJsonEntity<Zone> {
    private val locationConverter = LocationConverter()

    override fun convertToDatabaseColumn(attribute: Zone): String {
        val json = JsonObject()
        val leftJson = attribute.left.toBukkitLocation().asJsonObject()
        val rightJson = attribute.right.toBukkitLocation().asJsonObject()

        json.add("left", leftJson)
        json.add("right", rightJson)

        return json.toString()
    }

    override fun convertToEntityAttribute(dbData: String): Zone {
        val json = JsonParser().parse(dbData).asJsonObject

        val left = locationConverter.convertToEntityAttribute(json["left"].toString())
        val right = locationConverter.convertToEntityAttribute(json["right"].toString())

        return Zone(left.toProxyLocation(), right.toProxyLocation())
    }
}