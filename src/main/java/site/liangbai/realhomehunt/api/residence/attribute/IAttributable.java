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

package site.liangbai.realhomehunt.api.residence.attribute;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import site.liangbai.realhomehunt.common.database.converter.list.IJsonEntity;

import java.util.List;

public interface IAttributable<T> extends ConfigurationSerializable, IJsonEntity<IAttributable<T>> {
    T get();

    void set(T value);

    boolean allow(Object object);

    void force(Object value);

    String getName();

    List<String> allowValues();
}
