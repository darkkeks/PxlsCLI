package com.darkkeks;

import javax.imageio.ImageIO;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class TemplateLoadThread extends Thread {

    private final Template template;

    public TemplateLoadThread(Template template) {
        this.template = template;
    }

    @Override
    public void run() {
        try {
            System.out.println("Loading template.");

            URL url = new URL(template.getUrl());
            Image image = ImageIO.read(url);
            BufferedImage bufferedImage = new BufferedImage(
                    image.getWidth(null),
                    image.getWidth(null),
                    BufferedImage.TYPE_4BYTE_ABGR);
            bufferedImage.getGraphics().drawImage(image, 0, 0, null);
            bufferedImage.getGraphics().dispose();

            System.out.println("Template loaded.");
            template.setImageData(bufferedImage);
        } catch (IOException e) {
            System.out.println("Couldn't load template");
            e.printStackTrace();
        }
    }
}
