package com.nvlad.tinypng.ui.components;

import com.nvlad.tinypng.util.Console;

import javax.swing.*;
import java.awt.*;

public class ImagePreview extends JPanel {
    private static final String uiClassID = "ImagePreviewUI";

    private JPanel imagePanel;
    private JLabel label;

    public ImagePreview() {
        super();

        imagePanel = new JPanel();
        imagePanel.setBackground(Color.green);
        add(imagePanel);

        label = new JLabel();
        label.setText("Label");
        add(label);

        Console.Log("ImagePreview");
    }

    public String getUIClassID() {
        return uiClassID;
    }
}
