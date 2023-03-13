package com.ld.chatgptcopilot.util.result;

public interface Result<T> {

    boolean isValid();


    T get();

}
