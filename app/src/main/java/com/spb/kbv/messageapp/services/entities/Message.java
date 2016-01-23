package com.spb.kbv.messageapp.services.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class Message implements Parcelable{
    private String _id;
    private Calendar createdAt;
    private String longMessage;
    private String imageUrl;
    private UserDetails otherUser;
    private boolean isFromUs;
    private boolean isSelected;
    private boolean isRead;

    public Message(
            String id,
            Calendar createdAt,
            String longMessage,
            String imageUrl,
            UserDetails otherUser,
            boolean ifFromUs,
            boolean isRead) {
        this._id = id;
        this.createdAt = createdAt;
        this.longMessage = longMessage;
        this.imageUrl = imageUrl;
        this.otherUser = otherUser;
        this.isFromUs = ifFromUs;
        this.isRead = isRead;
    }

    private Message (Parcel parcel){
        _id = parcel.readString();
        createdAt = new GregorianCalendar();
        createdAt.setTimeInMillis(parcel.readLong());
        longMessage = parcel.readString();
        imageUrl = parcel.readString();
        otherUser = (UserDetails) parcel.readParcelable(UserDetails.class.getClassLoader());
        isFromUs = parcel.readByte() == 1;
        isRead = parcel.readByte() == 1;
    }

    @Override
    public void writeToParcel(Parcel destination, int flags) {
        destination.writeString(_id);
        destination.writeLong(createdAt.getTimeInMillis());
        destination.writeString(longMessage);
        destination.writeString(imageUrl);
        destination.writeParcelable(otherUser, 0);
        destination.writeByte((byte) (isFromUs ? 1 : 0));
        destination.writeByte((byte)(isRead ? 1 : 0));

    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getId() {
        return _id;
    }

    public Calendar getCreatedAt() {
        return createdAt;
    }

    public String getLongMessage() {
        return longMessage;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public UserDetails getOtherUser() {
        return otherUser;
    }

    public boolean isFromUs() {
        return isFromUs;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setIsRead(boolean isRead) {
        this.isRead = isRead;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public void setIsFromUs(boolean isFromUs) {
        this.isFromUs = isFromUs;
    }

    public static final Creator<Message> CREATOR = new Creator<Message>() {
        @Override
        public Message createFromParcel(Parcel source) {
            return new Message(source);
        }

        @Override
        public Message[] newArray(int size) {
            return new Message[size];
        }
    };
}
