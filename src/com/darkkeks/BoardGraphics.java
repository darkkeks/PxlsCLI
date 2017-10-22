package com.darkkeks;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;

public class BoardGraphics {

    private static final int WIDTH = 500;
    private static final int HEIGHT = 500;
    private static final int MOVE_STEP = 250;
    private static final int ZOOM_STEP = 2;

    private Board board;
    private Template template;
    private BoardCanvas canvas;
    private JFrame frame;

    private AffineTransform transform;
    private int offsetX, offsetY;
    private double zoom;

    public BoardGraphics(Board board) {
        this.board = board;

        canvas = new BoardCanvas(WIDTH, HEIGHT, board.getWidth(), board.getHeight());
        transform = canvas.getTransform();

        this.offsetX = this.offsetY = 0;
        this.zoom = 1;

        frame = new JFrame("PxlsCLI");
        frame.add(canvas);
        frame.setResizable(false);
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setupKeyListener();
        setupMouseWheelListener();
    }

    public void updateBoard() {
        canvas.drawBoard(board);
        redraw();
    }

    public void redraw() {
        if(board.isLoaded())
            canvas.repaint();
    }

    public void setPixel(int x, int y, int color) {
        canvas.setPixel(x, y, color);
    }

    public void setTemplate(Template template) {
        this.template = template;
        canvas.setTemplate(template);
    }

    private void moveUp() {
        offsetY = (int)Math.max(0, offsetY - MOVE_STEP / zoom);
    }

    private void moveDown() {
        offsetY = (int)Math.min(board.getHeight() - (int)Math.ceil((double)HEIGHT / zoom), offsetY + MOVE_STEP / zoom);
    }

    private void moveLeft() {
        offsetX = (int)Math.max(0, offsetX - MOVE_STEP / zoom);
    }

    private void moveRight() {
        offsetX = (int)Math.min(board.getWidth() - (int)Math.ceil((double)WIDTH / zoom), offsetX + MOVE_STEP / zoom);
    }

    private void zoomIn(int zoomX, int zoomY) {
        if(zoom < 128) {
            offsetX += zoomX / (2 * zoom);
            offsetY += zoomY / (2 * zoom);
            zoom *= ZOOM_STEP;
        }
    }

    private void zoomOut(int zoomX, int zoomY) {
        if(zoom > 1 / ZOOM_STEP) {
            zoom /= ZOOM_STEP;
            offsetX -= zoomX / (2 * zoom);
            offsetY -= zoomY / (2 * zoom);
        }
    }

    private void updateTransform() {
        transform.setToScale(zoom, zoom);
        transform.translate(-offsetX, -offsetY);
    }

    private void setupKeyListener() {
        frame.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {
                if(!board.isLoaded() || (template != null && !template.isLoaded()))
                    return;
                if(e.getKeyCode() == KeyEvent.VK_UP) {
                    moveUp();
                } else if(e.getKeyCode() == KeyEvent.VK_DOWN) {
                    moveDown();
                } else if(e.getKeyCode() == KeyEvent.VK_LEFT) {
                    moveLeft();
                } else if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    moveRight();
                }
                updateTransform();
                redraw();
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });
    }

    private void setupMouseWheelListener() {
        frame.addMouseWheelListener((e) -> {
            if(e.getWheelRotation() < 0) {
                zoomIn(e.getX(), e.getY());
            } else {
                zoomOut(e.getX(), e.getY());
            }
            updateTransform();
            redraw();
        });
    }
}
