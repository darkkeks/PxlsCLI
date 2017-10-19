package com.darkkeks;

import java.awt.image.BufferedImage;

public class Template {

    private BoardGraphics graphics;

    private String url;
    private int x, y;
    private float opacity;

    private BufferedImage imageData;

    public Template(BoardGraphics graphics, String url, int x, int y, float opacity) {
        this.url = url;
        this.x = x;
        this.y = y;
        this.opacity = opacity;
        this.graphics = graphics;

        new TemplateLoadThread(this).start();
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

    public void setImageData(BufferedImage imageData) {
        this.imageData = imageData;
        graphics.setTemplate(this);
    }

    public BufferedImage getImageData() {
        return imageData;
    }
}
