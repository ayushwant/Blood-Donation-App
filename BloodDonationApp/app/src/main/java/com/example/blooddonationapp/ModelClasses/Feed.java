package com.example.blooddonationapp.ModelClasses;

import java.util.UUID;

public class Feed
{
    private String text; // heading
    private String image;
    private String link;
    private boolean liked;
    private boolean saved;

    private String uid = null; //Generates random UUID
    private String timeCreated = null;

    public Feed() {
    }

    public Feed(String text, String image, String link) {
        this.text = text;
        this.image = image;
        this.link = link;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uuid) {
        this.uid = uuid;
    }

    public String getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(String timeCreated) {
        this.timeCreated = timeCreated;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public boolean isSaved() {
        return saved;
    }

    public void setSaved(boolean saved) {
        this.saved = saved;
    }
}
