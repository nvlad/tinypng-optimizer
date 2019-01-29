package com.nvlad.tinypng;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.intellij.util.xmlb.annotations.Transient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

@State(name = Constants.TITLE, storages = @Storage(file = "$APP_CONFIG$/tinypng-image-optimizer.xml"))
public class PluginGlobalSettings implements PersistentStateComponent<PluginGlobalSettings> {
    public String version;
    public String uuid;
    public String username;
    public String apiKey;
    public int dialogLocationX;
    public int dialogLocationY;
    public int dialogSizeWidth;
    public int dialogSizeHeight;
    public int dividerLocation = 200;
    public boolean checkSupportedFiles = true;

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

    public void setDialogSize(Dimension dimension) {
        dialogSizeWidth = dimension.width;
        dialogSizeHeight = dimension.height;
    }

    @Transient
    public Dimension getDialogSize() {
        return new Dimension(dialogSizeWidth, dialogSizeHeight);
    }

    public void setDialogLocation(Point location) {
        dialogLocationX = location.x;
        dialogLocationY = location.y;
    }

    @Transient
    public Point getDialogLocation() {
        return new Point(dialogLocationX, dialogLocationY);
    }
}
