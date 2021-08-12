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
import site.liangbai.realhomehunt.api.event.residence.ResidenceExpandEvent;
import site.liangbai.realhomehunt.api.locale.impl.Locale;
import site.liangbai.realhomehunt.api.residence.Residence;
import site.liangbai.realhomehunt.api.residence.manager.ResidenceManager;
import site.liangbai.realhomehunt.common.expand.Expand;
import site.liangbai.realhomehunt.internal.command.subcommand.ISubCommand;
import site.liangbai.realhomehunt.util.Locales;
import site.liangbai.realhomehunt.util.Pair;

/**
 * The type Expand command.
 *
 * @author Liangbai
 * @since 2021 /08/11 09:35 下午
 */
public class ExpandCommand implements ISubCommand {

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        //       0     1       2        3        4        total: 5
        // /rh admin expand <target> <expand>  <size>

        Locale locale = Locales.require(sender);

        if (args.length < 5) {
            sender.sendMessage(locale.asString("command.admin.expand.usage", label));

            return;
        }

        String target = args[2];

        Residence residence = ResidenceManager.getResidenceByOwner(target);

        if (residence == null) {
            sender.sendMessage(locale.asString("command.admin.expand.haveNotResidence", target));

            return;
        }

        Expand expand = Expand.matches(args[3]);

        if (expand == null) {
            sender.sendMessage(locale.asString("command.admin.expand.unknownExpand", args[3]));

            return;
        }

        double size;

        try {
            size = Double.parseDouble(args[4]);
        } catch (Throwable e) {
            sender.sendMessage(locale.asString("command.admin.expand.mustBeNumber", args[4]));

            return;
        }

        ResidenceExpandEvent.Pre preEvent = new ResidenceExpandEvent.Pre(sender, residence, expand, size);

        if (!preEvent.callEvent()) return;

        Pair<Location, Location> pair = expand(residence, preEvent.getExpand(), preEvent.getSize());

        ResidenceExpandEvent.Post postEvent = new ResidenceExpandEvent.Post(sender, residence, pair.getKey(), pair.getValue(), preEvent.getExpand(), preEvent.getSize());

        if (!postEvent.callEvent()) return;

        residence.setLeft(postEvent.getChangedLeft());
        residence.setRight(postEvent.getChangedRight());

        sender.sendMessage(locale.asString("command.admin.expand.success", target, args[3], size));
    }

    private static Pair<Location, Location> expand(Residence residence, Expand expand, double size) {
        Location left = residence.getLeft().clone();
        Location right = residence.getRight().clone();

        switch (expand) {
            case UP:
                if (left.getY() > right.getY()) {
                    left.add(0, size, 0);
                } else {
                    right.add(0, size, 0);
                }
                break;
            case DOWN:
                if (left.getY() < right.getY()) {
                    left.add(0, -size, 0);
                } else {
                    right.add(0, -size, 0);
                }
                break;
            case NORTH:
                if (left.getZ() < right.getZ()) {
                    left.add(0, 0, -size);
                } else {
                    right.add(0, 0, -size);
                }
                break;
            case SOUTH:
                if (left.getZ() > right.getZ()) {
                    left.add(0, 0, size);
                } else {
                    right.add(0, 0, size);
                }
                break;
            case WEST:
                if (left.getX() < right.getX()) {
                    left.add(-size, 0, 0);
                } else {
                    right.add(-size, 0, 0);
                }
                break;
            case EAST:
                if (left.getX() > right.getX()) {
                    left.add(size, 0, 0);
                } else {
                    right.add(size, 0, 0);
                }
                break;
            case ALL:
                size = size / 2;

                if (left.getX() > right.getX()) {
                    left.add(size, 0, 0);
                    right.add(-size, 0, 0);
                } else {
                    left.add(-size, 0, 0);
                    right.add(size, 0, 0);
                }

                if (left.getY() > right.getY()) {
                    left.add(0, size, 0);
                    right.add(0, -size, 0);
                } else {
                    left.add(0, -size, 0);
                    right.add(0, size, 0);
                }

                if (left.getZ() > right.getZ()) {
                    left.add(0, 0, size);
                    right.add(0, 0, -size);
                } else {
                    left.add(0, 0, -size);
                    right.add(0, 0, size);
                }
        }

        return new Pair<>(left, right);
    }
}
