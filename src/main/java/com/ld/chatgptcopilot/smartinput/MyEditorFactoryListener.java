package com.ld.chatgptcopilot.smartinput;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.event.EditorFactoryEvent;
import com.intellij.openapi.editor.event.EditorFactoryListener;
import com.intellij.openapi.editor.impl.CaretImpl;
import com.intellij.openapi.editor.impl.EditorImpl;
import com.intellij.openapi.editor.impl.EditorLastActionTracker;
import org.jetbrains.annotations.NotNull;

/*
* */
final class MyEditorFactoryListener implements EditorFactoryListener {
    @Override
    public void editorReleased(@NotNull EditorFactoryEvent event) {
       /* EditorLastActionTracker tracker = ApplicationManager.getApplication().getService(EditorLastActionTracker.class);
        EditorImpl killedEditor = (EditorImpl) event.getEditor();
        CaretImpl currentCaret = killedEditor.getCaretModel().getCurrentCaret();*/

    }
}
