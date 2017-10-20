package com.darkkeks;

import com.google.gson.*;

import java.io.*;
import java.net.URISyntaxException;
import java.util.ArrayList;

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
        template = new Template(graphics, "https://i.imgur.com/dJixaNt.png", 568, 762, 1);
        graphics.setTemplate(template);

        new BoardUpdateUser(board, graphics);
        new BoardLoadThread(this).start();
        new TemplateLoadThread(template).start();

        Object[] tokens = readTokens();
        System.out.println("Read " + tokens.length + " tokens.");

        BotNet bot = new BotNet(new TaskGenerator(board, template));
        for(Object token : tokens) {
                bot.addUser(new User((String)token));
        }
        bot.start();
    }

    public Board getBoard() {
        return board;
    }

    public void updateGraphics() {
        graphics.redraw();
    }

    private Object[] readTokens() {
        ArrayList<String> res = new ArrayList<>();
        File file = new File("tokens.in");
        try (BufferedReader reader = new BufferedReader(new FileReader(file))){
            String line;
            while((line = reader.readLine()) != null) {
                if(line.matches("\\d{5}\\|[a-zA-Z]{33}"))
                    res.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res.toArray();
    }

    public static void main(String[] args) throws URISyntaxException {
        new PxlsCLI().start();
    }
}
