package com.gaucow.betterbartersystem.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.Timestamp;

import java.util.List;

public class Listing implements Parcelable {
    private String userName;
    private List<String> bookAndAuthorName;
    private Timestamp timestamp;
    private String userEmail;
    private String docID;
    private int bookType;
    private long bookID;
    private long isbn;

    protected Listing(Parcel in) {
        userName = in.readString();
        bookAndAuthorName = in.createStringArrayList();
        timestamp = in.readParcelable(Timestamp.class.getClassLoader());
        userEmail = in.readString();
        docID = in.readString();
        bookType = in.readInt();
        bookID = in.readLong();
        isbn = in.readLong();
    }

    public static final Creator<Listing> CREATOR = new Creator<Listing>() {
        @Override
        public Listing createFromParcel(Parcel in) {
            return new Listing(in);
        }

        @Override
        public Listing[] newArray(int size) {
            return new Listing[size];
        }
    };

    public int getBookType() {
        return bookType;
    }

    public void setBookType(int bookType) {
        this.bookType = bookType;
    }

    public Listing(String userName, List<String> bookAndAuthorName, Timestamp timestamp, String userEmail, String docID, int bookType, long bookID, long isbn) {
        this.userName = userName;
        this.bookAndAuthorName = bookAndAuthorName;
        this.timestamp = timestamp;
        this.userEmail = userEmail;
        this.docID = docID;
        this.bookType = bookType;
        this.bookID = bookID;
        this.isbn = isbn;
    }

    public Listing() {}

    public String getDocID() {
        return docID;
    }

    public void setDocID(String docID) {
        this.docID = docID;
    }

    public String getName() {
        return userName;
    }

    public void setName(String name) {
        this.userName = name;
    }

    public Timestamp getTimeStamp() {
        return timestamp;
    }

    public void setTimeStamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public void setBookAndAuthorName(List<String> bookAndAuthorName) {
        this.bookAndAuthorName = bookAndAuthorName;
    }

    public List<String> getBookAndAuthorName() {
        return bookAndAuthorName;
    }

    public long getBookID() {
        return bookID;
    }

    public void setBookID(long bookID) {
        this.bookID = bookID;
    }

    public long getIsbn() {
        return isbn;
    }

    public void setIsbn(long isbn) {
        this.isbn = isbn;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userName);
        dest.writeStringList(bookAndAuthorName);
        dest.writeParcelable(timestamp, flags);
        dest.writeString(userEmail);
        dest.writeString(docID);
        dest.writeInt(bookType);
        dest.writeLong(bookID);
        dest.writeLong(isbn);
    }
}
