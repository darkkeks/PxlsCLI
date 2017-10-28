package com.darkkeks.PxlsCLI.board;

import java.awt.image.BufferedImage;

public class Template {

    private final BoardGraphics graphics;

    private final String url;
    private int x;
    private int y;
    private final float opacity;
    private final boolean replacePixels;

    private BufferedImage imageData;

    private boolean isLoaded;
    private byte[] data;

    public Template(BoardGraphics graphics, String url, int x, int y, float opacity, boolean replacePixels) {
        this.url = url;
        this.x = x;
        this.y = y;
        this.opacity = opacity;
        this.graphics = graphics;
        this.replacePixels = replacePixels;
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

    public boolean getReplacePixels() {
        return replacePixels;
    }

    public BufferedImage getImageData() {
        return imageData;
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
        boolean hasAlpha = imageData.getAlphaRaster() != null;

        int pixelLen = (hasAlpha ? 4 : 3);
        int current = 0;
        for(int x = 0; x < getWidth(); ++x) {
            for(int y = 0; y < getHeight(); ++y) {
                if(!hasAlpha || rgb_data[pixelLen * current + 3] == 255) {
                    byte closest = -1;
                    double distance = Double.POSITIVE_INFINITY;
                    for(byte i = 0; i < Color.count; ++i) {
                        double dist = Math.pow(rgb_data[pixelLen * current] - Color.get(i).r, 2) +
                                      Math.pow(rgb_data[pixelLen * current + 1] - Color.get(i).g, 2) +
                                      Math.pow(rgb_data[pixelLen * current + 2] - Color.get(i).b, 2);
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

    public boolean checkRange(int x, int y) {
        return x >= this.x && y >= this.y &&
                x < this.x + this.getWidth() &&
                y < this.y + this.getHeight();
    }

    public byte get(int x, int y) {
        if(isLoaded && checkRange(x, y))
            return data[(y - this.y) * getWidth() + (x - this.x)];
        return -2;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
