package site.liangbai.realhomehunt.database.converter.list;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import site.liangbai.realhomehunt.api.residence.attribute.IAttributable;
import site.liangbai.realhomehunt.util.Ref;

import javax.persistence.Converter;

@Converter
public class AttributableListConverter<T> extends JsonListConverter<IAttributable<T>> {
    @SuppressWarnings("unchecked")
    @Override
    public IAttributable<T> covertJsonToEntityAttribute(String jsonText) {
        JsonObject jsonObject = new JsonParser().parse(jsonText).getAsJsonObject();

        String target = jsonObject.get("target").getAsString();

        try {
            Class<IAttributable<T>> clazz = (Class<IAttributable<T>>) Class.forName(target);

            IAttributable<T> attributable = Ref.newInstance(clazz);

            return attributable.convertToEntityAttribute(jsonText);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("could not find an empty constructor for: " + target, e);
        }
    }
}
