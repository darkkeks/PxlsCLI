package com.darkkeks;

public class Color {

    private static final int colorCodes[] = {16777215, 13487565, 8947848, 2236962, 0, 16754641, 15007744, 8388608, 16768458, 15045888, 10512962, 15063296, 9756740, 179713, 54237, 33735, 234, 13594340, 16711935, 8519808};
    public static final int count = colorCodes.length;

    private static final Color[] colors = new Color[colorCodes.length];
    static {
        for(int i = 0; i < count; ++i) {
            colors[i] = new Color(colorCodes[i] + 0xFF000000);
        }
    }

    public static final Color BACKGROUND = Color.get(1);

    public final int code;
    public final int r, g, b;

    private Color(int code) {
        this.code = code;
        this.r = (code & 0xFF0000) >> 16;
        this.g = (code & 0x00FF00) >> 8;
        this.b = (code & 0x0000FF);
    }

    public static Color get(int i) {
        if(i >= 0 && i < count)
            return colors[i];
        return BACKGROUND;
    }
}
