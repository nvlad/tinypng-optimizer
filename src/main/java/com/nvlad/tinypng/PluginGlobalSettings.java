package com.nvlad.tinypng;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(name = "TinyPNG Image Optimizer", storages = @Storage(file = "$APP_CONFIG$/TinyPNG-Image-Optimizer.xml"))
public class PluginGlobalSettings implements PersistentStateComponent<PluginGlobalSettings> {
    public String version;
    public String uuid;
    public String username;
    public String apiKey;

    @Nullable
    @Override
    public PluginGlobalSettings getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull PluginGlobalSettings settings) {
        XmlSerializerUtil.copyBean(settings, this);
    }

    public static PluginGlobalSettings getInstance() {
        return ServiceManager.getService(PluginGlobalSettings.class);
    }
}
