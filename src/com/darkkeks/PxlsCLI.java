package com.darkkeks;

import com.google.gson.*;

import java.net.URISyntaxException;

public class PxlsCLI {

    private static final int WIDTH = 2000;
    private static final int HEIGHT = 2000;

    public static final JsonParser gson = new JsonParser();

    private BoardGraphics graphics;
    private Board board;
    private Template template;

    private void start() {
        board = new Board(WIDTH, HEIGHT);
        graphics = new BoardGraphics(board);
        template = new Template(graphics, "https://i.imgur.com/qGvCUhm.png", 100, 100, 1);
        graphics.setTemplate(template);

        new BoardUpdateUser(board, graphics);
        new User("pxls-token=15873|TcSFRMbdFjmpZEOZEWmCcFnTYnqaMDhPf");

        new BoardLoadThread(this).start();
        new TemplateLoadThread(template).start();
    }

    public Board getBoard() {
        return board;
    }

    public void updateGraphics() {
        graphics.redraw();
    }

    public static void main(String[] args) throws URISyntaxException {
        new PxlsCLI().start();
    }
}
