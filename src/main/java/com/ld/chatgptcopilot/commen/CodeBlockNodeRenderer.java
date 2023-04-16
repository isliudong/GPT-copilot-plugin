package com.ld.chatgptcopilot.commen;


import java.util.Set;

import com.vladsch.flexmark.ast.FencedCodeBlock;
import com.vladsch.flexmark.html.HtmlWriter;
import com.vladsch.flexmark.html.renderer.NodeRenderer;
import com.vladsch.flexmark.html.renderer.NodeRendererContext;
import com.vladsch.flexmark.html.renderer.NodeRendererFactory;
import com.vladsch.flexmark.html.renderer.NodeRenderingHandler;
import com.vladsch.flexmark.util.data.DataHolder;
import org.apache.commons.text.StringEscapeUtils;
import org.jetbrains.annotations.NotNull;

public class CodeBlockNodeRenderer implements NodeRenderer {

    private final boolean isReplaceButtonVisible;

    public CodeBlockNodeRenderer(boolean isReplaceButtonVisible) {
        this.isReplaceButtonVisible = isReplaceButtonVisible;
    }

    @Override
    public Set<NodeRenderingHandler<?>> getNodeRenderingHandlers() {
        return Set.of(new NodeRenderingHandler<>(FencedCodeBlock.class, this::render));
    }

    private void render(FencedCodeBlock node, NodeRendererContext context, HtmlWriter html) {
        var code = String.join("", node.getContentLines());


        html.attr("class", "code-header")
                .withAttr().tag("div")
                .attr("class", "lang")
                .withAttr().tag("span")
                .text(node.getInfo())
                .tag("/span")
                .attr("class", "actions")
                .withAttr().tag("div")
                .attr("class", "copy-button")
                .attr("onclick", String.format("window.JavaBridge.copyCode('%s')", StringEscapeUtils.escapeEcmaScript(code)))
                .withAttr().tag("button")
                .text("Copy")
                .tag("/button");
        if (isReplaceButtonVisible) {
            html.attr("class", "replace-button")
                    //.attr("disabled", "")导致初始化时不可用
                    .attr("title", "Please wait until the response has been generated")
                    .attr("onclick", String.format("window.JavaBridge.replaceCode('%s')", StringEscapeUtils.escapeEcmaScript(code)))
                    .withAttr().tag("button")
                    .text("Replace")
                    .tag("/button");
        }
        html.tag("/div")
                .tag("/div")
                .attr("class", "code-wrapper")
                .withAttr().tag("div");
        context.delegateRender();
        html.tag("/div");
    }

    public static class Factory implements NodeRendererFactory {

        private final boolean isReplaceButtonVisible;

        public Factory(boolean isReplaceButtonVisible) {
            super();
            this.isReplaceButtonVisible = isReplaceButtonVisible;
        }

        @NotNull
        @Override
        public NodeRenderer apply(@NotNull DataHolder options) {
            return new CodeBlockNodeRenderer(isReplaceButtonVisible);
        }
    }
}