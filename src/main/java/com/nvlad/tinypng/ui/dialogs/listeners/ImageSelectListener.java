package com.nvlad.tinypng.ui.dialogs.listeners;

import com.intellij.openapi.vfs.VirtualFile;
import com.nvlad.tinypng.ui.components.JImage;
import com.nvlad.tinypng.ui.dialogs.FileTreeNode;
import com.nvlad.tinypng.ui.dialogs.ProcessImage;
import com.nvlad.tinypng.util.StringFormatUtil;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import java.io.IOException;

public class ImageSelectListener implements TreeSelectionListener {
    private final ProcessImage myDialog;
    private final JTree myTree;
    private JImage imageBefore;
    private JLabel detailsBefore;
    private JImage imageAfter;
    private JLabel detailsAfter;

    public ImageSelectListener(ProcessImage dialog, JTree tree) {
        myDialog = dialog;
        myTree = tree;
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        FileTreeNode node = (FileTreeNode) myTree.getLastSelectedPathComponent();
        try {
            updateImage(imageBefore, detailsBefore, node.getVirtualFile());
            updateImage(imageAfter, detailsAfter, node.getImageBuffer());
        } catch (IOException e1) {
            e1.printStackTrace();
        }

    }

    public void setBeforeComponents(JImage image, JLabel label) {
        imageBefore = image;
        detailsBefore = label;
    }

    public void setAfterComponents(JImage image, JLabel label) {
        imageAfter = image;
        detailsAfter = label;
    }

    private void updateImage(JImage imagePanel, JLabel detailsLabel, VirtualFile file) throws IOException {
        if (file.isDirectory()) {
            imagePanel.setImage((VirtualFile) null);
            myDialog.clearTitle();
        } else {
            imagePanel.setImage(file);
            final int width = imagePanel.getImage().getWidth(myDialog);
            final int height = imagePanel.getImage().getHeight(myDialog);
            myDialog.setTitle(String.format("- %s [%dx%d]", file.getName(), width, height));
        }

        updateImageDetails(imagePanel, detailsLabel);
    }

    private void updateImage(JImage imagePanel, JLabel detailsLabel, byte[] buffer) throws IOException {
        imagePanel.setImage(buffer);
        updateImageDetails(imagePanel, detailsLabel);
    }

    private void updateImageDetails(JImage imagePanel, JLabel detailsLabel) {
        if (imagePanel.getImage() == null) {
            detailsLabel.setText("Size: ---");
        } else {
            detailsLabel.setText(String.format("Size: %s", StringFormatUtil.humanReadableByteCount(imagePanel.getImageSize())));
        }
    }
}
