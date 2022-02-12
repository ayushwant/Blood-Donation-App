package com.example.blooddonationapp.ModelClasses;

public class Message
{
    private String msgID, msg, senderID, imageUrl, documentUrl;
    private long timeStamp;
    boolean hasImageAttachment, hasDocument;

    public String getDocumentUrl() {
        return documentUrl;
    }

    public void setDocumentUrl(String documentUrl) {
        this.documentUrl = documentUrl;
        setHasDocument(true);
    }

    public boolean isHasDocument() {
        return hasDocument;
    }

    public void setHasDocument(boolean hasDocument) {
        this.hasDocument = hasDocument;
    }

    public boolean isHasImageAttachment() {
        return hasImageAttachment;
    }

    public void setHasImageAttachment(boolean hasImageAttachment) {
        this.hasImageAttachment = hasImageAttachment;
    }

    public Message() {
    }

    public Message(String msg, String senderID, long timeStamp) {
        this.msg = msg;
        this.senderID = senderID;
        this.timeStamp = timeStamp;
    }

    public String getMsgID() {
        return msgID;
    }

    public void setMsgID(String msgID) {
        this.msgID = msgID;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        this.hasImageAttachment = true;
    }
}
