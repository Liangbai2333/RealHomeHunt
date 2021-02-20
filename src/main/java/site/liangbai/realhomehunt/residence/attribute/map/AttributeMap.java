package site.liangbai.realhomehunt.residence.attribute.map;

import site.liangbai.realhomehunt.residence.attribute.IAttributable;
import site.liangbai.realhomehunt.residence.attribute.impl.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class AttributeMap {
    private static final Map<String, Class<? extends IAttributable<?>>> map = new HashMap<>();

    static {
        registerAttribute("creature", CreatureAttribute.class);

        registerAttribute("animals", AnimalsAttribute.class);

        registerAttribute("monster", MonsterAttribute.class);

        registerAttribute("explode", ExplodeAttribute.class);

        registerAttribute("burn", BurnAttribute.class);

        registerAttribute("ignite", IgniteAttribute.class);

        registerAttribute("spreadFire", SpreadFireAttribute.class);
    }

    public static void registerAttribute(String type, Class<? extends IAttributable<?>> attributeClass) {
        map.put(type.toLowerCase(), attributeClass);
    }

    public static Class<? extends IAttributable<?>> getMap(String type) {
        return map.get(type);
    }

    public static List<String> getTypes() {
        return new ArrayList<>(map.keySet());
    }
}
