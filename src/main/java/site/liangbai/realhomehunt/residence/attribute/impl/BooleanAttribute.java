package site.liangbai.realhomehunt.residence.attribute.impl;

import org.jetbrains.annotations.NotNull;
import site.liangbai.realhomehunt.residence.attribute.IAttributable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public abstract class BooleanAttribute implements IAttributable<Boolean> {
    private boolean value;

    private static final Pattern booleanPattern = Pattern.compile("true|false");

    public BooleanAttribute() {
        this.value = false;
    }

    public BooleanAttribute(Map<String, Object> map) {
        this.value = (boolean) map.get("value");
    }

    @Override
    public Boolean get() {
        return value;
    }

    @Override
    public void set(Boolean value) {
        this.value = value;
    }

    @Override
    public boolean allow(Object object) {
        if (object instanceof String) {
            String str = (String) object;

            return booleanPattern.matcher(str).matches();
        }

        return object.getClass().equals(Boolean.class);
    }

    @Override
    public void force(Object value) {
        if (value instanceof String) {
            set(Boolean.parseBoolean((String) value));

            return;
        }

        set((Boolean) value);
    }

    @Override
    public String getName() {
        return "boolean";
    }

    @Override
    public List<String> allowValues() {
        return Arrays.asList("true", "false");
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();

        map.put("value", get());

        return map;
    }
}
