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

package site.liangbai.realhomehunt.common.database.converter;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class LocationConverter implements AttributeConverter<Location, String> {

    @Override
    public String convertToDatabaseColumn(Location attribute) {
        JsonObject jsonObject = new JsonObject();

        if (attribute.getWorld() != null) {
            jsonObject.addProperty("world", attribute.getWorld().getName());
        }

        jsonObject.addProperty("x", attribute.getX());
        jsonObject.addProperty("y", attribute.getY());
        jsonObject.addProperty("z", attribute.getZ());

        jsonObject.addProperty("yaw", attribute.getYaw());
        jsonObject.addProperty("pitch", attribute.getPitch());

        return jsonObject.toString();
    }

    @Override
    public Location convertToEntityAttribute(String dbData) {
        JsonObject jsonObject = new JsonParser().parse(dbData).getAsJsonObject();

        World world = null;
        if (jsonObject.has("world")) {
            String worldName = jsonObject.get("world").getAsString();
            world = Bukkit.getWorld(worldName);
            if (world == null) {
                throw new IllegalArgumentException("unknown world: " + worldName);
            }
        }

        double x = jsonObject.get("x").getAsDouble();
        double y = jsonObject.get("y").getAsDouble();
        double z = jsonObject.get("z").getAsDouble();

        float yaw = jsonObject.get("yaw").getAsFloat();
        float pitch = jsonObject.get("pitch").getAsFloat();

        return new Location(world, x, y, z, yaw, pitch);
    }
}
