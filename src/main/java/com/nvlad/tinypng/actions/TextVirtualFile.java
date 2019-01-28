package com.nvlad.tinypng.actions;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileSystem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class TextVirtualFile extends VirtualFile {
    private final VirtualFile myFile;
    private static FileType myFileType = FileTypeManager.getInstance().getStdFileType("txt");

    public TextVirtualFile(@NotNull VirtualFile file) {
        myFile = file;
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return myFileType;
    }

    @NotNull
    @Override
    public String getName() {
        return myFile.getName();
    }

    @NotNull
    @Override
    public VirtualFileSystem getFileSystem() {
        return myFile.getFileSystem();
    }

    @NotNull
    @Override
    public String getPath() {
        return myFile.getPath();
    }

    @Override
    public boolean isWritable() {
        return myFile.isWritable();
    }

    @Override
    public boolean isDirectory() {
        return myFile.isDirectory();
    }

    @Override
    public boolean isValid() {
        return myFile.isValid();
    }

    @Override
    public VirtualFile getParent() {
        return myFile.getParent();
    }

    @Override
    public VirtualFile[] getChildren() {
        return myFile.getChildren();
    }

    @NotNull
    @Override
    public OutputStream getOutputStream(Object requestor, long newModificationStamp, long newTimeStamp) throws IOException {
        return myFile.getOutputStream(requestor, newModificationStamp, newTimeStamp);
    }

    @NotNull
    @Override
    public byte[] contentsToByteArray() throws IOException {
        return myFile.contentsToByteArray();
    }

    @Override
    public long getTimeStamp() {
        return myFile.getTimeStamp();
    }

    @Override
    public long getModificationStamp() {
        return myFile.getModificationStamp();
    }

    @Override
    public long getLength() {
        return myFile.getLength();
    }

    @Override
    public void refresh(boolean asynchronous, boolean recursive, @Nullable Runnable postRunnable) {
        myFile.refresh(asynchronous, recursive, postRunnable);
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return myFile.getInputStream();
    }
}
