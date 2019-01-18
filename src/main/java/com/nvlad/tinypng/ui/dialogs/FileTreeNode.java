package com.nvlad.tinypng.ui.dialogs;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.CheckedTreeNode;

public class FileTreeNode extends CheckedTreeNode {
    private byte[] compressedImage;

    public FileTreeNode() {

    }

    public FileTreeNode(VirtualFile file) {
        super(file);
    }

    public VirtualFile getVirtualFile() {
        return (VirtualFile) getUserObject();
    }

    public byte[] getImageBuffer() {
        return compressedImage;
    }

    public void setImageBufer(byte[] compressedImage) {
        this.compressedImage = compressedImage;
    }
}
