package com.darkkeks.PxlsCLI.board;

public class Board {
    private int width, height;
    private byte[] data;
    private boolean isLoaded;

    public Board(int width, int height) {
        this.width = width;
        this.height = height;
        this.isLoaded = false;
    }

    public void setData(byte[] data) {
        this.data = data;
        this.isLoaded = true;
    }

    public byte[] getData() {
        return data;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean checkRange(int x, int y) {
        return x >= 0 && y >= 0 && x < width && y < height;
    }

    public byte get(int x, int y) {
        if(isLoaded && checkRange(x, y))
            return data[y * width + x];
        return -1;
    }

    public void set(int x, int y, byte color) {
        if(isLoaded && checkRange(x, y))
            data[y * width + x] = color;
    }

    public boolean isLoaded() {
        return isLoaded;
    }
}
