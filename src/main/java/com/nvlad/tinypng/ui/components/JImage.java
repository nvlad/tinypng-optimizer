package com.nvlad.tinypng.ui.components;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.JBColor;
import com.intellij.util.ui.UIUtil;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class JImage extends JPanel {
    private BufferedImage image;
    private int size;
    private Rectangle myRect;
    private BufferedImage bgImage;

    public JImage() {
        this.setBorder(BorderFactory.createLineBorder(JBColor.border()));
        this.addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (image != null) {
                    final Rectangle newRect = getImageRect();
                    if (myRect != newRect) {
                        myRect = getImageRect();
                        bgImage = prepareChessboardBackground();
                    }
                }
            }

            @Override
            public void componentMoved(ComponentEvent e) {

            }

            @Override
            public void componentShown(ComponentEvent e) {

            }

            @Override
            public void componentHidden(ComponentEvent e) {

            }
        });
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

        if (image != null) {
            myRect = getImageRect();
            bgImage = prepareChessboardBackground();
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
            g.drawImage(bgImage, myRect.x, myRect.y, myRect.width, myRect.height, this);
            g.drawImage(image, myRect.x, myRect.y, myRect.width, myRect.height, this);
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

    private BufferedImage prepareChessboardBackground() {
        final BufferedImage image = UIUtil.createImage(myRect.width, myRect.height, BufferedImage.TYPE_INT_RGB);
        final Graphics graphics = image.getGraphics();
        boolean even = true;
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, myRect.width, myRect.height);
        graphics.setColor(Color.LIGHT_GRAY);
        for (int x = 0; x < myRect.width; x += 5) {
            even = ((x / 5) & 1) == 0;
            for (int y = 0; y < myRect.height; y += 5) {
                even = !even;
                if (even) {
                    graphics.fillRect(x, y, 5, 5);
                }
            }
        }

        return image;
    }
}
