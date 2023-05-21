package com.ld.chatgptcopilot.smartinput;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Objects;

import com.intellij.codeInsight.hint.HintManager;
import com.intellij.lang.java.JavaLanguage;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.editor.event.EditorMouseEvent;
import com.intellij.openapi.editor.event.EditorMouseListener;
import com.intellij.psi.JavaTokenType;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

final class MyEditorMouseListener implements EditorMouseListener {
    static PsiElement last;

    @Override
    public void mousePressed(@NotNull EditorMouseEvent e) {

    }

    @Override
    public void mouseClicked(@NotNull EditorMouseEvent e) {
        //switchInputMethod(e);
    }

    private static void switchInputMethod(@NotNull EditorMouseEvent e) {
        Editor editor = e.getEditor();
        if (editor.getProject() == null) {
            return;
        }
        Caret currentCaret = editor.getCaretModel().getCurrentCaret();
        LogicalPosition logicalPosition = currentCaret.getLogicalPosition();
        System.out.println(logicalPosition);

        //获取当前位置psi元素
        Document document = editor.getDocument();
        PsiFile psiFile = PsiDocumentManager.getInstance(editor.getProject()).getPsiFile(document);
        if (psiFile == null) {
            return;
        }
        PsiElement psiElement = psiFile.findElementAt(currentCaret.getOffset());
        if (psiElement == null) {
            return;
        }
        if (!psiElement.getLanguage().is(JavaLanguage.INSTANCE)) {
            return;
        }
        String text = psiElement.getText();

        HintManager hintManager = HintManager.getInstance();
        if (last == null) {
            last = psiElement;
            return;
        }
        if (!Objects.equals(isComment(last), isComment(psiElement)) && (isComment(last) || isComment(psiElement))) {
            pressShift();
            last = psiElement;
            return;
        }


        //判断是否是注释
        /*if (psiElement instanceof PsiComment) {
            hintManager.showInformationHint(editor, "注释： " + text, HintManager.ABOVE);

        } else if (psiElement instanceof PsiJavaToken) {
            if (JavaTokenType.STRING_LITERAL.equals(((PsiJavaToken) psiElement).getTokenType())) {
                hintManager.showInformationHint(editor, "Java字符串： " + text, HintManager.ABOVE);
            } else {
                hintManager.showInformationHint(editor, "Java代码： " + text, HintManager.ABOVE);
            }

        }
        //判断是否是代码
        else if (psiElement instanceof PsiPlainText) {
            hintManager.showInformationHint(editor, "PsiPlainText： " + text, HintManager.ABOVE);
        } else {
            //获取具体psiElement class

            String type = psiElement.getClass().getSimpleName();
            hintManager.showInformationHint(editor, type + "： " + text, HintManager.ABOVE);
        }*/
    }

    private static boolean isComment(PsiElement psiElement) {
        if (psiElement == null) {
            return false;
        }
        if (psiElement instanceof PsiComment || preIsLineComment(psiElement)) {
            return true;
        }
        //递归判断父元素
        return isComment(psiElement.getParent());
    }

    private static boolean preIsLineComment(PsiElement psiElement) {
        if (psiElement == null || psiElement.getPrevSibling() == null) {
            return false;
        }
        if (!(psiElement.getPrevSibling() instanceof PsiComment)) {
            return false;
        }
        return JavaTokenType.END_OF_LINE_COMMENT.equals(((PsiComment) psiElement.getPrevSibling()).getTokenType());
    }

    private static void pressShift() {
        try {
            // 创建Robot对象
            Robot robot = new Robot();

            // 模拟按下SHIFT键
            robot.keyPress(KeyEvent.VK_SHIFT);

            // 模拟释放SHIFT键
            robot.keyRelease(KeyEvent.VK_SHIFT);

            // 模拟按下组合键'Ctrl' + 'C'
            //robot.keyPress(KeyEvent.VK_CONTROL);
            //robot.keyPress(KeyEvent.VK_C);

            // 模拟释放组合键'Ctrl' + 'C'
            //robot.keyRelease(KeyEvent.VK_C);
            //robot.keyRelease(KeyEvent.VK_CONTROL);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void mouseReleased(@NotNull EditorMouseEvent e) {
    }
}
