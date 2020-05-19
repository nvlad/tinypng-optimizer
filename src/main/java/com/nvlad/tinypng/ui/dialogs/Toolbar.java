package com.nvlad.tinypng.ui.dialogs;

import com.intellij.ide.actions.AboutAction;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import icons.ImagesIcons;
import org.intellij.images.actions.ToggleTransparencyChessboardAction;

import javax.swing.*;
import java.awt.*;

class Toolbar {
    static JPanel create() {
        DefaultActionGroup group = new DefaultActionGroup();
        group.add(new AboutAction());
        ToggleTransparencyChessboardAction transparencyChessboardAction = new ToggleTransparencyChessboardAction();
        transparencyChessboardAction.getTemplatePresentation().setIcon(ImagesIcons.ToggleTransparencyChessboard);
        group.add(transparencyChessboardAction);

        ActionToolbar actionToolbar = ActionManager.getInstance().createActionToolbar("top", group, true);
        JPanel toolbar = (JPanel) actionToolbar.getComponent();
        toolbar.setMinimumSize(new Dimension(0, actionToolbar.getMaxButtonHeight()));
        actionToolbar.setTargetComponent(toolbar);

        return toolbar;
    }
}
