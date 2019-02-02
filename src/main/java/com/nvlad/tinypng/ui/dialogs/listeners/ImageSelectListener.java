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

    public ImageSelectListener(ProcessImage dialog) {
        myDialog = dialog;
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        FileTreeNode node = (FileTreeNode) myDialog.getTree().getLastSelectedPathComponent();
        try {
            updateImage(myDialog.getImageBefore(), myDialog.getDetailsBefore(), node.getVirtualFile());
            updateImage(myDialog.getImageAfter(), myDialog.getDetailsAfter(), node.getImageBuffer());
        } catch (IOException e1) {
            e1.printStackTrace();
        }

    }

    private void updateImage(JImage imagePanel, JLabel detailsLabel, VirtualFile file) throws IOException {
        if (file.isDirectory()) {
            imagePanel.setImage((VirtualFile) null);
            myDialog.clearTitle();
        } else {
            imagePanel.setImage(file);
            if (imagePanel.getImage() != null) {
                final int width = imagePanel.getImage().getWidth(myDialog);
                final int height = imagePanel.getImage().getHeight(myDialog);
                myDialog.setTitle(String.format("- %s [%dx%d]", file.getName(), width, height));
            }
        }

        updateImageDetails(imagePanel, detailsLabel, "Old");
    }

    private void updateImage(JImage imagePanel, JLabel detailsLabel, byte[] buffer) throws IOException {
        imagePanel.setImage(buffer);
        updateImageDetails(imagePanel, detailsLabel, "New");
    }

    private void updateImageDetails(JImage imagePanel, JLabel detailsLabel, String prefix) {
        if (imagePanel.getImage() == null) {
            detailsLabel.setText(prefix + " Size: ---");
        } else {
            detailsLabel.setText(String.format(prefix + " Size: %s", StringFormatUtil.humanReadableByteCount(imagePanel.getImageSize())));
        }
    }
}
