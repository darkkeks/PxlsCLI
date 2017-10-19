package com.darkkeks;

import java.util.HashMap;
import java.util.Map;

public class Color {

    private static final int colorCodes[] = {16777215, 13487565, 8947848, 2236962, 0, 16754641, 15007744, 8388608, 16768458, 15045888, 10512962, 15063296, 9756740, 179713, 54237, 33735, 234, 13594340, 16711935, 8519808};
    public static final int count = colorCodes.length;

    public static final Color BACKGROUND = new Color((byte)-1, colorCodes[1]);
    public static final Color TRANSPARENT = new Color((byte)-2, 0);

    private static Map<Byte, Color> colors = new HashMap<>();

    static {
        for(byte i = 0; i < count; ++i) {
            colors.put(i, new Color(i, colorCodes[i] + 0xFF000000));
        }
        colors.put(BACKGROUND.id, BACKGROUND);
        colors.put(TRANSPARENT.id, TRANSPARENT);
    }

    public static Color get(int i) {
        if(colors.containsKey((byte)i))
            return colors.get((byte)i);
        return BACKGROUND;
    }


    public final int code;
    public final byte id;
    public final int r, g, b;

    private Color(byte id, int code) {
        this.id = id;
        this.code = code;
        this.r = (code & 0xFF0000) >> 16;
        this.g = (code & 0x00FF00) >> 8;
        this.b = (code & 0x0000FF);
    }
}
