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
