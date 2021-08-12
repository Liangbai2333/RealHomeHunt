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

package site.liangbai.realhomehunt.common.expand;

import java.util.Arrays;
import java.util.List;

public enum Expand {
    UP(Direction.Y),
    DOWN(Direction.Y),
    NORTH(Direction.X, Direction.Z),
    SOUTH(Direction.X, Direction.Z),
    EAST(Direction.X, Direction.Z),
    WEST(Direction.X, Direction.Z),
    ALL(Direction.X, Direction.Y, Direction.Z);

    private final List<Direction> accepted;

    Expand(Direction... accepted){
        this.accepted = Arrays.asList(accepted);
    }

    public boolean isAccepted(Direction direction) {
        return accepted.contains(direction);
    }

    public static Expand matches(String expand) {
        return Arrays.stream(values())
                .filter(it -> it.name().equalsIgnoreCase(expand))
                .findFirst()
                .orElse(null);
    }

    enum Direction {
        X,
        Y,
        Z
    }


}
