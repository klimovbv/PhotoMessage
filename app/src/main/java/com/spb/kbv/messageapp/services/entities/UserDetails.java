package com.spb.kbv.messageapp.services.entities;

import android.os.Parcel;
import android.os.Parcelable;

public class UserDetails implements Parcelable{
    private final String id;
    private final boolean isContact;
    private final String displayName;
    private final String username;
    private final String avatarUrl;

    public UserDetails(String id, boolean isContact, String displayName, String username, String avatarUrl) {
        this.id = id;
        this.isContact = isContact;
        this.displayName = displayName;
        this.username = username;
        this.avatarUrl = avatarUrl;
    }

    private UserDetails(Parcel parcel){
        id = parcel.readString();
        isContact = parcel.readByte() == 1;
        displayName = parcel.readString();
        username = parcel.readString();
        avatarUrl = parcel.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel destination, int i) {
        destination.writeString(id);
        destination.writeByte((byte) (isContact ? 1 : 0));
        destination.writeString(displayName);
        destination.writeString(username);
        destination.writeString(avatarUrl);

    }

    public String getId() {
        return id;
    }

    public boolean isContact() {
        return isContact;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getUsername() {
        return username;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public static final Creator<UserDetails> CREATOR = new Creator<UserDetails>() {
        @Override
        public UserDetails createFromParcel(Parcel parcel) {
            return new UserDetails(parcel);
        }

        @Override
        public UserDetails[] newArray(int size) {
            return new UserDetails[size];
        }
    };
}
