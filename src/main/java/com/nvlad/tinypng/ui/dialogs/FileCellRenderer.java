package com.nvlad.tinypng.ui.dialogs;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Iconable;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.CheckboxTree;
import com.intellij.ui.ColoredTreeCellRenderer;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.util.IconUtil;

import javax.swing.*;

public class FileCellRenderer extends CheckboxTree.CheckboxTreeCellRenderer {
    private final Project myProject;

    public FileCellRenderer(Project project) {
        myProject = project;
    }
    @Override
    public void customizeRenderer(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        final FileTreeNode node = (FileTreeNode) value;
        final VirtualFile file = node.getVirtualFile();
        if (file == null) {
            return;
        }

        ColoredTreeCellRenderer renderer = getTextRenderer();
        renderer.setIcon(IconUtil.getIcon(file, Iconable.ICON_FLAG_VISIBILITY, myProject));
        renderer.append(file.getName());

        if (node.hasError()) {
            renderer.append("  " + node.getError().message, SimpleTextAttributes.ERROR_ATTRIBUTES);

            return;
        }

        if (node.getImageBuffer() != null) {
            long optimized = 100 - (node.getImageBuffer().length * 100 / file.getLength());
            renderer.append(String.format("  %d%%", optimized), SimpleTextAttributes.DARK_TEXT);
        }
    }
}
