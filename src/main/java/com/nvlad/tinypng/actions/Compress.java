package com.nvlad.tinypng.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.util.ArrayUtil;
import com.nvlad.tinypng.Constants;
import com.nvlad.tinypng.Icons;
import com.nvlad.tinypng.PluginGlobalSettings;
import com.nvlad.tinypng.ui.dialogs.ProcessImage;
import com.tinify.Tinify;

import javax.swing.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Compress extends AnAction {
    private static final String[] supportedExtensions = {"png", "jpg", "jpeg"};

    public Compress() {
        getTemplatePresentation().setIcon(Icons.ACTION);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        final VirtualFile[] roots = PlatformDataKeys.VIRTUAL_FILE_ARRAY.getData(e.getDataContext());
        final JFrame frame = WindowManager.getInstance().getFrame(project);
        if (roots == null || frame == null) {
            return;
        }

        if (StringUtil.isEmptyOrSpaces(Tinify.key())) {
            PluginGlobalSettings settings = PluginGlobalSettings.getInstance();
            if (StringUtil.isEmptyOrSpaces(settings.apiKey)) {
                settings.apiKey = Messages.showInputDialog(project, Constants.API_KEY_QUESTION, Constants.TITLE, Messages.getQuestionIcon());
            }

            if (StringUtil.isEmptyOrSpaces(settings.apiKey)) {
                return;
            }

            Tinify.setKey(settings.apiKey);
        }

        final List<VirtualFile> list = getSupportedFileList(roots);
        final ProcessImage dialog = new ProcessImage(project, list, Arrays.asList(roots));
        dialog.setDialogSize(frame);
        dialog.setVisible(true);
    }

    @Override
    public void update(AnActionEvent e) {
        final Presentation presentation = e.getPresentation();
        final List<VirtualFile> list = getSupportedFileList(PlatformDataKeys.VIRTUAL_FILE_ARRAY.getData(e.getDataContext()));
        presentation.setIcon(Icons.ACTION);
        presentation.setEnabled(!list.isEmpty());
        super.update(e);
    }

    private List<VirtualFile> getSupportedFileList(VirtualFile[] files) {
        List<VirtualFile> result = new LinkedList<>();
        if (files == null) {
            return result;
        }

        for (VirtualFile file : files) {
            if (file.isDirectory()) {
                result.addAll(getSupportedFileList(file.getChildren()));
                continue;
            }

            final String extension = file.getExtension();
            if (extension != null && ArrayUtil.contains(extension.toLowerCase(), supportedExtensions)) {
                result.add(file);
            }
        }

        return result;
    }
}
