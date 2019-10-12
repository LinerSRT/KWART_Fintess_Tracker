package com.kwart.tracking.utils;

import android.app.Activity;
import android.graphics.Color;
import android.util.TypedValue;

public class ColorUtil {
    public static int getAttrColor(Activity activity, int attrColor) {
        TypedValue themeBackgroundColor = new TypedValue();
        int parsedColor;

        if (activity.getTheme().resolveAttribute(attrColor,
                themeBackgroundColor, true)) {
            switch (themeBackgroundColor.type) {
                case TypedValue.TYPE_INT_COLOR_ARGB4:
                    parsedColor = Color.argb(
                            (themeBackgroundColor.data & 0xf000) >> 8,
                            (themeBackgroundColor.data & 0xf00) >> 4,
                            themeBackgroundColor.data & 0xf0,
                            (themeBackgroundColor.data & 0xf) << 4);
                    break;

                case TypedValue.TYPE_INT_COLOR_RGB4:
                    parsedColor = Color.rgb(
                            (themeBackgroundColor.data & 0xf00) >> 4,
                            themeBackgroundColor.data & 0xf0,
                            (themeBackgroundColor.data & 0xf) << 4);
                    break;

                case TypedValue.TYPE_INT_COLOR_ARGB8:
                    parsedColor = themeBackgroundColor.data;
                    break;

                case TypedValue.TYPE_INT_COLOR_RGB8:
                    parsedColor = Color.rgb(
                            (themeBackgroundColor.data & 0xff0000) >> 16,
                            (themeBackgroundColor.data & 0xff00) >> 8,
                            themeBackgroundColor.data & 0xff);
                    break;

                default:
                    throw new RuntimeException("ClassName: couldn't parse theme " +
                            "background color attribute " + themeBackgroundColor.toString());
            }
        } else {
            throw new RuntimeException("ClassName: couldn't find background color in " +
                    "theme");
        }
        return parsedColor;
    }
}
