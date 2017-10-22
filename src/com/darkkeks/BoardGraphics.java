package com.darkkeks;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

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

//    private int mouseClickStartX = 0, mouseClickStartY = 0;

    private boolean isShiftHeld, isCtrlHeld;

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
        setupMouseListeners();
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
            canvas.drawOnePixel(x - offsetX, y - offsetY, color, zoom);
        }
    }

    public void setTemplate(Template template) {
        this.template = template;
    }

    private void moveUp() {
        if(isShiftHeld && isCtrlHeld) return;
        else if(isCtrlHeld) offsetY = Math.max(0, offsetY - 10);
        else if(isShiftHeld) offsetY = Math.max(0, offsetY - 1);
        else offsetY = Math.max(0, offsetY - MOVE_STEP / zoom);
    }

    private void moveDown() {
        if(isShiftHeld && isCtrlHeld) return;
        else if(isCtrlHeld) offsetY = Math.min(board.getHeight() - currentHeight, offsetY + 10);
        else if(isShiftHeld) offsetY = Math.min(board.getHeight() - currentHeight, offsetY + 1);
        else offsetY = Math.min(board.getHeight() - currentHeight, offsetY + MOVE_STEP / zoom);
    }

    private void moveLeft() {
        if(isShiftHeld && isCtrlHeld) return;
        else if(isCtrlHeld) offsetX = Math.max(0, offsetX - 10);
        else if(isShiftHeld) offsetX = Math.max(0, offsetX - 1);
        else offsetX = Math.max(0, offsetX - MOVE_STEP / zoom);
    }

    private void moveRight() {
        if(isShiftHeld && isCtrlHeld) return;
        else if(isCtrlHeld) offsetX = Math.min(board.getWidth() - currentWidth, offsetX + 10);
        else if(isShiftHeld) offsetX = Math.min(board.getWidth() - currentWidth, offsetX + 1);
        else offsetX = Math.min(board.getWidth() - currentWidth, offsetX + MOVE_STEP / zoom);
    }

    private void zoomInCenter() {
        if(zoom < 128) {
            zoom <<= 1;
            offsetX = offsetX + currentWidth / 4;
            offsetY = offsetY + currentHeight / 4;
        }
        updateCurrentDimensions();
    }

    private void zoomOutCenter() {
        if(zoom > 1) {
            offsetX = Math.min(board.getWidth() - currentWidth * 2, Math.max(0, offsetX - currentWidth / 2));
            offsetY = Math.min(board.getHeight() - currentHeight * 2, Math.max(0, offsetY - currentHeight / 2));
            zoom >>= 1;
        }
        updateCurrentDimensions();
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
                if(!board.isLoaded() || (template != null && !template.isLoaded())) return;

                int c = e.getKeyCode();

                if(c == PxlsCLI.settings.getControlsShift()) isShiftHeld = true;
                if(c == PxlsCLI.settings.getControlsCtrl()) isCtrlHeld = true;
                else if(c == PxlsCLI.settings.getControlsUp()) moveUp();
                else if(c == PxlsCLI.settings.getControlsDown()) moveDown();
                else if(c == PxlsCLI.settings.getControlsLeft()) moveLeft();
                else if(c == PxlsCLI.settings.getControlsRight()) moveRight();
                else if(c == PxlsCLI.settings.getControlsZoomIn()) zoomInCenter();
                else if(c == PxlsCLI.settings.getControlsZoomOut()) zoomOutCenter();
                redraw();
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if(!board.isLoaded())
                    return;

                int c = e.getKeyCode();

                if(c == PxlsCLI.settings.getControlsShift()) isShiftHeld = false;
                else if(c == PxlsCLI.settings.getControlsCtrl()) isCtrlHeld = false;
                redraw();
            }
        });
    }

    private void setupMouseListeners() {
        frame.addMouseWheelListener((e) -> {
            if(e.getWheelRotation() < 0) zoomInCenter();
            else zoomOutCenter();

            redraw();
        });
    }
}
