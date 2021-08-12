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

package site.liangbai.realhomehunt.internal.command.subcommand.impl;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import site.liangbai.realhomehunt.api.locale.impl.Locale;
import site.liangbai.realhomehunt.api.residence.Residence;
import site.liangbai.realhomehunt.api.residence.manager.ResidenceManager;
import site.liangbai.realhomehunt.common.particle.EffectGroup;
import site.liangbai.realhomehunt.internal.command.subcommand.ISubCommand;
import site.liangbai.realhomehunt.util.Locales;
import site.liangbai.realhomehunt.util.Zones;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The type Show command.
 *
 * @author Liangbai
 * @since 2021 /08/12 02:46 下午
 */
public final class ShowCommand implements ISubCommand {
    public static final Map<String, EffectGroup> SHOW_CACHES = new ConcurrentHashMap<>();

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) return;

        Locale locale = Locales.require(sender);

        Player player = ((Player) sender);

        if (!ResidenceManager.isOpened(player.getWorld())) {
            sender.sendMessage(locale.asString("command.show.notOpened"));

            return;
        }

        Residence residence = ResidenceManager.getResidenceByOwner(player.getName());

        if (residence == null) {
            sender.sendMessage(locale.asString("command.show.haveNotResidence"));

            return;
        }

        String name = player.getName();

        if (SHOW_CACHES.containsKey(name)) {
            EffectGroup effectGroup = SHOW_CACHES.get(name);

            effectGroup.turnOff();

            SHOW_CACHES.remove(name);

            sender.sendMessage(locale.asString("command.show.successOff"));
        } else {
            SHOW_CACHES.put(name, Zones.startShowWithBlockLocation(player, residence.getLeft(), residence.getRight()));

            player.sendMessage(locale.asString("command.show.successOn"));
        }
    }


}
