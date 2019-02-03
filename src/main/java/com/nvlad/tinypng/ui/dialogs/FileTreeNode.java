package com.nvlad.tinypng.ui.dialogs;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.CheckedTreeNode;
import com.nvlad.tinypng.services.TinyPNGErrorInfo;

public class FileTreeNode extends CheckedTreeNode {
    private byte[] compressedImage;
    private TinyPNGErrorInfo error;

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

    public void setImageBuffer(byte[] compressedImage) {
        this.compressedImage = compressedImage;
    }

    public boolean hasError() {
        return error != null;
    }

    public TinyPNGErrorInfo getError() {
        return error;
    }

    public void setError(TinyPNGErrorInfo error) {
        this.error = error;
    }
}
