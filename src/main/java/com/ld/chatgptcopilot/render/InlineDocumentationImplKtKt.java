package com.ld.chatgptcopilot.render;

import java.util.List;

import com.intellij.codeInsight.documentation.DocumentationManager;
import com.intellij.lang.documentation.InlineDocumentation;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiDocCommentBase;
import com.intellij.psi.PsiFile;
import com.intellij.util.SmartList;
import com.intellij.util.concurrency.annotations.RequiresBackgroundThread;
import com.intellij.util.concurrency.annotations.RequiresReadLock;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class InlineDocumentationImplKtKt {
    @RequiresReadLock
    @RequiresBackgroundThread
    @NotNull
    public static List<InlineDocumentation> inlineDocumentationItems(@NotNull PsiFile file) {
        Intrinsics.checkNotNullParameter(file, "file");
        SmartList<InlineDocumentation> result = new SmartList<>();
        DocumentationManager.getProviderFromElement(file).collectDocComments(file, (@NotNull PsiDocCommentBase it) -> {
            Intrinsics.checkNotNullParameter(it, "it");
            result.add(new PsiCommentInlineDocumentation(it));
        });
        return result;
    }

    @RequiresReadLock
    @RequiresBackgroundThread
    @Nullable
    public static InlineDocumentation findInlineDocumentation(@NotNull PsiFile file, @NotNull TextRange textRange) {
        Intrinsics.checkNotNullParameter(file, "file");
        Intrinsics.checkNotNullParameter(textRange, "textRange");
        PsiDocCommentBase docComment = DocumentationManager.getProviderFromElement(file).findDocComment(file, textRange);
        if (docComment == null) {
            return null;
        } else {
            Intrinsics.checkNotNullExpressionValue(docComment, "DocumentationManager.get…textRange) ?: return null");
            return new PsiCommentInlineDocumentation(docComment);
        }
    }
}
