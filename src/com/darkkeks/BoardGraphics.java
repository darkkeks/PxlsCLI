package com.darkkeks;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class BoardGraphics {

    private static final int WIDTH = 500;
    private static final int HEIGHT = 500;
    private static final int MOVE_STEP = 250;

    private Board board;
    private Template template;
    private BoardCanvas canvas;
    private JFrame frame;

    private int offsetX, offsetY;
    private int zoom;

    private boolean isShiftHeld;
    private boolean drawTemplate;

    public BoardGraphics(Board board) {
        this.board = board;
        this.offsetX = this.offsetY = 0;
        this.zoom = 0;
        this.isShiftHeld = drawTemplate = false;

        frame = new JFrame("PxlsCLI");

        canvas = new BoardCanvas(WIDTH, HEIGHT);

        frame.add(canvas);
        frame.setResizable(false);
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setupKeyListener();
        setupMouseWheelListener();
    }

    public void redraw() {
        if(board.isLoaded())
            canvas.drawBoard(board, offsetX, offsetY, WIDTH, HEIGHT, zoom);
        if(drawTemplate && template != null)
            canvas.drawTemplate(template, offsetX, offsetY, zoom);
    }

    public void setPixel(int x, int y, int color) {
        if(x >= offsetX && y >= offsetY &&
                x < offsetX + WIDTH / (1 << zoom) &&
                y < offsetY + HEIGHT / (1 << zoom)) {
            canvas.drawPixel(x - offsetX, y - offsetY, color, zoom);
        }
    }

    public void setTemplate(Template template) {
        this.template = template;
        redraw();
    }

    private void moveUp() {
        if(isShiftHeld)
            offsetY = Math.max(0, offsetY - 1);
        else
            offsetY = Math.max(0, offsetY - MOVE_STEP / (1 << zoom));
    }

    private void moveDown() {
        if(isShiftHeld)
            offsetY = Math.min(board.getHeight() - HEIGHT / (1 << zoom), offsetY + 1);
        else
            offsetY = Math.min(board.getHeight() - HEIGHT / (1 << zoom), offsetY + MOVE_STEP / (1 << zoom));
    }

    private void moveLeft() {
        if(isShiftHeld)
            offsetX = Math.max(0, offsetX - 1);
        else
            offsetX = Math.max(0, offsetX - MOVE_STEP / (1 << zoom));
    }

    private void moveRight() {
        if(isShiftHeld)
            offsetX = Math.min(board.getWidth() - WIDTH / (1 << zoom), offsetX + 1);
        else
            offsetX = Math.min(board.getWidth() - WIDTH / (1 << zoom), offsetX + MOVE_STEP / (1 << zoom));
    }

    private void zoomIn() {
        if(zoom < 7) zoom++;
    }

    private void zoomOut() {
        if(zoom > 0) zoom--;
        offsetX = Math.min(board.getWidth() - WIDTH / (1 << zoom), offsetX);
        offsetY = Math.min(board.getHeight() - HEIGHT / (1 << zoom), offsetY);
    }

    private void setupKeyListener() {
        frame.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {
                if(!board.isLoaded())
                    return;
                if(e.getKeyCode() == KeyEvent.VK_SHIFT){
                    isShiftHeld = true;
                } else if(e.getKeyCode() == KeyEvent.VK_UP) {
                    moveUp();
                } else if(e.getKeyCode() == KeyEvent.VK_DOWN) {
                    moveDown();
                } else if(e.getKeyCode() == KeyEvent.VK_LEFT) {
                    moveLeft();
                } else if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    moveRight();
                }
                redraw();
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if(!board.isLoaded())
                    return;
                if(e.getKeyCode() == KeyEvent.VK_SHIFT) {
                    isShiftHeld = false;
                }
                redraw();
            }
        });
    }

    private void setupMouseWheelListener() {
        frame.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if(e.getWheelRotation() < 0) {
                    zoomIn();
                } else {
                    zoomOut();
                }
                redraw();
            }
        });
    }
}
