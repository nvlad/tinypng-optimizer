package com.nvlad.tinypng.services;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.nvlad.tinypng.PluginGlobalSettings;
import com.tinify.Tinify;

import java.io.IOException;

public class TinyPNG {
    public static byte[] process(VirtualFile file) throws IOException {
        if (StringUtil.isEmptyOrSpaces(Tinify.key())) {
            PluginGlobalSettings settings = PluginGlobalSettings.getInstance();
            Tinify.setKey(settings.apiKey);
        }

        return Tinify.fromFile(file.getPath()).toBuffer();
    }
}
