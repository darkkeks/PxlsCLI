package com.darkkeks;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;

public class BoardLoadThread extends Thread {
    private PxlsCLI pxlsCLI;

    public BoardLoadThread(PxlsCLI pxlsCLI) {
        this.pxlsCLI = pxlsCLI;
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

            pxlsCLI.getBoard().setData(baos.toByteArray());
            pxlsCLI.updateGraphics();
        } catch (IOException e) {
            System.err.printf("Failed to load board data");
            e.printStackTrace();
        }
    }
}
