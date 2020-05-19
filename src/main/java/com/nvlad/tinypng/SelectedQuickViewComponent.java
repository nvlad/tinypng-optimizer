package com.nvlad.tinypng;

import com.intellij.ide.projectView.ProjectView;
import com.intellij.ide.projectView.impl.nodes.BasePsiNode;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.nvlad.tinypng.util.Console;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class SelectedQuickViewComponent implements ProjectComponent {
    private final Timer myTimer;
    private final Project myProject;
    private JTree myTree;
    private FileEditor myQuickView;

    public SelectedQuickViewComponent(Project project) {
        myTimer = new Timer(true);
        myProject = project;
    }

    @Override
    public void projectOpened() {
        myTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Console.Log("SelectedQuickViewComponent::run");
                try {
                    myTree = ProjectView.getInstance(myProject).getCurrentProjectViewPane().getTree();
                    myTree.addTreeSelectionListener(e -> {
                        if (myTree.getSelectionModel().getSelectionCount() != 1) {
                            return;
                        }
                        final VirtualFile virtualFile = getVirtualFileByPath(e.getPath());
                        if (virtualFile.isDirectory()) {
                            return;
                        }

                        FileEditorManager fileEditorManager = FileEditorManager.getInstance(myProject);

                        final FileEditor[] fileEditors = fileEditorManager.getAllEditors(virtualFile);
                        if (fileEditors.length > 0) {
                            fileEditorManager.openFile(virtualFile, false);
                        } else {
//                            FileEditorManager.getInstance(myProject)
                        }

                        System.out.println("SelectedQuickViewComponent::addTreeSelectionListener");
                    });

                    myTimer.cancel();
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }, new Date(), 200);
    }

    private static VirtualFile getVirtualFileByPath(TreePath treePath) {
        return ((BasePsiNode) ((DefaultMutableTreeNode) treePath.getLastPathComponent()).getUserObject()).getVirtualFile();
    }
}
