package com.nvlad.tinypng.ui.dialogs.listeners;

import com.intellij.openapi.application.ApplicationManager;
import com.nvlad.tinypng.ui.dialogs.FileTreeNode;
import com.nvlad.tinypng.ui.dialogs.ProcessImage;

import javax.swing.tree.DefaultTreeModel;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class SaveActionListener extends ActionListenerBase {
    public SaveActionListener(ProcessImage dialog) {
        super(dialog);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        dialog.getButtonSave().setEnabled(false);
        dialog.getButtonCancel().setEnabled(false);

        List<FileTreeNode> nodes = getCheckedNodes((FileTreeNode) dialog.getTree().getModel().getRoot());
        ApplicationManager.getApplication().runWriteAction(new Runnable() {
            @Override
            public void run() {
                for (FileTreeNode node : nodes) {
                    try {
                        OutputStream stream = node.getVirtualFile().getOutputStream(this);
                        stream.write(node.getImageBuffer());
                        stream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                ApplicationManager.getApplication().invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        for (FileTreeNode node : nodes) {
                            node.setImageBuffer(null);
                            ((DefaultTreeModel) dialog.getTree().getModel()).nodeChanged(node);
                        }

                        dialog.getButtonCancel().setText("Close");
                        dialog.getButtonCancel().setEnabled(true);
                        dialog.getButtonProcess().setEnabled(true);
                        dialog.getRootPane().setDefaultButton(dialog.getButtonProcess());
                    }
                });
            }
        });
    }
}
