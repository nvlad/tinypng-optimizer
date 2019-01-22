package com.nvlad.tinypng.ui.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.text.StringUtil;
import com.nvlad.tinypng.Constants;
import com.nvlad.tinypng.PluginGlobalSettings;
import com.tinify.Exception;
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
        return Constants.TITLE;
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
            if (StringUtil.isEmptyOrSpaces(Tinify.key())) {
                Tinify.setKey(settings.apiKey);
            }

            updateUsageCount(false);
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
        Tinify.setKey(settings.apiKey);
        if (!StringUtil.isEmptyOrSpaces(settings.apiKey)) {
            updateUsageCount(true);
        } else {
            usage.setText(Constants.SETTINGS_USAGE_EMPTY);
        }
    }

    @Override
    public void reset() {
        PluginGlobalSettings settings = PluginGlobalSettings.getInstance();
        apiKey.setText(settings.apiKey);
    }

    private void updateUsageCount(boolean showErrorDialog) {
        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            try {
                if (Tinify.validate()) {
                    usage.setText(Constants.SETTINGS_USAGE + Tinify.compressionCount());
                } else {
                    usage.setText(Constants.SETTINGS_USAGE_EMPTY);
                }
            } catch (Exception e) {
                ApplicationManager.getApplication().invokeLater(() -> {
                    usage.setText(Constants.SETTINGS_USAGE_EMPTY);
                    if (showErrorDialog) {
                        Messages.showErrorDialog(e.getMessage(), Constants.TITLE);
                    }
                }, ModalityState.stateForComponent(mainPanel));
            }
        });
    }
}
