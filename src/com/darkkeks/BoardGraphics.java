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
    private int currentWidth, currentHeight;
    private int zoom;

    private boolean isShiftHeld;

    public BoardGraphics(Board board) {
        this.board = board;
        this.offsetX = this.offsetY = 0;
        this.currentWidth = WIDTH;
        this.currentHeight = HEIGHT;
        this.zoom = 1;
        this.isShiftHeld = false;

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
            canvas.drawBoard(board, offsetX, offsetY, currentWidth, currentHeight, zoom);
        if(template != null && template.isLoaded())
            canvas.drawTemplate(template, offsetX, offsetY, currentWidth, currentHeight, zoom);
    }

    public void setPixel(int x, int y, int color) {
        if(x >= offsetX && y >= offsetY &&
                x < offsetX + currentWidth &&
                y < offsetY + currentHeight) {
            canvas.drawPixel(x - offsetX, y - offsetY, color, zoom);
        }
    }

    public void setTemplate(Template template) {
        this.template = template;
    }

    private void moveUp() {
        if(isShiftHeld)
            offsetY = Math.max(0, offsetY - 1);
        else
            offsetY = Math.max(0, offsetY - MOVE_STEP / zoom);
    }

    private void moveDown() {
        if(isShiftHeld)
            offsetY = Math.min(board.getHeight() - currentHeight, offsetY + 1);
        else
            offsetY = Math.min(board.getHeight() - currentHeight, offsetY + MOVE_STEP / zoom);
    }

    private void moveLeft() {
        if(isShiftHeld)
            offsetX = Math.max(0, offsetX - 1);
        else
            offsetX = Math.max(0, offsetX - MOVE_STEP / zoom);
    }

    private void moveRight() {
        if(isShiftHeld)
            offsetX = Math.min(board.getWidth() - currentWidth, offsetX + 1);
        else
            offsetX = Math.min(board.getWidth() - currentWidth, offsetX + MOVE_STEP / zoom);
    }

    private void zoomIn() {
        if(zoom < 128) zoom <<= 1;
        updateCurrentDimensions();
    }

    private void zoomOut() {
        if(zoom > 1) zoom >>= 1;
        updateCurrentDimensions();

        offsetX = Math.min(board.getWidth() - currentWidth, offsetX);
        offsetY = Math.min(board.getHeight() - currentHeight, offsetY);
    }

    private void updateCurrentDimensions() {
        currentWidth = (int)Math.ceil((double)WIDTH / zoom);
        currentHeight = (int)Math.ceil((double)HEIGHT / zoom);
    }

    private void setupKeyListener() {
        frame.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {
                if(!board.isLoaded() && !template.isLoaded())
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
        frame.addMouseWheelListener((e) -> {
            if(e.getWheelRotation() < 0) {
                zoomIn();
            } else {
                zoomOut();
            }
            redraw();
        });
    }
}
