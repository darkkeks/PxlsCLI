package com.darkkeks.PxlsCLI.network;

import com.darkkeks.PxlsCLI.board.Board;
import com.darkkeks.PxlsCLI.board.BoardGraphics;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;

public class BoardLoadThread extends Thread {
    private final Board board;
    private final BoardGraphics graphics;

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

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            InputStream is = url.openStream();

            byte[] row = new byte[16384];

            int n;
            while ((n = is.read(row)) > 0) {
                stream.write(row, 0, n);
            }

            System.out.println("Loaded board");
            System.out.println("Took " + (new Date().getTime() - start.getTime()) + "ms");

            board.setData(stream.toByteArray());
            graphics.updateBoard();
        } catch (IOException e) {
            System.err.printf("Failed to load board data");
            e.printStackTrace();
        }
    }
}
