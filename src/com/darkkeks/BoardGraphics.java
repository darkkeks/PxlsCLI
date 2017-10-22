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
    private boolean isShiftHeld, isCtrlHeld;

    public BoardGraphics(Board board) {
        this.board = board;

        canvas = new BoardCanvas(WIDTH, HEIGHT, board.getWidth(), board.getHeight());
        transform = canvas.getTransform();

        this.offsetX = this.offsetY = 0;
        this.isShiftHeld = this.isCtrlHeld = false;
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

    private double getMoveStep() {
        if(isShiftHeld) return 1;
        if(isCtrlHeld) return 10;
        return MOVE_STEP / zoom;
    }

    private int getWidthInPixels() {
        return (int)Math.ceil((double)WIDTH / zoom);
    }

    private int getHeightInPixels() {
        return (int)Math.ceil((double)HEIGHT / zoom);
    }

    private void zoomInCenter() {
        zoomIn(WIDTH / 2, HEIGHT / 2);
    }

    private void zoomOutCenter() {
        zoomOut(WIDTH / 2, HEIGHT / 2);
    }

    private void zoomIn(int zoomX, int zoomY) {
        if(zoom < 128) {
            offsetX += zoomX / (2 * zoom);
            offsetY += zoomY / (2 * zoom);
            zoom *= ZOOM_STEP;
        }
    }

    private void zoomOut(int zoomX, int zoomY) {
        if(zoom > 1) {
            zoom /= ZOOM_STEP;
            offsetX -= zoomX / (2 * zoom);
            offsetY -= zoomY / (2 * zoom);
        }
    }

    private void checkBorders() {
        offsetX = Math.max(offsetX, -getWidthInPixels() / 2);
        offsetX = Math.min(offsetX, board.getWidth() - getWidthInPixels() / 2);
        offsetY = Math.max(offsetY, -getHeightInPixels() / 2);
        offsetY = Math.min(offsetY, board.getHeight() - getHeightInPixels() / 2);
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

                int key = e.getKeyCode();
                if (key == PxlsCLI.settings.getControlsShift()) isShiftHeld = true;
                if (key == PxlsCLI.settings.getControlsCtrl()) isCtrlHeld = true;
                if (key == PxlsCLI.settings.getControlsUp()) offsetY -= getMoveStep();
                if (key == PxlsCLI.settings.getControlsDown()) offsetY += getMoveStep();
                if (key == PxlsCLI.settings.getControlsLeft()) offsetX -= getMoveStep();
                if (key == PxlsCLI.settings.getControlsRight()) offsetX += getMoveStep();
                if (key == PxlsCLI.settings.getControlsZoomIn()) zoomInCenter();
                if (key == PxlsCLI.settings.getControlsZoomOut()) zoomOutCenter();

                checkBorders();
                updateTransform();
                redraw();
            }

            @Override
            public void keyReleased(KeyEvent e) {
                int key = e.getKeyCode();

                if (key == PxlsCLI.settings.getControlsShift()) isShiftHeld = false;
                if (key == PxlsCLI.settings.getControlsCtrl()) isCtrlHeld = false;
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
            
            checkBorders();
            updateTransform();
            redraw();
        });
    }
}
