/*
 * RealHomeHunt
 * Copyright (C) 2022  Liangbai
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
    private static final Map<String, Class<? extends IAttributable<?>>> stringToTypeMap = new HashMap<>();
    private static final Map<Class<? extends IAttributable<?>>, String> typeToStringMap = new HashMap<>();

    static {
        registerAttribute("creature", Creature.class);
        registerAttribute("animals", Animals.class);
        registerAttribute("monster", Monster.class);
        registerAttribute("explode", Explode.class);
        registerAttribute("burn", Burn.class);
        registerAttribute("ignite", Ignite.class);
        registerAttribute("spread-fire", SpreadFire.class);
        registerAttribute("glow", Glow.class);
        registerAttribute("break", Break.class);
        registerAttribute("place", Place.class);
        registerAttribute("build", Build.class);
        registerAttribute("open-door", OpenDoor.class);
        registerAttribute("piston", Piston.class);
        registerAttribute("piston-protection", PistonProtection.class);
    }

    public static void registerAttribute(String type, Class<? extends IAttributable<?>> attributeClass) {
        stringToTypeMap.put(type, attributeClass);
        typeToStringMap.put(attributeClass, type);
    }

    public static void registerAttributeSerializer() {
        stringToTypeMap.values().forEach(ConfigurationSerialization::registerClass);
    }

    @SuppressWarnings("unchecked")
    public static <T> Class<IAttributable<T>> getMapWithType(String type) {
        return (Class<IAttributable<T>>) getMap(type);
    }

    public static String getRefName(IAttributable<?> attributable) {
        return typeToStringMap.get(attributable.getClass());
    }

    public static Class<? extends IAttributable<?>> getMap(String type) {
        return stringToTypeMap.get(type);
    }

    public static List<String> getTypes() {
        return new ArrayList<>(stringToTypeMap.keySet());
    }
}
