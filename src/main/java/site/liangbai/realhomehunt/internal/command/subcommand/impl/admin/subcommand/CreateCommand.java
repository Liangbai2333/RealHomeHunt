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

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import site.liangbai.realhomehunt.api.cache.SelectCache;
import site.liangbai.realhomehunt.api.event.residence.ResidenceCreateEvent;
import site.liangbai.realhomehunt.api.locale.impl.Locale;
import site.liangbai.realhomehunt.api.locale.manager.LocaleManager;
import site.liangbai.realhomehunt.api.residence.Residence;
import site.liangbai.realhomehunt.api.residence.manager.ResidenceManager;
import site.liangbai.realhomehunt.internal.command.subcommand.ISubCommand;
import site.liangbai.realhomehunt.util.Locations;
import site.liangbai.realhomehunt.util.Messages;

import java.util.Objects;

/**
 * The type Create command.
 *
 * @author Liangbai
 * @since 2021 /08/10 03:00 下午
 */
public final class CreateCommand implements ISubCommand {
    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) return;

        Player player = ((Player) sender);

        Locale locale = LocaleManager.require(player);

        if (args.length < 3) {
            sender.sendMessage(locale.asString("command.admin.create.usage", label));

            return;
        }

        if (!ResidenceManager.isOpened(player.getWorld())) {
            sender.sendMessage(locale.asString("command.admin.create.notOpened"));

            return;
        }

        String target = args[2];

        Residence old = ResidenceManager.getResidenceByOwner(target);

        if (old != null) {
            SelectCache.pop(player);

            sender.sendMessage(locale.asString("command.admin.create.hasOld", target));

            return;
        }

        Location loc1 = SelectCache.require(SelectCache.SelectType.FIRST, player.getName());
        Location loc2 = SelectCache.require(SelectCache.SelectType.SECOND, player.getName());

        if (loc1 == null || loc2 == null) {
            sender.sendMessage(locale.asString("command.admin.create.notSelectZone"));

            return;
        }

        if (!Objects.equals(loc1.getWorld(), loc2.getWorld())) {
            sender.sendMessage(locale.asString("command.admin.create.pointsNotInSameWorld"));

            return;
        }

        if (!ResidenceManager.isOpened(loc1.getWorld())) {
            sender.sendMessage(locale.asString("command.create.notOpened"));

            return;
        }

        if (ResidenceManager.containsResidence(loc1, loc2)) {
            SelectCache.pop(player);

            sender.sendMessage(locale.asString("command.admin.create.containsOther"));

            return;
        }

        Residence residence = new Residence.Builder().owner(target).left(loc1).right(loc2).build();

        if (!new ResidenceCreateEvent.Pre(player, residence).callEvent()) return;

        Location defaultSpawn = Locations.getAverageLocation(loc1.getWorld(), loc1, loc2);

        residence.setSpawn(defaultSpawn);

        if (!new ResidenceCreateEvent.Post(player, residence).callEvent()) return;

        ResidenceManager.register(residence);

        residence.save();

        SelectCache.pop(player);

        sender.sendMessage(locale.asString("command.admin.create.success", target));

        Messages.sendToAll("command.admin.create.sendToAll", sender.getName(), target);
    }
}
