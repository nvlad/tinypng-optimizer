package com.nvlad.tinypng.ui.dialogs.listeners;

import com.intellij.openapi.application.ApplicationManager;
import com.nvlad.tinypng.services.TinyPNG;
import com.nvlad.tinypng.ui.dialogs.FileTreeNode;
import com.nvlad.tinypng.ui.dialogs.ProcessImage;
import com.nvlad.tinypng.util.StringFormatUtil;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;

public class ProcessActionListener extends ActionListenerBase {
    public ProcessActionListener(ProcessImage dialog, JTree tree) {
        super(dialog, tree);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        myDialog.setTitle("[0%]");
        imageCompressInProgress = true;
        buttonProcess.setEnabled(false);
        buttonCancel.setText("Stop");
        final List<FileTreeNode> nodes = getCheckedNodes((FileTreeNode) myTree.getModel().getRoot());
        for (FileTreeNode node : nodes) {
            node.setImageBuffer(null);
            ((DefaultTreeModel) myTree.getModel()).nodeChanged(node);
        }

        ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
            @Override
            public void run() {
                int index = 0;
                for (FileTreeNode node : nodes) {
                    try {
                        node.setImageBuffer(TinyPNG.process(node.getVirtualFile()));
//                } catch (Exception e) {
//                    System.out.println(e);
                    } catch (IOException e) {

                        e.printStackTrace();
                    }

                    final float finalIndex = index;
                    ApplicationManager.getApplication().invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            ((DefaultTreeModel) myTree.getModel()).nodeChanged(node);
                            dialog.setTitle(String.format("[%.0f%%]", finalIndex / nodes.size() * 100));
                        }
                    });

                    if (!imageCompressInProgress) {
                        break;
                    }

                    index++;
                }

                ApplicationManager.getApplication().invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        myDialog.clearTitle();
                        myDialog.imageCompressInProgress = false;
                        myDialog.getRootPane().setDefaultButton(buttonOK);
                        myDialog.buttonProcess.setEnabled(true);
                        myDialog.buttonOK.setEnabled(true);
                        myDialog.buttonCancel.setText("Cancel");

                        long totalBytes = 0;
                        long totalSavedBytes = 0;
                        for (FileTreeNode node : nodes) {
                            totalBytes += node.getVirtualFile().getLength();
                            totalSavedBytes += node.getVirtualFile().getLength() - node.getImageBuffer().length;
                        }

                        float compress = (((float) totalSavedBytes) * 100 / ((float) totalBytes));
                        String saved = StringFormatUtil.humanReadableByteCount(totalSavedBytes);
                        totalDetails.setText(String.format("Total compress: %.1f%% / Saved: %s", compress, saved));
                    }
                });

            }
        });

    }
}
