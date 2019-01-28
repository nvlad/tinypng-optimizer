package com.nvlad.tinypng.ui.dialogs.listeners;

import com.nvlad.tinypng.ui.dialogs.ProcessImage;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CancelActionListener implements ActionListener {
    private final ProcessImage myDialog;

    public CancelActionListener(ProcessImage dialog) {
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
