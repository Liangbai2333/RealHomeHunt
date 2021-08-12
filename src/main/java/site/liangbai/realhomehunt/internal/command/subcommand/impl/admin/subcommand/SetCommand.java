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

package site.liangbai.realhomehunt.internal.command.subcommand.impl.admin.subcommand;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import site.liangbai.realhomehunt.api.event.residence.ResidenceSetAttributeEvent;
import site.liangbai.realhomehunt.api.locale.impl.Locale;
import site.liangbai.realhomehunt.api.locale.manager.LocaleManager;
import site.liangbai.realhomehunt.api.residence.Residence;
import site.liangbai.realhomehunt.api.residence.attribute.IAttributable;
import site.liangbai.realhomehunt.api.residence.attribute.map.AttributeMap;
import site.liangbai.realhomehunt.api.residence.manager.ResidenceManager;
import site.liangbai.realhomehunt.internal.command.subcommand.ISubCommand;

/**
 * The type Set command.
 *
 * @author Liangbai
 * @since 2021 /08/11 05:48 下午
 */
public final class SetCommand implements ISubCommand {
    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) return;

        Player player = ((Player) sender);

        Locale locale = LocaleManager.require(player);

        if (args.length < 5) {
            sender.sendMessage(locale.asString("command.admin.set.usage", label));

            return;
        }

        String target = args[2];

        Residence residence = ResidenceManager.getResidenceByOwner(target);

        if (residence == null) {
            sender.sendMessage(locale.asString("command.admin.set.haveNotResidence", target));

            return;
        }

        String attributeType = args[3].toLowerCase();

        Class<? extends IAttributable<?>> attributeClass = AttributeMap.getMap(attributeType);

        if (attributeClass == null) {
            sender.sendMessage(locale.asString("command.admin.set.unknownAttribute", attributeType));

            return;
        }

        IAttributable<?> attribute = residence.getAttributeWithoutType(attributeClass);

        String value = args[4];

        if (attribute.allow(value)) {
            ResidenceSetAttributeEvent event = new ResidenceSetAttributeEvent(player, residence, attribute, value);
            if (!event.callEvent()) return;

            String attributeName = attribute.getName();

            attribute.force(event.getValue());

            sender.sendMessage(locale.asString("command.admin.set.success", target, attributeName, value));

            residence.save();
        } else {
            sender.sendMessage(locale.asString("command.admin.set.notAllow", attribute.allowValues()));
        }
    }
}
