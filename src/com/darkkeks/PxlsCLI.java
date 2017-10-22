package com.darkkeks;

import com.google.gson.*;

import java.io.*;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class PxlsCLI {

    private static final int WIDTH = 2000;
    private static final int HEIGHT = 2000;

    public static final JsonParser gson = new JsonParser();

    private void start() {
        Board board = new Board(WIDTH, HEIGHT);
        BoardGraphics graphics = new BoardGraphics(board);
        Template template = new Template(graphics, "https://i.imgur.com/YNACc3J.png", 1186, 582, 0.5f);
        graphics.setTemplate(template);

        new BoardUpdateUser(board, graphics);
        new BoardLoadThread(board, graphics).start();
        new TemplateLoadThread(template).start();

        /*Object[] tokens = readTokens();
        System.out.println("Read " + tokens.length + " tokens.");

        BotNet bot = new BotNet(new TaskGenerator(board, template));
        for(Object token : tokens) {
            new User((String)token);
            bot.addUser(new User((String)token));
        }
        bot.start();*/
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
