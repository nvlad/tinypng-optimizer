package com.nvlad.tinypng.ui.dialogs.listeners;

import com.nvlad.tinypng.ui.dialogs.FileTreeNode;
import com.nvlad.tinypng.ui.dialogs.ProcessImage;

import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

public abstract class ActionListenerBase implements ActionListener {
    final ProcessImage dialog;

    public ActionListenerBase(ProcessImage dialog) {
        this.dialog = dialog;
    }

    List<FileTreeNode> getCheckedNodes(FileTreeNode root) {
        List<FileTreeNode> nodes = new LinkedList<>();
        Enumeration enumeration = root.children();
        while (enumeration.hasMoreElements()) {
            FileTreeNode node = (FileTreeNode) enumeration.nextElement();
            if (!node.isLeaf()) {
                nodes.addAll(getCheckedNodes(node));
                continue;
            }

            if (node.isChecked()) {
                nodes.add(node);
            }
        }

        return nodes;
    }
}
