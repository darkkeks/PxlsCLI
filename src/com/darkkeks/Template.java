package com.darkkeks;

import java.awt.image.BufferedImage;

public class Template {

    private BoardGraphics graphics;

    private String url;
    private int x, y;
    private float opacity;

    private boolean isLoaded;

    private BufferedImage imageData;
    private byte[] data;

    public Template(BoardGraphics graphics, String url, int x, int y, float opacity) {
        this.url = url;
        this.x = x;
        this.y = y;
        this.opacity = opacity;
        this.graphics = graphics;
        this.isLoaded = false;
    }

    public String getUrl() {
        return url;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return imageData.getWidth();
    }

    public int getHeight() {
        return imageData.getHeight();
    }

    public float getOpacity() {
        return opacity;
    }

    public int get(int x, int y) {
        return data[y * getWidth() + x];
    }

    public byte[] getData() {
        return data;
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    public void setImageData(BufferedImage imageData) {
        this.imageData = imageData;
        findNearestColors();

        this.isLoaded = true;
        graphics.redraw();
    }

    private void findNearestColors() {
        data = new byte[getWidth() * getHeight()];
        int[] rgb_data = imageData.getRaster().getPixels(0, 0, getWidth(), getHeight(), (int[])null);
        int current = 0;
        for(int x = 0; x < getWidth(); ++x) {
            for(int y = 0; y < getHeight(); ++y) {
                if(rgb_data[4 * current + 3] == 255) {
                    byte closest = -1;
                    double distance = Double.POSITIVE_INFINITY;
                    for(byte i = 0; i < Color.count; ++i) {
                        double dist = Math.pow(rgb_data[4 * current] - Color.get(i).r, 2) +
                                      Math.pow(rgb_data[4 * current + 1] - Color.get(i).g, 2) +
                                      Math.pow(rgb_data[4 * current + 2] - Color.get(i).b, 2);
                        if(dist < distance) {
                            distance = dist;
                            closest = i;
                        }
                    }
                    data[current] = closest;
                } else {
                    data[current] = Color.TRANSPARENT.id; // transparent
                }
                current++;
            }
        }
    }
}
