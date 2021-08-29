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

package site.liangbai.realhomehunt.common.database

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.bukkit.Location
import site.liangbai.realhomehunt.api.residence.Residence
import site.liangbai.realhomehunt.api.residence.attribute.IAttributable
import site.liangbai.realhomehunt.api.zone.Zone
import site.liangbai.realhomehunt.common.database.converter.IJsonEntity
import site.liangbai.realhomehunt.common.database.converter.impl.AttributeConverter
import site.liangbai.realhomehunt.common.database.converter.impl.LocationConverter
import site.liangbai.realhomehunt.common.database.converter.impl.ZoneConverter
import taboolib.common.reflect.Reflex.Companion.invokeConstructor
import kotlin.reflect.KClass

internal object ConverterManager {
    private val JSON_PARSER = JsonParser()
    private val converters = mutableMapOf<Class<*>, Class<out IJsonEntity<out Any>>>()
    private val nameConverters = mutableMapOf<String, Class<out IJsonEntity<out Any>>>()

    private val converterInstances = mutableMapOf<Class<*>, IJsonEntity<out Any>>()

    init {
        Zone::class convert ZoneConverter::class
        Location::class convert LocationConverter::class
        IAttributable::class convert AttributeConverter::class
        Residence.IgnoreBlockInfo::class convert Residence.IgnoreBlockInfo::class
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T : Any> Class<out IJsonEntity<out T>>.findInstance(): IJsonEntity<out T> {
        return converterInstances.computeIfAbsent(this) {
            it.invokeConstructor() as IJsonEntity<T>
        } as IJsonEntity<T>
    }

    private infix fun KClass<*>.convert(parser: KClass<out IJsonEntity<out Any>>) {
        converters[java] = parser.java
        nameConverters[parser.java.simpleName] = parser.java
    }

    @Suppress("UNCHECKED_CAST")
    fun matches(type: Class<*>): IJsonEntity<Any> {
        for (converter in converters) {
            if (converter.key.isAssignableFrom(type)) {
                return converter.value.findInstance() as IJsonEntity<Any>
            }
        }

        return EmptyConverter
    }

    internal object EmptyConverter :
        IJsonEntity<Any> {
        override fun convertToDatabaseColumn(attribute: Any): String {
            return attribute.toString()
        }

        override fun convertToEntityAttribute(dbData: String): Any {
            return JSON_PARSER.parse(dbData).toString()
        }
    }

    private fun Any.convertToJsonObject(): JsonObject {
        val jsonObject = JsonObject()
        val converter = matches(this::class.java)
        jsonObject.addProperty("converter", converter::class.java.simpleName)
        jsonObject.addProperty("data", converter.convertToDatabaseColumn(this))
        val jsonElement = JSON_PARSER.parse(converter.convertToDatabaseColumn(this))
            ?: throw IllegalArgumentException("convert value must be a json element text.")
        jsonObject.add("data", jsonElement)
        return jsonObject
    }

    fun Any.convertToString(): String {
        if (this is String) {
            return this
        }

        return convertToJsonObject().toString()
    }

    fun <T> List<T>.convertToString(): String {
        val jsonArray = JsonArray()

        for (value in this) {
            if (value is String) {
                jsonArray.add(value)
            } else {
                jsonArray.add(value?.convertToJsonObject())
            }
        }

        return jsonArray.toString()
    }

    @Suppress("UNCHECKED_CAST", "DEPRECATION")
    fun <T> String.convertToEntity(): T {
        val jsonElement = JSON_PARSER.parse(this)
        if (!jsonElement.isJsonObject) {
            return this as T
        }
        var jsonObject: JsonObject
        if (!jsonElement.asJsonObject.also { jsonObject = it }.has("converter")) {
            return forceConvertToEntity<T>()
                ?: throw IllegalArgumentException("could not convert the json: $jsonObject")
        }
        val converter = nameConverters[jsonObject.get("converter").asString]!!.findInstance()
        val data = jsonObject.get("data").toString()
        return converter.convertToEntityAttribute(data) as T
    }

    fun <T> String.convertToEntityList(): List<T> {
        val jsonArray = JSON_PARSER.parse(this).asJsonArray
        val list = mutableListOf<T>()

        jsonArray.forEach {
            if (it.isJsonPrimitive) {
                list.add(it.asString.convertToEntity())
            } else {
                list.add(it.toString().convertToEntity())
            }
        }

        return list
    }

    // For origin
    @Suppress("UNCHECKED_CAST")
    @Deprecated("It is only used to merge origin (before 1.4.0) and will be removed soon.")
    private fun <T> String.forceConvertToEntity(): T? {
        for (value in converters.values) {
            val instance = value.findInstance()

            try {
                return instance.convertToEntityAttribute(this) as T
            } catch (ignored: Throwable) { }
        }

        return null
    }
}