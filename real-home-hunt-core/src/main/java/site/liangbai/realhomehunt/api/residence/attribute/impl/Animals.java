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

import java.util.Map;

/**
 * The type Animals attribute.
 *
 * @author Liangbai
 * @since 2021 /08/10 05:00 下午
 */
public final class Animals extends Creature {
    public Animals() {
    }

    public Animals(Map<String, Object> map) {
        super(map);
    }

    @Override
    public String getName() {
        return "animals";
    }
}