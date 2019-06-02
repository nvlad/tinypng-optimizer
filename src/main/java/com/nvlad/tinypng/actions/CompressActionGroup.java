package com.nvlad.tinypng.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;

public class CompressActionGroup extends DefaultActionGroup {
    private boolean firstRun = true;

    @Override
    public void update(AnActionEvent e) {
        if (!firstRun) {
            return;
        }

        firstRun = false;
        if (this.isPopup()) {
            AnAction[] actions = this.getChildren(e);
            for (AnAction action : actions) {
                action.getTemplatePresentation().setIcon(null);
            }
        }
    }
}
