package com.darkkeks.PxlsCLI.board;

import com.darkkeks.PxlsCLI.PxlsCLI;

import javax.swing.*;
import java.awt.event.*;
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
    private double offsetX, offsetY,
            mousePressedX, mousePressedY,
            templateAccumulatedMoveX, templateAccumulatedMoveY;
    private double zoom;
    private boolean isShiftHeld, isCtrlHeld, isTemplateMove;

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
        frame.setIconImage(new ImageIcon(PxlsCLI.class.getResource("/favicon.png")).getImage());
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
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

    private double getWidthInPixels() {
        return WIDTH / zoom;
    }

    private double getHeightInPixels() {
        return HEIGHT / zoom;
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
                if (key == KeyParser.getKeyCode(PxlsCLI.config.get("controls", "shift"))) isShiftHeld = true;
                if (key == KeyParser.getKeyCode(PxlsCLI.config.get("controls", "ctrl"))) isCtrlHeld = true;
                if (key == KeyParser.getKeyCode(PxlsCLI.config.get("controls", "up"))) offsetY -= getMoveStep();
                if (key == KeyParser.getKeyCode(PxlsCLI.config.get("controls", "left"))) offsetX -= getMoveStep();
                if (key == KeyParser.getKeyCode(PxlsCLI.config.get("controls", "right"))) offsetX += getMoveStep();
                if (key == KeyParser.getKeyCode(PxlsCLI.config.get("controls", "down"))) offsetY += getMoveStep();
                if (key == KeyParser.getKeyCode(PxlsCLI.config.get("controls", "zoomIn"))) zoomInCenter();
                if (key == KeyParser.getKeyCode(PxlsCLI.config.get("controls", "zoomOut"))) zoomOutCenter();

                checkBorders();
                updateTransform();
                redraw();
            }

            @Override
            public void keyReleased(KeyEvent e) {
                int key = e.getKeyCode();

                if (key == KeyParser.getKeyCode(PxlsCLI.config.get("controls", "shift"))) isShiftHeld = false;
                if (key == KeyParser.getKeyCode(PxlsCLI.config.get("controls", "ctrl"))) isCtrlHeld = false;
            }
        });
    }

    private void setupMouseWheelListener() {
        canvas.addMouseWheelListener((e) -> {
            int x = e.getX(), y = e.getY();
            if (x < 0 || x > WIDTH ||
                    y < 0 || y > HEIGHT)
                return;
            if(e.getWheelRotation() < 0)
                zoomIn(x, y);
            else
                zoomOut(x, y);
          
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

                if (isTemplateMove) {
                    PxlsCLI.config.put("template", "offsetX", canvas.getTemplateTransform().getTranslateX());
                    PxlsCLI.config.put("template", "offsetY", canvas.getTemplateTransform().getTranslateY());
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                isTemplateMove = isCtrlHeld;
                mousePressedX = e.getX();
                mousePressedY = e.getY();
            }

            @Override
            public void mouseReleased(MouseEvent e) {}

            @Override public void mouseEntered(MouseEvent e) {}

            @Override public void mouseExited(MouseEvent e) {}
        });

        canvas.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if(!isTemplateMove) {
                    offsetX += (mousePressedX - e.getX()) / zoom;
                    offsetY += (mousePressedY - e.getY()) / zoom;
                } else {
                    templateAccumulatedMoveX += mousePressedX - e.getX();
                    templateAccumulatedMoveY += mousePressedY - e.getY();

                    int x = (int)canvas.getTemplateTransform().getTranslateX();
                    int y = (int)canvas.getTemplateTransform().getTranslateY();

                    if (Math.abs(templateAccumulatedMoveX) >= zoom) {
                        x -= Math.round(templateAccumulatedMoveX / zoom);
                        templateAccumulatedMoveX -= zoom * Math.round(templateAccumulatedMoveX / zoom);
                    }

                    if (Math.abs(templateAccumulatedMoveY) >= zoom) {
                        y -= Math.round(templateAccumulatedMoveY / zoom);
                        templateAccumulatedMoveY -= zoom * Math.round(templateAccumulatedMoveY / zoom);
                    }

                    canvas.getTemplateTransform().setToTranslation(x, y);
                    PxlsCLI.config.put("template", "offsetY", y);
                    PxlsCLI.config.put("template", "offsetX", x);
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
