package site.liangbai.realhomehunt.residence.attribute.impl;

import java.util.Map;

public final class AnimalsAttribute extends CreatureAttribute {
    public AnimalsAttribute() {
        super();
    }

    public AnimalsAttribute(Map<String, Object> map) {
        super(map);
    }

    @Override
    public String getName() {
        return "animals";
    }
}
