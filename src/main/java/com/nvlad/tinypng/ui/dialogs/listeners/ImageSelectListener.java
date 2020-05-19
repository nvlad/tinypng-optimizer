package com.nvlad.tinypng.ui.dialogs.listeners;

import com.nvlad.tinypng.entities.ImageEntity;
import com.nvlad.tinypng.ui.dialogs.FileTreeNode;
import com.nvlad.tinypng.ui.dialogs.ProcessImage;
import com.nvlad.tinypng.util.Console;
import org.intellij.images.ui.ImageComponent;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Logger;

public class ImageSelectListener implements TreeSelectionListener {
    private final ProcessImage myDialog;

    public ImageSelectListener(ProcessImage dialog) {
        myDialog = dialog;
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        FileTreeNode node = (FileTreeNode) myDialog.getTree().getLastSelectedPathComponent();
        try {
            if (node.getVirtualFile().isDirectory()) {
                updateImage(myDialog.getImageBefore(), myDialog.getDetailsBefore(), null);
                updateImage(myDialog.getImageAfter(), myDialog.getDetailsAfter(), null);
            } else {
                updateImage(myDialog.getImageBefore(), myDialog.getDetailsBefore(), new ImageEntity(node.getVirtualFile()));
                if (node.getImageBuffer() != null) {
                    updateImage(myDialog.getImageAfter(), myDialog.getDetailsAfter(), new ImageEntity(node.getImageBuffer()));
                }
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }

    }

    private void updateImage(ImageComponent imagePanel, JLabel detailsLabel, ImageEntity entity) throws IOException {
        if (entity == null) {
            imagePanel.getDocument().setValue((BufferedImage) null);
            myDialog.clearTitle();
        } else {
            BufferedImage image = entity.getSource();
            imagePanel.getDocument().setValue(image);
            if (imagePanel.getDocument().getValue() != null) {
                final int width = image.getWidth(myDialog);
                final int height = image.getHeight(myDialog);
                myDialog.setTitle(String.format("- %s [%dx%d]", entity.getName(), width, height));
            }
        }
        imagePanel.updateUI();
        imagePanel.setTransparencyChessboardVisible(true);

        updateImageDetails(imagePanel, detailsLabel, "Old");
    }

//    private void updateImage(ImageComponent imagePanel, JLabel detailsLabel, ImageEntity entity) throws IOException {
//        imagePanel.getDocument().setValue(entity.getSource());
//        updateImageDetails(imagePanel, detailsLabel, "New");
//    }

    private void updateImageDetails(ImageComponent imagePanel, JLabel detailsLabel, String prefix) {
        if (imagePanel.getDocument().getValue() == null) {
            detailsLabel.setText(prefix + " Size: ---");

            Console.Log(prefix + " Size: ---");
        } else {
//            detailsLabel.setText(String.format(prefix + " Size: %s", StringFormatUtil.humanReadableByteCount(imagePanel.getDocument().getValue())));
        }
    }
}
