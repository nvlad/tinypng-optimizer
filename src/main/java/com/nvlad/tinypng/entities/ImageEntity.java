package com.nvlad.tinypng.entities;

import com.intellij.openapi.vfs.VirtualFile;
import org.ini4j.InvalidFileFormatException;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

public class ImageEntity {
    private BufferedImage mySource;
    private long mySourceSize;
    private String myImageFormat;
    private String myName;

    public ImageEntity(VirtualFile file) throws IOException {
        this(file.getInputStream());
        myName = file.getName();
    }

    public ImageEntity(byte[] buffer) throws IOException {
        this(new ByteArrayInputStream(buffer));
    }

    public ImageEntity(InputStream stream) throws IOException {
        ImageInputStream imageInputStream = ImageIO.createImageInputStream(stream);
        final Iterator<ImageReader> readers = ImageIO.getImageReaders(imageInputStream);
        if (readers.hasNext()) {
            final ImageReader reader = readers.next();
            ImageReadParam param = reader.getDefaultReadParam();
            reader.setInput(imageInputStream, true, true);
            mySource = reader.read(0, param);
            mySourceSize = imageInputStream.length();
            myImageFormat = reader.getFormatName();
        } else {
            throw new InvalidFileFormatException("Unknown image format.");
        }
    }

    public BufferedImage getSource() {
        return mySource;
    }

    public long getSize() {
        return mySourceSize;
    }

    public String getFormat() {
        return myImageFormat;
    }

    public String getName() {
        return myName;
    }
}
