package com.darkkeks;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class TemplateLoadThread extends Thread {

    private Template template;

    public TemplateLoadThread(Template template) {
        this.template = template;
    }

    @Override
    public void run() {
        try {
            System.out.println("Loading template.");

            URL url = new URL(template.getUrl());
            BufferedImage image = ImageIO.read(url);

            System.out.println("Template loaded.");
            template.setImageData(image);
        } catch (IOException e) {
            System.out.println("Couldn't load template");
            e.printStackTrace();
        }
    }
}
