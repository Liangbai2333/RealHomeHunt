package site.liangbai.realhomehunt.database.converter.list;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.ArrayList;
import java.util.List;

@Converter
public class StringListConverter implements AttributeConverter<List<String>, String> {
    @Override
    public String convertToDatabaseColumn(List<String> attribute) {
        JsonArray jsonArray = new JsonArray();

        attribute.forEach(jsonArray::add);

        return jsonArray.toString();
    }

    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        JsonArray jsonArray = new JsonParser().parse(dbData).getAsJsonArray();

        List<String> list = new ArrayList<>();

        jsonArray.forEach(it -> list.add(it.getAsString()));

        return list;
    }
}
