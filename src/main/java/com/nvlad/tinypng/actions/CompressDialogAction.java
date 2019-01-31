package com.nvlad.tinypng.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.WindowManager;
import com.nvlad.tinypng.services.TinyPNG;
import com.nvlad.tinypng.ui.dialogs.ProcessImage;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;

public class CompressDialogAction extends BaseCompressAction {
    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        final VirtualFile[] roots = PlatformDataKeys.VIRTUAL_FILE_ARRAY.getData(e.getDataContext());
        final JFrame frame = WindowManager.getInstance().getFrame(project);
        if (roots == null || frame == null) {
            return;
        }

        TinyPNG.setupApiKey(project);

        final List<VirtualFile> list = getSupportedFileList(roots, false);
        final ProcessImage dialog = new ProcessImage(project, list, Arrays.asList(roots));
        dialog.setDialogSize(frame);
        dialog.setVisible(true);
    }
}
