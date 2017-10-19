package com.darkkeks;

public class BoardUpdateUser extends MessageReceiver {

    private Board board;
    private BoardGraphics graphics;

    public BoardUpdateUser(Board board, BoardGraphics graphics) {
        this.board = board;
        this.graphics = graphics;
        connect(null);
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
