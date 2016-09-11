package com.mavericks.myocontroller.models;

/**
 * @author Anurag
 */
public class Message {
    public Message(String data, boolean isSpeaker) {
        this.data = data;
        this.isSpeaker = isSpeaker;
    }

    public String data;
    public boolean isSpeaker;
}
