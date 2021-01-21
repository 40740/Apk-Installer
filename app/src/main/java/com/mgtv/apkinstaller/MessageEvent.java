package com.mgtv.apkinstaller;

/**
 * @author Mr.xw
 * @time 2021/1/15 16:38
 */
public class MessageEvent {
    private String message;

    public MessageEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
