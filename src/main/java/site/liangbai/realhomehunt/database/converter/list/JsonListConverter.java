package site.liangbai.realhomehunt.database.converter.list;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import javax.persistence.AttributeConverter;
import java.util.ArrayList;
import java.util.List;

/*
Base type should be not use it.
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
