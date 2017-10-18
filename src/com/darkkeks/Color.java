package com.darkkeks;

public class Color {

    private static final int colorCodes[] = {16777215, 13487565, 8947848, 2236962, 0, 16754641, 15007744, 8388608, 16768458, 15045888, 10512962, 15063296, 9756740, 179713, 54237, 33735, 234, 13594340, 16711935, 8519808};

    private static final Color[] colors = new Color[colorCodes.length];
    static {
        for(int i = 0; i < colorCodes.length; ++i) {
            colors[i] = new Color(colorCodes[i] + 0xFF000000);
        }
    }

    public static final Color BACKGROUND = Color.get(1);

    private int code;

    private Color(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static Color get(int i) {
        if(i >= 0 && i < colors.length)
            return colors[i];
        return BACKGROUND;
    }
}
