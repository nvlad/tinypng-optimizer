package com.nvlad.tinypng.ui.components;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.JBColor;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class JImage extends JPanel {
    private BufferedImage image;
    private int size;

    public JImage() {
        this.setBorder(BorderFactory.createLineBorder(JBColor.border()));
    }

    public void setImage(VirtualFile file) throws IOException {
        setImage(file == null ? null : file.contentsToByteArray());
    }

    public void setImage(byte[] buffer) throws IOException {
        if (buffer == null) {
            image = null;
            size = 0;
        } else {
            image = ImageIO.read(new ByteArrayInputStream(buffer));
            size = buffer.length;
        }

        repaint();
    }

    public Image getImage() {
        return image;
    }

    public int getImageSize() {
        return size;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null) {
            Rectangle rect = getImageRect();
            g.drawImage(image, rect.x, rect.y, rect.width, rect.height, this);
        }
    }

    private Rectangle getImageRect() {
        Rectangle rect = new Rectangle();
        if (getWidth() > image.getWidth() && getHeight() > image.getHeight()) {
            rect.width = image.getWidth();
            rect.height = image.getHeight();
            rect.x = (getWidth() - rect.width) / 2;
            rect.y = (getHeight() - rect.height) / 2;

            return rect;
        }

        final float widthTransform = ((float) getWidth()) / ((float) image.getWidth());
        final float heightTransform = ((float) getHeight()) / ((float) image.getHeight());
        if (getWidth() < image.getWidth() && getHeight() < image.getHeight()) {
            if (widthTransform < heightTransform) {
                rect.width = (int) Math.floor(image.getWidth() * widthTransform);
                rect.height = (int) Math.floor(image.getHeight() * widthTransform);
            } else {
                rect.width = (int) Math.floor(image.getWidth() * heightTransform);
                rect.height = (int) Math.floor(image.getHeight() * heightTransform);
            }

            rect.x = (getWidth() - rect.width) / 2;
            rect.y = (getHeight() - rect.height) / 2;

            return rect;
        }

        if (getWidth() > image.getWidth()) {
            rect.width = (int) Math.floor(image.getWidth() * heightTransform);
            rect.height = getHeight();
            rect.x = (getWidth() - rect.width) / 2;
            rect.y = 0;
        } else {
            rect.width = getWidth();
            rect.height = (int) Math.floor(image.getHeight() * widthTransform);
            rect.x = 0;
            rect.y = (getHeight() - rect.height) / 2;
        }

        return rect;
    }
}
