package com.darkkeks.PxlsCLI.board;

import com.darkkeks.PxlsCLI.network.MessageReceiver;

public class BoardUpdateUser extends MessageReceiver {

    private final Board board;
    private final BoardGraphics graphics;

    public BoardUpdateUser(Board board, BoardGraphics graphics) {
        this.board = board;
        this.graphics = graphics;
        connect("");
    }

    @Override
    protected void handleAlert(String message) {
        System.out.println(message);
    }

    @Override
    protected void handleUsers(int count) {
        super.handleUsers(count);
    }

    @Override
    protected void handlePixel(int x, int y, byte color) {
        board.set(x, y, color);
        graphics.setPixel(x, y, color);
    }
}
