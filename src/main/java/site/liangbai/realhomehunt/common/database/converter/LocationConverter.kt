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

package site.liangbai.realhomehunt.common.database.converter

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import javax.persistence.AttributeConverter
import javax.persistence.Converter

@Converter
class LocationConverter : AttributeConverter<Location, String> {
    companion object {
        fun Location.asJsonObject(): JsonObject {
            return JsonObject()
                .also {
                    if (world != null) {
                        it.addProperty("world", world!!.name)
                    }
                    it.addProperty("x", x)
                    it.addProperty("y", y)
                    it.addProperty("z", z)
                    it.addProperty("yaw", yaw)
                    it.addProperty("pitch", pitch)
                }
        }
    }

    override fun convertToDatabaseColumn(attribute: Location): String {
        return attribute.asJsonObject().toString()
    }

    override fun convertToEntityAttribute(dbData: String): Location {
        val jsonObject = JsonParser().parse(dbData).asJsonObject
        var world: World? = null
        if (jsonObject.has("world")) {
            val worldName = jsonObject["world"].asString
            world = Bukkit.getWorld(worldName)
            requireNotNull(world) { "unknown world: $worldName" }
        }
        val x = jsonObject["x"].asDouble
        val y = jsonObject["y"].asDouble
        val z = jsonObject["z"].asDouble
        val yaw = jsonObject["yaw"].asFloat
        val pitch = jsonObject["pitch"].asFloat
        return Location(world, x, y, z, yaw, pitch)
    }
}