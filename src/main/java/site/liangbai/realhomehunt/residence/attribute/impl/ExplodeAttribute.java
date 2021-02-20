package site.liangbai.realhomehunt.residence.attribute.impl;

import java.util.Map;

public final class ExplodeAttribute extends BooleanAttribute {
    public ExplodeAttribute() {
        super();
    }

    public ExplodeAttribute(Map<String, Object> map) {
        super(map);
    }

    @Override
    public String getName() {
        return "explode";
    }
}
