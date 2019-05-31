package com.nvlad.tinypng.ui.dialogs.listeners;

import com.nvlad.tinypng.ui.dialogs.ProcessImageDialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CancelActionListener implements ActionListener {
    private final ProcessImageDialog myDialog;

    public CancelActionListener(ProcessImageDialog dialog) {
        myDialog = dialog;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!myDialog.getCompressInProgress()) {
            myDialog.dispose();
        }

        myDialog.setCompressInProgress(false);
        myDialog.getButtonCancel().setText("Cancel");
    }
}
