package site.liangbai.realhomehunt.database.converter.list;

import javax.persistence.AttributeConverter;

public interface IJsonEntity<X extends IJsonEntity<X>> extends AttributeConverter<X, String> {
}
