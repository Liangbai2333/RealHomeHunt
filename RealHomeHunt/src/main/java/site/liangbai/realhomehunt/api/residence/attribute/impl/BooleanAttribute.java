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

package site.liangbai.realhomehunt.api.residence.attribute.impl;

import org.jetbrains.annotations.NotNull;
import site.liangbai.realhomehunt.api.residence.attribute.IAttributable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public abstract class BooleanAttribute implements IAttributable<Boolean> {
    private boolean value;

    private static final Pattern booleanPattern = Pattern.compile("true|false");

    public BooleanAttribute() {
        this.value = false;
    }

    public BooleanAttribute(Map<String, Object> map) {
        this.value = (boolean) map.get("value");
    }

    @Override
    public Boolean get() {
        return value;
    }

    @Override
    public void set(Boolean value) {
        this.value = value;
    }

    @Override
    public boolean allow(Object object) {
        if (object instanceof String str) {
            return booleanPattern.matcher(str).matches();
        }

        return object.getClass().equals(Boolean.class);
    }

    @Override
    public void force(Object value) {
        if (value instanceof String str) {
            set(Boolean.parseBoolean(str));

            return;
        }

        set((Boolean) value);
    }

    @Override
    public List<String> allowValues() {
        return Arrays.asList("true", "false");
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();

        map.put("value", get());

        return map;
    }
}
