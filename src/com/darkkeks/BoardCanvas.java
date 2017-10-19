package com.darkkeks;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class BoardCanvas extends JPanel {

    private BufferedImage canvas;

    public BoardCanvas(int width, int height) {
        canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        fillCanvas(Color.BACKGROUND);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(canvas.getWidth(), canvas.getHeight());
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.drawImage(canvas, null, null);
    }

    public void fillCanvas(Color c) {
        int color = c.code;
        for (int x = 0; x < canvas.getWidth(); x++) {
            for (int y = 0; y < canvas.getHeight(); y++) {
                canvas.setRGB(x, y, color);
            }
        }
        repaint();
    }

    public void drawBoard(Board board, int offsetX, int offsetY, int width, int height, int zoom) {
        byte[] data = board.getData();
        for(int x = 0; x < width; ++x) {
            int current = offsetY * board.getWidth() + (x + offsetX);
            for(int y = 0; y < height; ++y) {
                drawPixel(x, y, data[current], zoom);
                current += board.getWidth();
            }
        }
        repaint();
    }

    public void drawPixel(int x, int y, int color, int zoom) {
        for(int i = x * zoom; i < canvas.getWidth() && i < (x + 1) * zoom; ++i) {
            for(int j = y * zoom; j < canvas.getHeight() && j < (y + 1) * zoom; ++j) {
                canvas.setRGB(i, j, Color.get(color).code);
            }
        }
        repaint();
    }

    public void drawTemplate(Template template, int offsetX, int offsetY, int width, int height, int zoom) {
        int x1 = Math.max(template.getX(), offsetX);
        int x2 = Math.min(template.getX() + template.getWidth(), offsetX + width);
        int y1 = Math.max(template.getY(), offsetY);
        int y2 = Math.min(template.getY() + template.getHeight(), offsetY + height);
        for(int x = x1; x < x2; ++x) {
            for(int y = y1; y < y2; ++y) {
                drawPixel(x - offsetX, y - offsetY, 
                    template.get(x - template.getX(), y - template.getY()), zoom);
            }
        }
        repaint();
    }
}
