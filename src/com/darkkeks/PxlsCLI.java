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

        new BoardLoadThread(this).start();
    }

    public void receiveMessage(String message) {
        try {
            JsonObject msg = gson.parse(message).getAsJsonObject();
            if(msg.has("type")) {
                String type = msg.get("type").getAsString();
                if(type.equalsIgnoreCase("pixel")) handlePixel(msg);
                if(type.equalsIgnoreCase("userdata")) handleUsedata(msg);
                if(type.equalsIgnoreCase("can_undo")) handleCanUndo(msg);
                if(type.equalsIgnoreCase("cooldown")) handleCooldown(msg);
                if(type.equalsIgnoreCase("users")) handleUsers(msg);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handlePixel(JsonObject msg) {
        JsonArray pixels = msg.get("pixels").getAsJsonArray();
        for(JsonElement pixel : pixels) {
            int x = pixel.getAsJsonObject().get("x").getAsInt();
            int y = pixel.getAsJsonObject().get("y").getAsInt();
            byte color = pixel.getAsJsonObject().get("color").getAsByte();
            board.set(x, y, color);
            graphics.setPixel(x, y, color);
        }
    }

    private void handleCanUndo(JsonObject msg) {

    }

    private void handleCooldown(JsonObject msg) {

    }

    private void handleUsers(JsonObject msg) {

    }

    private void handleUsedata(JsonObject msg) {

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
