package com.darkkeks;

import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class PxlsCLI {

    private static final int WIDTH = 2000;
    private static final int HEIGHT = 2000;

    public static final JsonParser gson = new JsonParser();

    public static final Settings settings = new Settings("settings.json");

    private void start() {
        Board board = new Board(WIDTH, HEIGHT);
        BoardGraphics graphics = new BoardGraphics(board);

        Template template = new Template(graphics,
                settings.getTemplateURI(),
                settings.getTemplateOffsetX(),
                settings.getTemplateOffsetY(),
                settings.getTemplateOpacity());
        graphics.setTemplate(template);

        new BoardUpdateUser(board, graphics);
        new BoardLoadThread(board, graphics).start();
        new TemplateLoadThread(template).start();

        Object[] tokens = readTokens(settings.getTokensFilePath());
        System.out.println("Read " + tokens.length + " tokens.");

        UserProvider userProvider = new UserProvider();
        for(Object token : tokens) {
            userProvider.add(new User((String)token));
        }

        BotNet bot = new BotNet(new TaskGenerator(board, template), userProvider);
        bot.start();
    }

    private Object[] readTokens(String path) {
        ArrayList<String> res = new ArrayList<>();
        File file = new File(path);
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
