package com.nvlad.tinypng;

import com.intellij.ide.projectView.impl.ProjectViewPane;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

public class projectViewPane extends ProjectViewPane {
    protected projectViewPane(@NotNull Project project) {
        super(project);
    }

    @Override
    public void select(Object element, VirtualFile file, boolean requestFocus) {
        System.out.println("Select " + file.getName());
    }
}
