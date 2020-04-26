/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.slideview.screensaver;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author Harald
 */
class ImageComponent extends JComponent {

    private static final long serialVersionUID = 1L;

    private Image image;

    private double totalWidth = 1920; // default

    private double totalHeight = 1080; // default

    public ImageComponent() {
    }

    public void setTotalSpace(double totalWidth, double totalHeight) {
        this.totalWidth = totalWidth;
        this.totalHeight = totalHeight;
    }

    public void setFoto(String filename) {
        try {
            if (image != null) {
                image.flush();
                image = null;
            }
            image = ImageIO.read(new File(filename));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        if (image == null) {
            return;
        }
        double imageWidth = image.getWidth(this);
        double imageHeight = image.getHeight(this);

        double factor;
        if ((imageHeight / imageWidth) > (totalHeight / totalWidth)) {
            factor = totalHeight / imageHeight;
        } else {
            factor = totalWidth / imageWidth;
        }
        double actualWidth = imageWidth * factor;
        double actualHeight = imageHeight * factor;

        g.drawImage(image, //
                (int) ((totalWidth - actualWidth) / 2), //
                (int) ((totalHeight - actualHeight) / 2), //
                (int) (actualWidth + ((totalWidth - actualWidth) / 2)), //
                (int) (actualHeight + ((totalHeight - actualHeight) / 2)), //
                0, 0, (int) imageWidth, (int) imageHeight, Color.BLACK, this);

    }

}
