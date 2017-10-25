package com.darkkeks.PxlsCLI.board;

import com.darkkeks.PxlsCLI.PxlsCLI;

import javax.swing.JFrame;
import javax.swing.WindowConstants;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;

public class BoardGraphics {

    private static final int WIDTH = 500;
    private static final int HEIGHT = 500;
    private static final int MOVE_STEP = 250;
    private static final double ZOOM_STEP = Math.sqrt(2);

    private Board board;
    private Template template;
    private BoardCanvas canvas;
    private JFrame frame;
    private BoardClickListener boardClickListener;

    private AffineTransform transform;
    private int offsetX, offsetY,
            mousePressedX, mousePressedY,
            mouseAccumulatedMoveX, mouseAccumulatedMoveY;
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
        setupMouseListener();
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

    public void setBoardClickListener(BoardClickListener listener) {
        this.boardClickListener = listener;
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
            zoom *= ZOOM_STEP;
            offsetX += (ZOOM_STEP - 1) * zoomX / zoom;
            offsetY += (ZOOM_STEP - 1) * zoomY / zoom;
        }
    }

    private void zoomOut(int zoomX, int zoomY) {
        if(zoom - 1 > 1e-9) { // floating-point comparison epsilon
            offsetX -= (ZOOM_STEP - 1) * zoomX / zoom;
            offsetY -= (ZOOM_STEP - 1) * zoomY / zoom;
            zoom /= ZOOM_STEP;
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
        canvas.addMouseWheelListener((e) -> {
            int x = e.getX(), y = e.getY();
            if (x < 0 || x > WIDTH || y < 0 || y > HEIGHT) return;
            if(e.getWheelRotation() < 0) zoomIn(x, y);
            else zoomOut(x, y);
          
            checkBorders();
            updateTransform();
            redraw();
        });
    }

    private void setupMouseListener() {
        canvas.addMouseListener(new MouseListener() {
            @Override public void mouseClicked(MouseEvent e) {
                try {
                    Point2D boardPoint = transform.inverseTransform(new Point2D.Double(e.getX(), e.getY()), null);

                    if(boardClickListener != null && board.isLoaded()) {
                        int x = (int)boardPoint.getX();
                        int y = (int)boardPoint.getY();
                        if(board.checkRange(x, y)) {
                            boardClickListener.onClick(x, y);
                        }
                    }
                } catch (NoninvertibleTransformException ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                mousePressedX = e.getX();
                mousePressedY = e.getY();
                mouseAccumulatedMoveX = 0;
                mouseAccumulatedMoveY = 0;
            }

            @Override
            public void mouseReleased(MouseEvent e) {}

            @Override public void mouseEntered(MouseEvent e) {}

            @Override public void mouseExited(MouseEvent e) {}
        });

        canvas.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                mouseAccumulatedMoveX += mousePressedX - e.getX();
                mouseAccumulatedMoveY += mousePressedY - e.getY();

                if (Math.abs(mouseAccumulatedMoveX) >= zoom) {
                    offsetX += Math.round(mouseAccumulatedMoveX / zoom);
                    mouseAccumulatedMoveX -= zoom * Math.round(mouseAccumulatedMoveX / zoom);
                }

                if (Math.abs(mouseAccumulatedMoveY) >= zoom) {
                    offsetY += Math.round(mouseAccumulatedMoveY / zoom);
                    mouseAccumulatedMoveY -= zoom * Math.round(mouseAccumulatedMoveY / zoom);
                }

                mousePressedX = e.getX();
                mousePressedY = e.getY();

                checkBorders();
                updateTransform();
                redraw();
            }

            @Override public void mouseMoved(MouseEvent e) {}
        });
    }
}