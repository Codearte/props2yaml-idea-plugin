package io.codearte.props2yaml;

import java.io.IOException;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;

/**
 * @author Jakub Kubrynski
 */
public class ConvertAction extends AnAction {

    public static final String GROUP_DISPLAY_ID = "io.codearte.props2yaml";

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        PsiFile selectedFile = anActionEvent.getData(LangDataKeys.PSI_FILE);
        if (selectedFile == null) {
            Notifications.Bus.notify(new Notification(GROUP_DISPLAY_ID, "No file selected", "Please select properties file first", NotificationType.ERROR));
            return;
        }
        if (!StdFileTypes.PROPERTIES.equals(selectedFile.getFileType())) {
            Notifications.Bus.notify(new Notification(GROUP_DISPLAY_ID, "Incorrect file selected", "Please select properties file first", NotificationType.ERROR));
            return;
        }

        VirtualFile propertiesFile = selectedFile.getVirtualFile();
        ApplicationManager.getApplication().runWriteAction(() -> {
            try {
                String yamlContent = new Props2YAML(new String(propertiesFile.contentsToByteArray())).convert();
                propertiesFile.rename(this, propertiesFile.getNameWithoutExtension() + ".yml");
                propertiesFile.setBinaryContent(yamlContent.getBytes());
            } catch (IOException e) {
                Notifications.Bus.notify(new Notification(GROUP_DISPLAY_ID, "Cannot rename file", e.getMessage(), NotificationType.ERROR));
            }
        });

        Notifications.Bus.notify(new Notification(GROUP_DISPLAY_ID, "File converted", "File converted successfully", NotificationType.INFORMATION));
    }
}
