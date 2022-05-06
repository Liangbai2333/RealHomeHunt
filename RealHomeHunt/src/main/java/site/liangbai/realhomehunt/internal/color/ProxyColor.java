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

package site.liangbai.realhomehunt.internal.color;

import org.apache.commons.lang3.Validate;
import org.bukkit.Color;

import java.util.Arrays;

/**
 * The enum Proxy color.
 *
 * @author Liangbai
 * @since 2021 /08/12 01:06 下午
 */
public enum ProxyColor {
    WHITE(0xFFFFFF),
    SILVER(0xC0C0C0),
    GRAY(0x808080),
    BLACK(0x000000),
    RED(0xFF0000),
    MAROON(0x800000),
    YELLOW(0xFFFF00),
    OLIVE(0x808000),
    LIME(0x00FF00),
    GREEN(0x008000),
    AQUA(0x00FFFF),
    TEAL(0x008080),
    BLUE(0x0000FF),
    NAVY(0x000080),
    FUCHSIA(0xFF00FF),
    PURPLE(0x800080),
    ORANGE(0xFFA500);

    private static final int BIT_MASK = 0xff;

    private final byte red;
    private final byte green;
    private final byte blue;

    ProxyColor(int rgb) {
        this(rgb >> 16 & BIT_MASK, rgb >> 8 & BIT_MASK, rgb >> 0 & BIT_MASK);
    }

    ProxyColor(int red, int green, int blue) {
        Validate.isTrue(red >= 0 && red <= BIT_MASK, "Red is not between 0-255: ", red);
        Validate.isTrue(green >= 0 && green <= BIT_MASK, "Green is not between 0-255: ", green);
        Validate.isTrue(blue >= 0 && blue <= BIT_MASK, "Blue is not between 0-255: ", blue);
        this.red = (byte) red;
        this.green = (byte) green;
        this.blue = (byte) blue;
    }

    /**
     * Gets the red component
     *
     * @return red component, from 0 to 255
     */
    public int getRed() {
        return BIT_MASK & red;
    }

    /**
     * Gets the green component
     *
     * @return green component, from 0 to 255
     */
    public int getGreen() {
        return BIT_MASK & green;
    }

    /**
     * Gets the blue component
     *
     * @return blue component, from 0 to 255
     */
    public int getBlue() {
        return BIT_MASK & blue;
    }

    /**
     * Gets the color as an RGB integer.
     *
     * @return An integer representation of this color, as 0xRRGGBB
     */
    public int asRGB() {
        return getRed() << 16 | getGreen() << 8 | getBlue() << 0;
    }

    public Color toColor() {
        return Color.fromRGB(getRed(), getGreen(), getBlue());
    }

    public static ProxyColor matches(String colorName, ProxyColor def) {
        return Arrays.stream(values())
                .filter(it -> it.name().equalsIgnoreCase(colorName))
                .findFirst()
                .orElse(def);
    }
}
