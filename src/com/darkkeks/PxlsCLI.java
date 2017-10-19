package com.darkkeks;

import com.google.gson.*;

import java.net.URISyntaxException;

public class PxlsCLI {

    private static final int WIDTH = 2000;
    private static final int HEIGHT = 2000;

    public static final JsonParser gson = new JsonParser();

    private BoardGraphics graphics;
    private Board board;

    private void start() throws URISyntaxException {
        board = new Board(WIDTH, HEIGHT);
        graphics = new BoardGraphics(board);

        new BoardUpdateUser(board, graphics);
        new User(board, "pxls-token=15873|TcSFRMbdFjmpZEOZEWmCcFnTYnqaMDhPf");

        new Template(graphics, "https://i.imgur.com/YNACc3J.png", 1186, 582, 1);

        new BoardLoadThread(this).start();
    }

    public Board getBoard() {
        return board;
    }

    public void updateBoard() {
        graphics.redraw();
    }

    public static void main(String[] args) throws URISyntaxException {
        new PxlsCLI().start();
    }
}
