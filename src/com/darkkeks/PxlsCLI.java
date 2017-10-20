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
        template = new Template(graphics, "https://i.imgur.com/qGvCUhm.png", 1243, 585, 1);
        //graphics.setTemplate(template);

        new BoardUpdateUser(board, graphics);
        new BoardLoadThread(this).start();
        new TemplateLoadThread(template).start();

        new BotNet(new TaskGenerator(board, template))
                .addUser(new User("16192|rmUQRosnInSkcqExYxxVyCxINDsjvkrmx"))
                .addUser(new User("16195|teJJEwxwCyolXykdSufVZROPomPVUwEIm"))
                .start();
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
