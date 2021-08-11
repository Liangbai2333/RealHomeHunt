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

package site.liangbai.realhomehunt.internal.command.subtabcompleter.impl;

import org.bukkit.command.CommandSender;
import site.liangbai.realhomehunt.internal.command.subtabcompleter.ISubTabCompleter;
import site.liangbai.realhomehunt.api.residence.manager.ResidenceManager;
import site.liangbai.realhomehunt.api.residence.Residence;
import site.liangbai.realhomehunt.api.residence.attribute.IAttributable;
import site.liangbai.realhomehunt.api.residence.attribute.map.AttributeMap;

import java.util.List;

public final class SetTabCompleter implements ISubTabCompleter {
    @Override
    public List<String> handle(CommandSender sender, int length, String[] args) {
        if (length == 2) {
            return AttributeMap.getTypes();
        }

        if (length == 3) {
            String type = args[1].toLowerCase();

            Residence residence = ResidenceManager.getResidenceByOwner(sender.getName());

            if (residence == null) return null;

            Class<? extends IAttributable<?>> attributeClass = AttributeMap.getMap(type);

            return residence.getAttributeWithoutType(attributeClass).allowValues();
        }

        return null;
    }
}
