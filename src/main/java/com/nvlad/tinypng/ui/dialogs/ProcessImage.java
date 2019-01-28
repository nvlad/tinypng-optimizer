package com.nvlad.tinypng.ui.dialogs;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.CheckboxTree;
import com.intellij.ui.JBColor;
import com.intellij.util.ui.UIUtil;
import com.intellij.util.ui.tree.TreeUtil;
import com.nvlad.tinypng.Constants;
import com.nvlad.tinypng.PluginGlobalSettings;
import com.nvlad.tinypng.ui.components.JImage;
import com.nvlad.tinypng.ui.dialogs.listeners.CancelActionListener;
import com.nvlad.tinypng.ui.dialogs.listeners.ImageSelectListener;
import com.nvlad.tinypng.ui.dialogs.listeners.ProcessActionListener;
import com.nvlad.tinypng.ui.dialogs.listeners.SaveActionListener;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

public class ProcessImage extends JDialog {
    private JPanel contentPane;
    private JButton buttonSave;
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
    private JLabel titleBefore;
    private JLabel titleAfter;
    private List<VirtualFile> myFiles;
    private List<VirtualFile> myRoots;
    private Project myProject;
    private boolean imageCompressInProgress;

    public ProcessImage(Project project, List<VirtualFile> files, List<VirtualFile> roots) {
        imageCompressInProgress = false;
        myFiles = files;
        myRoots = roots;
        myProject = project;

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonProcess);

        buttonProcess.addActionListener(new ProcessActionListener(this));
        buttonSave.addActionListener(new SaveActionListener(this));

        final CancelActionListener cancelActionListener = new CancelActionListener(this);
        buttonCancel.addActionListener(cancelActionListener);

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onClose();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(cancelActionListener, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        configureUI();
    }

    public void setDialogSize(JFrame frame) {
        this.setMinimumSize(new Dimension(frame.getWidth() / 2, frame.getHeight() / 2));
        this.setLocationRelativeTo(frame);
        PluginGlobalSettings settings = PluginGlobalSettings.getInstance();
        splitPanel.setDividerLocation(settings.dividerLocation);
        if (settings.dialogLocationX != 0) {
            this.setLocation(settings.dialogLocationX, settings.dialogLocationY);
        }

        if (settings.dialogSizeWidth != 0) {
            this.setPreferredSize(settings.getDialogSize());
        }

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                settings.setDialogSize(((ProcessImage) e.getSource()).getSize());
            }

            @Override
            public void componentMoved(ComponentEvent e) {
                settings.setDialogLocation(((ProcessImage) e.getSource()).getLocation());
            }
        });
        splitPanel.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY, new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                settings.dividerLocation = (int) evt.getNewValue();
            }
        });
        this.pack();
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
        clearTitle();

        titleBefore.setForeground(JBColor.green.darker());
        titleAfter.setForeground(JBColor.red.darker());
    }

    @Override
    public void setTitle(String title) {
        super.setTitle(Constants.TITLE + (title == null ? "" : " " + title));
    }

    public void clearTitle() {
        setTitle(null);
    }

    public JTree getTree() {
        return fileTree;
    }

    public JImage getImageBefore() {
        return (JImage) imageBefore;
    }

    public JImage getImageAfter() {
        return (JImage) imageAfter;
    }

    public JLabel getDetailsBefore() {
        return detailsBefore;
    }

    public JLabel getDetailsAfter() {
        return detailsAfter;
    }

    public JLabel getTotalDetails() {
        return totalDetails;
    }

    public JButton getButtonSave() {
        return buttonSave;
    }

    public JButton getButtonCancel() {
        return buttonCancel;
    }

    public JButton getButtonProcess() {
        return buttonProcess;
    }

    public void setCompressInProgress(boolean value) {
        imageCompressInProgress = value;
    }

    public boolean getCompressInProgress() {
        return imageCompressInProgress;
    }

    private void onClose() {
        dispose();
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        UIUtil.removeScrollBorder(scrollPanel);
        imageBefore = new JImage();
        imageAfter = new JImage();
        fileTree = new CheckboxTree(new FileCellRenderer(myProject), buildTree());
        fileTree.setRootVisible(false);
        fileTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        TreeUtil.expandAll(fileTree);

        fileTree.addTreeSelectionListener(new ImageSelectListener(this));
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
        }

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
