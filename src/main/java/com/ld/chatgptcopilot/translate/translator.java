package com.ld.chatgptcopilot.translate;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

public interface translator {
    Result translate(Param param);
    String translate(String text, String from, String to);


    @Data
    class Result {
        public List<String> texts=new ArrayList<>();
        public String from;
        public String to;
    }

    @Data
    class Param {
        public List<String> texts = new ArrayList<>();
        public String from="en";
        public String to="zh-CHS";
    }
}
