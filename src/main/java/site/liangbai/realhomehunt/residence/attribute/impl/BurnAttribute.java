package site.liangbai.realhomehunt.residence.attribute.impl;

import java.util.Map;

public final class BurnAttribute extends BooleanAttribute {
    public BurnAttribute() {
        super();
    }

    public BurnAttribute(Map<String, Object> map) {
        super(map);
    }

    @Override
    public String getName() {
        return "burn";
    }
}
