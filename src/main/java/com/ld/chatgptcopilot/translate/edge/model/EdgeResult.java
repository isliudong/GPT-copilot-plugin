package com.ld.chatgptcopilot.translate.edge.model;

import java.util.List;

import lombok.Data;

@Data
public class EdgeResult {
    private List<EdgeTranslation> translations;
}
