package site.liangbai.realhomehunt.residence.attribute;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.List;

public interface IAttributable<T> extends ConfigurationSerializable {
    T get();

    void set(T value);

    boolean allow(Object object);

    void force(Object value);

    String getName();

    List<String> allowValues();
}
