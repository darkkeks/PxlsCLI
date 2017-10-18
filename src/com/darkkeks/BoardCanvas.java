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
        int color = c.getCode();
        for (int x = 0; x < canvas.getWidth(); x++) {
            for (int y = 0; y < canvas.getHeight(); y++) {
                canvas.setRGB(x, y, color);
            }
        }
        repaint();
    }

    public void drawBoard(Board board, int offsetX, int offsetY, int width, int height, int zoom) {
        for(int x = 0; x < width; ++x) {
            for(int y = 0; y < height; ++y) {
                canvas.setRGB(x, y, Color.get(
                        board.get(
                        x / (1 << zoom) + offsetX,
                        y / (1 << zoom) + offsetY)
                    ).getCode());
            }
        }
        repaint();
    }

    public void drawPixel(int x, int y, int color, int zoom) {
        for(int i = x * (1 << zoom); i < (x + 1) * (1 << zoom); ++i) {
            for(int j = y * (1 << zoom); j < (y + 1) * (1 << zoom); ++j) {
                canvas.setRGB(i, j, Color.get(color).getCode());
            }
        }
        repaint();
    }
}
