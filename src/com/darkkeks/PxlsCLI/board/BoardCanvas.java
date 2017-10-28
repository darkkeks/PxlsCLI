package com.darkkeks.PxlsCLI.board;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;

public class BoardCanvas extends JPanel {

    private final int width;
    private final int height;
    private final BufferedImage canvas;
    private final AffineTransform transform;
    private final AffineTransform templateTransform;
    private Template template;

    public BoardCanvas(int width, int height, int boardWidth, int boardHeight) {
        this.width = width;
        this.height = height;
        transform = new AffineTransform();
        templateTransform = new AffineTransform();
        canvas = new BufferedImage(boardWidth, boardHeight, BufferedImage.TYPE_INT_ARGB);
        fillCanvas(Color.BACKGROUND);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(width, height);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.drawImage(canvas, transform, null);

        if(template != null && template.isLoaded()) {
            RescaleOp filter = new RescaleOp(new float[]{1f, 1f, 1f, template.getOpacity()}, new float[4], null);
            BufferedImage filtered = filter.filter(template.getImageData(), null);

            AffineTransform currentTemplateTransform = new AffineTransform(transform);
            currentTemplateTransform.translate(templateTransform.getTranslateX(), templateTransform.getTranslateY());
            g2.drawImage(filtered, currentTemplateTransform, null);
        }
    }

    public void setTemplate(Template template) {
        this.template = template;
        updateTemplate();
    }

    public void updateTemplate() {
        this.templateTransform.setToTranslation(template.getX(), template.getY());
    }

    public AffineTransform getTemplateTransform() {
        return templateTransform;
    }

    public AffineTransform getTransform() {
        return transform;
    }

    private void fillCanvas(Color c) {
        int color = c.code;
        for (int x = 0; x < canvas.getWidth(); x++) {
            for (int y = 0; y < canvas.getHeight(); y++) {
                canvas.setRGB(x, y, color);
            }
        }
        repaint();
    }

    public void drawBoard(Board board) {
        byte[] data = board.getData();
        for(int x = 0; x < board.getWidth(); ++x) {
            int current = x;
            for(int y = 0; y < board.getHeight(); ++y) {
                canvas.setRGB(x, y, Color.get(data[current]).code);
                current += board.getWidth();
            }
        }
        repaint();
    }

    public void setPixel(int x, int y, int color) {
        canvas.setRGB(x, y, Color.get(color).code);
        repaint();
    }
}
