package site.liangbai.realhomehunt.residence.attribute.impl;

import java.util.Map;

public class CreatureAttribute extends BooleanAttribute {
    public CreatureAttribute() {
        super();

        set(true);
    }

    public CreatureAttribute(Map<String, Object> map) {
        super(map);
    }

    @Override
    public String getName() {
        return "creature";
    }
}
