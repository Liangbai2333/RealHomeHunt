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

package site.liangbai.realhomehunt.api.residence.attribute

import org.bukkit.command.CommandSender
import taboolib.library.reflex.Reflex.Companion.invokeConstructor
import taboolib.platform.util.asLangTextOrNull

fun IAttributable<out Any>.getName(requester: CommandSender): String {
    val source = this::class.java.simpleName
    return requester.asLangTextOrNull("attribute-${source.lowercase()}-name", requester.name)
        ?: source
}

fun <T> Class<out IAttributable<T>>.create() = invokeConstructor()

fun Class<out IAttributable<*>>.create2() = invokeConstructor()