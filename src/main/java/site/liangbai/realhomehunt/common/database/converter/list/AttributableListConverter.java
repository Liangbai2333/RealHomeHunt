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

package site.liangbai.realhomehunt.common.database.converter.list;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import site.liangbai.realhomehunt.api.residence.attribute.IAttributable;
import site.liangbai.realhomehunt.api.residence.attribute.map.AttributeMap;
import site.liangbai.realhomehunt.util.Ref;

import javax.persistence.Converter;

@Converter
public class AttributableListConverter<T> extends JsonListConverter<IAttributable<T>> {
    @Override
    public IAttributable<T> covertJsonToEntityAttribute(String jsonText) {
        JsonObject jsonObject = new JsonParser().parse(jsonText).getAsJsonObject();

        String target = jsonObject.get("target").getAsString();

        Class<IAttributable<T>> clazz = AttributeMap.getMapWithType(target);

        IAttributable<T> attributable = Ref.newInstance(clazz);

        return attributable.convertToEntityAttribute(jsonText);
    }
}
