package site.liangbai.realhomehunt.residence.attribute.impl;

import java.util.Map;

public final class IgniteAttribute extends BooleanAttribute {
    public IgniteAttribute() {
        super();
    }

    public IgniteAttribute(Map<String, Object> map) {
        super(map);
    }

    @Override
    public String getName() {
        return "ignite";
    }
}
