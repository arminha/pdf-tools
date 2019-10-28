/*
 * Copyright (C) 2019  Armin Häberling
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.aha.pdftools.gui;

import com.formdev.flatlaf.util.UIScale;

import java.awt.*;

public class WindowUtil {
    public static void setBoundsScaled(Window w, int x, int y, int width, int height) {
        float factor = UIScale.getUserScaleFactor();
        if (factor > 1.0f) {
            width = Math.round(factor * width);
            height = Math.round(factor * height);
        }
        w.setBounds(x, y, width, height);
    }
}
