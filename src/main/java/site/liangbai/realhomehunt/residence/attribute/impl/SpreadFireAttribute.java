package site.liangbai.realhomehunt.residence.attribute.impl;

import java.util.Map;

public final class SpreadFireAttribute extends BooleanAttribute {
    public SpreadFireAttribute() {
        super();
    }

    public SpreadFireAttribute(Map<String, Object> map) {
        super(map);
    }

    @Override
    public String getName() {
        return "spreadFire";
    }
}
