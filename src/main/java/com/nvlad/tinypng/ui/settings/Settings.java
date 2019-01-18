package com.nvlad.tinypng.ui.settings;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.util.text.StringUtil;
import com.nvlad.tinypng.PluginGlobalSettings;
import com.tinify.Tinify;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class Settings implements Configurable {
    private JPanel mainPanel;
    private JTextField apiKey;
    private JLabel usage;

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "TinyPNG Compress";
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return apiKey;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        PluginGlobalSettings settings = PluginGlobalSettings.getInstance();
        if (!StringUtil.isEmptyOrSpaces(settings.apiKey)) {
            Tinify.setKey(settings.apiKey);

            updateUsageCount();
        }

        return mainPanel;
    }

    @Override
    public boolean isModified() {
        PluginGlobalSettings settings = PluginGlobalSettings.getInstance();
        if (StringUtil.isEmptyOrSpaces(settings.apiKey) && StringUtil.isEmptyOrSpaces(apiKey.getText())) {
            return false;
        }

        return !apiKey.getText().equals(settings.apiKey);
    }

    @Override
    public void apply() {
        PluginGlobalSettings settings = PluginGlobalSettings.getInstance();
        settings.apiKey = apiKey.getText();

        if (!StringUtil.isEmptyOrSpaces(settings.apiKey)) {
            updateUsageCount();
        }
    }

    @Override
    public void reset() {
        PluginGlobalSettings settings = PluginGlobalSettings.getInstance();
        apiKey.setText(settings.apiKey);
    }

    private void updateUsageCount() {
        usage.setText("Usage this month: " + Tinify.compressionCount());
    }
}
