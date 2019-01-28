package com.nvlad.tinypng.ui.dialogs.listeners;

import com.intellij.openapi.application.ApplicationManager;
import com.nvlad.tinypng.services.TinyPNG;
import com.nvlad.tinypng.ui.dialogs.FileTreeNode;
import com.nvlad.tinypng.ui.dialogs.ProcessImage;
import com.nvlad.tinypng.util.StringFormatUtil;

import javax.swing.tree.DefaultTreeModel;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.List;

public class ProcessActionListener extends ActionListenerBase {
    public ProcessActionListener(ProcessImage dialog) {
        super(dialog);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        dialog.setTitle("[0%]");
        dialog.setCompressInProgress(true);
        dialog.getButtonProcess().setEnabled(false);
        dialog.getButtonCancel().setText("Stop");
        final List<FileTreeNode> nodes = getCheckedNodes((FileTreeNode) dialog.getTree().getModel().getRoot());
        for (FileTreeNode node : nodes) {
            node.setImageBuffer(null);
            ((DefaultTreeModel) dialog.getTree().getModel()).nodeChanged(node);
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
                            ((DefaultTreeModel) dialog.getTree().getModel()).nodeChanged(node);
                            dialog.setTitle(String.format("[%.0f%%]", finalIndex / nodes.size() * 100));
                        }
                    });

                    if (!dialog.getCompressInProgress()) {
                        break;
                    }

                    index++;
                }

                ApplicationManager.getApplication().invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        dialog.clearTitle();
                        dialog.setCompressInProgress(false);
                        dialog.getRootPane().setDefaultButton(dialog.getButtonSave());
                        dialog.getButtonSave().setEnabled(true);
                        dialog.getButtonCancel().setText("Cancel");

                        long totalBytes = 0;
                        long totalSavedBytes = 0;
                        for (FileTreeNode node : nodes) {
                            totalBytes += node.getVirtualFile().getLength();
                            totalSavedBytes += node.getVirtualFile().getLength() - node.getImageBuffer().length;
                        }

                        float compress = (((float) totalSavedBytes) * 100 / ((float) totalBytes));
                        String saved = StringFormatUtil.humanReadableByteCount(totalSavedBytes);
                        dialog.getTotalDetails().setText(String.format("Total compress: %.1f%% / Saved: %s", compress, saved));
                    }
                });

            }
        });
    }
}
