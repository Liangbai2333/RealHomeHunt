/*
 * RealHomeHunt
 * Copyright (C) 2021  Liangbai
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package site.liangbai.realhomehunt.api.residence.attribute.map;

import org.bukkit.configuration.serialization.ConfigurationSerialization;
import site.liangbai.realhomehunt.api.residence.attribute.impl.*;
import site.liangbai.realhomehunt.api.residence.attribute.IAttributable;

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
        registerAttribute("spread-fire", SpreadFireAttribute.class);
        registerAttribute("glow", GlowAttribute.class);
        registerAttribute("break", BreakAttribute.class);
        registerAttribute("place", PlaceAttribute.class);
        registerAttribute("build", BuildAttribute.class);
        registerAttribute("open-door", OpenDoorAttribute.class);
        registerAttribute("piston", PistonAttribute.class);
        registerAttribute("piston-protection", PistonProtectionAttribute.class);
    }

    public static void registerAttribute(String type, Class<? extends IAttributable<?>> attributeClass) {
        map.put(type, attributeClass);
    }

    public static void registerAttributeSerializer() {
        map.values().forEach(ConfigurationSerialization::registerClass);
    }

    @SuppressWarnings("unchecked")
    public static <T> Class<IAttributable<T>> getMapWithType(String type) {
        return (Class<IAttributable<T>>) getMap(type);
    }

    public static Class<? extends IAttributable<?>> getMap(String type) {
        return map.get(type);
    }

    public static List<String> getTypes() {
        return new ArrayList<>(map.keySet());
    }
}
