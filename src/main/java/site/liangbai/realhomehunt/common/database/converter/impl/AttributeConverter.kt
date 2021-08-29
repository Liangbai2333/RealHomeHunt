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
import site.liangbai.realhomehunt.api.residence.attribute.IAttributable
import site.liangbai.realhomehunt.api.residence.attribute.map.AttributeMap
import site.liangbai.realhomehunt.common.database.converter.IJsonEntity
import site.liangbai.realhomehunt.util.Ref

class AttributeConverter : IJsonEntity<IAttributable<*>> {
    override fun convertToDatabaseColumn(attribute: IAttributable<*>): String {
        val jsonObject = JsonObject()
        jsonObject.addProperty("target", attribute.name)
        jsonObject.addProperty("value", attribute.get().toString())
        return jsonObject.toString()
    }

    override fun convertToEntityAttribute(dbData: String): IAttributable<IAttributable<*>> {
        val jsonObject = JsonParser().parse(dbData).asJsonObject
        val target = jsonObject["target"].asString
        val clazz = AttributeMap.getMapWithType<IAttributable<*>>(target)
        val attributable = Ref.newInstance(clazz)
        attributable.force(jsonObject["value"].toString())
        return attributable
    }
}