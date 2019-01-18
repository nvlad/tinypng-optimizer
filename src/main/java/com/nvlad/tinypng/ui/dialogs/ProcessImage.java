package com.nvlad.tinypng.ui.dialogs;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.CheckboxTree;
import com.intellij.util.ui.UIUtil;
import com.intellij.util.ui.tree.TreeUtil;
import com.nvlad.tinypng.services.TinyPNG;
import com.nvlad.tinypng.ui.components.JImage;
import com.nvlad.tinypng.util.StringFormatUtil;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

public class ProcessImage extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JPanel imageBefore;
    private JLabel detailsBefore;
    private JPanel imageAfter;
    private JLabel detailsAfter;
    private JTree fileTree;
    private JScrollPane scrollPanel;
    private JSplitPane splitPanel;
    private JButton buttonProcess;
    private JLabel totalDetails;
    private List<VirtualFile> myFiles;
    private List<VirtualFile> myRoots;
    private boolean imageCompressInProgress;

    public ProcessImage(List<VirtualFile> files, List<VirtualFile> roots) {
        imageCompressInProgress = false;
        myFiles = files;
        myRoots = roots;

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonProcess);

        buttonProcess.addActionListener(e -> {
            onProcess();
        });

        buttonOK.addActionListener(e -> onSave());

        buttonCancel.addActionListener(e -> onCancel());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        configureUI();
    }


    private void configureUI() {
        splitPanel.setBackground(UIUtil.getPanelBackground());
        splitPanel.setUI(new BasicSplitPaneUI() {
            public BasicSplitPaneDivider createDefaultDivider() {
                return new BasicSplitPaneDivider(this) {
                    private final int dashHeight = 40;
                    private Color background = UIUtil.getPanelBackground();
                    private Color dashes = UIUtil.getSeparatorColor();

                    public void setBorder(Border b) {
                    }

                    @Override
                    public void paint(Graphics g) {
                        g.setColor(background);
                        g.fillRect(0, 0, getSize().width, getSize().height);

                        final int top = (getSize().height - dashHeight) / 2;
                        g.setColor(dashes);
                        g.drawLine(4, top, 4, top + dashHeight);
                        g.drawLine(7, top, 7, top + dashHeight);
                        super.paint(g);
                    }
                };
            }
        });
        splitPanel.setBorder(null);
    }

    private void onProcess() {
        imageCompressInProgress = true;
        buttonProcess.setEnabled(false);
        buttonCancel.setText("Stop");
        final List<FileTreeNode> nodes = getCheckedNodes((FileTreeNode) fileTree.getModel().getRoot());
        for (FileTreeNode node : nodes) {
            node.setImageBufer(null);
            ((DefaultTreeModel) fileTree.getModel()).nodeChanged(node);
        }

        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            for (FileTreeNode node : nodes) {
                try {
                    node.setImageBufer(TinyPNG.process(node.getVirtualFile()));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                ApplicationManager.getApplication().invokeLater(() -> {
                    ((DefaultTreeModel) fileTree.getModel()).nodeChanged(node);
                });

                if (!imageCompressInProgress) {
                    break;
                }
            }

            ApplicationManager.getApplication().invokeLater(() -> {
                imageCompressInProgress = false;
                getRootPane().setDefaultButton(buttonOK);
                buttonProcess.setEnabled(true);
                buttonOK.setEnabled(true);
                buttonCancel.setText("Cancel");

                long totalBytes = 0;
                long totalSavedBytes = 0;
                for (FileTreeNode node : nodes) {
                    totalBytes += node.getVirtualFile().getLength();
                    totalSavedBytes += node.getVirtualFile().getLength() - node.getImageBuffer().length;
                }

                float compress = (((float) totalSavedBytes) * 100 / ((float) totalBytes));
                String saved = StringFormatUtil.humanReadableByteCount(totalSavedBytes);
                totalDetails.setText(String.format("Total compress: %.1f%% / Saved: %s", compress, saved));
            });
        });
    }

    private List<FileTreeNode> getCheckedNodes(FileTreeNode root) {
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

    private void onSave() {
        buttonOK.setEnabled(false);
        buttonCancel.setEnabled(false);

        List<FileTreeNode> nodes = getCheckedNodes((FileTreeNode) fileTree.getModel().getRoot());
        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            for (FileTreeNode node : nodes) {
                try {
                    OutputStream stream = node.getVirtualFile().getOutputStream(this);
                    stream.write(node.getImageBuffer());
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            ApplicationManager.getApplication().invokeLater(() -> {
                for (FileTreeNode node : nodes) {
                    node.setImageBufer(null);
                    ((DefaultTreeModel) fileTree.getModel()).nodeChanged(node);
                }

                buttonCancel.setText("Close");
                buttonCancel.setEnabled(true);
            });
        });
    }

    private void onCancel() {
        if (!imageCompressInProgress) {
            dispose();
        }

        imageCompressInProgress = false;
        buttonCancel.setText("Cancel");
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        UIUtil.removeScrollBorder(scrollPanel);
        imageBefore = new JImage();
        imageAfter = new JImage();
        fileTree = new CheckboxTree(new FileCellRenderer(), buildTree());
        fileTree.setRootVisible(false);
        fileTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        TreeUtil.expandAll(fileTree);
        fileTree.addTreeSelectionListener(e -> {
            FileTreeNode node = (FileTreeNode) fileTree.getLastSelectedPathComponent();
            try {
                updateImage((JImage) imageBefore, detailsBefore, node.getVirtualFile());
                updateImage((JImage) imageAfter, detailsAfter, node.getImageBuffer());
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
    }

    private void updateImage(JImage imagePanel, JLabel detailsLabel, VirtualFile file) throws IOException {
        if (file.isDirectory()) {
            imagePanel.setImage((VirtualFile) null);
        } else {
            imagePanel.setImage(file);
        }

        updateImageDetails(imagePanel, detailsLabel);
    }

    private void updateImage(JImage imagePanel, JLabel detailsLabel, byte[] buffer) throws IOException {
        imagePanel.setImage(buffer);
        updateImageDetails(imagePanel, detailsLabel);
    }

    private void updateImageDetails(JImage imagePanel, JLabel detailsLabel) {
        if (imagePanel.getImage() == null) {
            detailsLabel.setText("Size: ---");
        } else {
            detailsLabel.setText(String.format("Size: %s", StringFormatUtil.humanReadableByteCount(imagePanel.getImageSize())));
        }
    }

    private FileTreeNode buildTree() {
        FileTreeNode root = new FileTreeNode();
        for (VirtualFile file : myFiles) {
            getParent(root, file).add(new FileTreeNode(file));
        }

        return root;
    }

    private FileTreeNode getParent(FileTreeNode root, VirtualFile file) {
        if (myRoots.contains(file)) {
            return root;
        }

        LinkedList<VirtualFile> path = new LinkedList<>();
        while (!myRoots.contains(file)) {
            file = file.getParent();
            path.addFirst(file);
        };

        FileTreeNode parent = root;
        for (VirtualFile pathElement : path) {
            FileTreeNode node = findNodeByUserObject(parent, pathElement);
            if (node == null) {
                node = new FileTreeNode(pathElement);
                parent.add(node);
            }

            parent = node;
        }

        return parent;
    }

    @Nullable
    private FileTreeNode findNodeByUserObject(FileTreeNode root, Object userObject) {
        Enumeration enumeration = root.children();
        while (enumeration.hasMoreElements()) {
            FileTreeNode node = (FileTreeNode) enumeration.nextElement();
            if (node.getUserObject() == userObject) {
                return node;
            }
        }

        return null;
    }
}