package com.example.hp_lap.gorupchat;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.design.internal.ParcelableSparseArray;

/**
 * Created by hp-lap on 2/24/2018.
 */

public class MessageModel implements Parcelable {
    private String text;
    private String name;
    private String photoUrl;

    public MessageModel(){}

    public MessageModel(String text, String name, String photoUrl) {
        this.text = text;
        this.name = name;
        this.photoUrl = photoUrl;
    }

    protected MessageModel(Parcel in) {
        text = in.readString();
        name = in.readString();
        photoUrl = in.readString();
    }

    public static final Creator<MessageModel> CREATOR = new Creator<MessageModel>() {
        @Override
        public MessageModel createFromParcel(Parcel in) {
            return new MessageModel(in);
        }

        @Override
        public MessageModel[] newArray(int size) {
            return new MessageModel[size];
        }
    };

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(text);
        parcel.writeString(name);
        parcel.writeString(photoUrl);
    }
}
