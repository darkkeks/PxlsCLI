package com.darkkeks;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;

public class BoardLoadThread extends Thread {
    private Board board;
    private BoardGraphics graphics;

    public BoardLoadThread(Board board, BoardGraphics graphics) {
        this.board = board;
        this.graphics = graphics;
    }

    @Override
    public void run() {
        try {
            Date start = new Date();
            System.out.println("Loading board data.");

            URL url = new URL("https://pxls.space/boarddata");

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            InputStream is = url.openStream();

            byte[] row = new byte[16384];

            int n;
            while ((n = is.read(row)) > 0) {
                baos.write(row, 0, n);
            }

            System.out.println("Loaded board");
            System.out.println("Took " + (new Date().getTime() - start.getTime()) + "milis");

            board.setData(baos.toByteArray());
            graphics.updateBoard();
        } catch (IOException e) {
            System.err.printf("Failed to load board data");
            e.printStackTrace();
        }
    }
}
