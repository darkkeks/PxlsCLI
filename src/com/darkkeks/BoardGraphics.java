package com.darkkeks;

import javax.swing.JFrame;
import javax.swing.WindowConstants;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
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
    private int offsetX, offsetY, mousePressedX, mousePressedY, mouseAccumulatedMoveX, mouseAccumulatedMoveY;
    private int zoom;
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
        setupMouseEventListeners();
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
            int x = e.getX(), y = e.getY();
            if (x < 0 || x > WIDTH || y < 0 || y > HEIGHT) return;
            if(e.getWheelRotation() < 0) zoomIn(x, y);
            else zoomOut(x, y);
          
            checkBorders();
            updateTransform();
            redraw();
        });
    }

    private void setupMouseEventListeners() {
        frame.addMouseListener(new MouseListener() {
            @Override public void mouseClicked(MouseEvent e) { }

            @Override
            public void mousePressed(MouseEvent e) {
                mousePressedX = e.getX();
                mousePressedY = e.getY();
                mouseAccumulatedMoveX = 0;
                mouseAccumulatedMoveY = 0;
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                mousePressedX = 0;
                mousePressedY = 0;
                mouseAccumulatedMoveX = 0;
                mouseAccumulatedMoveY = 0;
            }

            @Override public void mouseEntered(MouseEvent e) { }

            @Override public void mouseExited(MouseEvent e) { }
        });

        frame.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int dx = mousePressedX - e.getX(),
                    dy = mousePressedY - e.getY();

                mouseAccumulatedMoveX += dx;
                mouseAccumulatedMoveY += dy;

                if (Math.abs(mouseAccumulatedMoveX) >= zoom) {
                    offsetX += mouseAccumulatedMoveX / zoom;
                    mouseAccumulatedMoveX -= zoom * (mouseAccumulatedMoveX / zoom);
                }

                if (Math.abs(mouseAccumulatedMoveY) >= zoom) {
                    offsetY += mouseAccumulatedMoveY / zoom;
                    mouseAccumulatedMoveY -= zoom * (mouseAccumulatedMoveY / zoom);
                }

                mousePressedX = e.getX();
                mousePressedY = e.getY();

                checkBorders();
                updateTransform();
                redraw();
            }

            @Override public void mouseMoved(MouseEvent e) { }
        });
    }
}