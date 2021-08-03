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

package site.liangbai.realhomehunt.database.converter.list;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import javax.persistence.AttributeConverter;
import java.util.ArrayList;
import java.util.List;

/*
Base type should be not used.
 */
public abstract class JsonListConverter<X extends IJsonEntity<X>> implements AttributeConverter<List<X>, String> {
    @Override
    public String convertToDatabaseColumn(List<X> attribute) {
        JsonArray jsonArray = new JsonArray();

        attribute.forEach(it -> jsonArray.add(it.convertToDatabaseColumn(it)));

        return jsonArray.toString();
    }

    @Override
    public List<X> convertToEntityAttribute(String dbData) {
        JsonArray jsonArray = new JsonParser().parse(dbData).getAsJsonArray();

        List<X> list = new ArrayList<>();

        jsonArray.forEach(it -> {
            String jsonText = it.getAsString();

            list.add(covertJsonToEntityAttribute(jsonText));
        });

        return list;
    }

    public abstract X covertJsonToEntityAttribute(String jsonText);
}
