package com.example.blooddonationapp.ModelClasses;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

public class User implements Parcelable {
    private String name;
    private String phone;
    private String email;
    private String uid;
    private String imgUri;
    private String dob;
    private String bloodGrp;
    private String address;
    private String LatLng;

    private Map<String, Boolean> likedFeeds;
    private Map<String, Boolean> savedFeeds;

    protected User(Parcel in) {
        name = in.readString();
        phone = in.readString();
        email = in.readString();
        uid = in.readString();
        imgUri = in.readString();
        dob = in.readString();
        bloodGrp = in.readString();
        address = in.readString();
        LatLng = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getLatLng() {
        return LatLng;
    }

    public void setLatLng(String latLng) {
        LatLng = latLng;
    }

    public Map<String, Boolean> getLikedFeeds() {
        return likedFeeds;
    }

    public void setLikedFeeds(Map<String, Boolean> likedFeeds) {
        this.likedFeeds = likedFeeds;
    }

    public Map<String, Boolean> getSavedFeeds() {
        return savedFeeds;
    }

    public void setSavedFeeds(Map<String, Boolean> savedFeeds) {
        this.savedFeeds = savedFeeds;
    }

    public User(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getImgUri() {
        return imgUri;
    }

    public void setImgUri(String imgUri) {
        this.imgUri = imgUri;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getBloodGrp() {
        return bloodGrp;
    }

    public void setBloodGrp(String bloodGrp) {
        this.bloodGrp = bloodGrp;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(phone);
        parcel.writeString(email);
        parcel.writeString(uid);
        parcel.writeString(imgUri);
        parcel.writeString(dob);
        parcel.writeString(bloodGrp);
        parcel.writeString(address);
        parcel.writeString(LatLng);
    }
}
