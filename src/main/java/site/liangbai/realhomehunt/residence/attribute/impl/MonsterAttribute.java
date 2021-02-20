package site.liangbai.realhomehunt.residence.attribute.impl;

import java.util.Map;

public final class MonsterAttribute extends CreatureAttribute {
    public MonsterAttribute() {
        super();
    }

    public MonsterAttribute(Map<String, Object> map) {
        super(map);
    }

    @Override
    public String getName() {
        return "monster";
    }
}
