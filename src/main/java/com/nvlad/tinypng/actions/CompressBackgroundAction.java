package com.nvlad.tinypng.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.nvlad.tinypng.services.TinyPNG;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class CompressBackgroundAction extends BaseCompressAction {
    @Override
    public void actionPerformed(AnActionEvent e) {
        ProgressManager.getInstance().run(new Task.Backgroundable(e.getProject(), "TinyPNG Image Optimizer", true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                Project project = e.getProject();
                final VirtualFile[] roots = PlatformDataKeys.VIRTUAL_FILE_ARRAY.getData(e.getDataContext());
                if (roots == null || !TinyPNG.setupApiKey(project)) {
                    return;
                }

                final List<VirtualFile> list = getSupportedFileList(roots, false);
                indicator.setText("Optimize Image Size...");
                double index = 0;
                for (VirtualFile file : list) {
                    indicator.setText2(file.getName());
                    index += 1;
                    try {
                        final byte[] result = TinyPNG.process(file);
                        WriteCommandAction.runWriteCommandAction(project, new SaveImage(file, result));
                        indicator.setFraction(index / list.size());
                    } catch (IOException ex) {
                        System.out.println(ex.toString());
                    }
                }
            }
        });
    }

    class SaveImage implements Runnable {
        private final VirtualFile myFile;
        private final byte[] myBuffer;

        SaveImage(VirtualFile file, byte[] buffer) {
            myFile = file;
            myBuffer = buffer;
        }

        @Override
        public void run() {
            try {
                OutputStream stream = myFile.getOutputStream(this);
                stream.write(myBuffer);
                stream.close();
            } catch (IOException e) {
                System.out.println(e.toString());
            }
        }
    }
}
