package com.example.loading.helloworld.ipc;

import android.os.Parcel;
import android.os.Parcelable;

import com.loading.common.utils.Loger;

public class BookEntity implements Parcelable {
    public static final String TAG = "BookEntity_dwz";
    public String id;
    public String name;

    public BookEntity() {
    }

    public BookEntity(String id, String name) {
        this.id = id;
        this.name = name;
    }

    protected BookEntity(Parcel in) {
        readFromParcel(in);
//        Loger.d(TAG, "<--BookEntity.init from parcel, this=" + this);
    }

    public static final Creator<BookEntity> CREATOR = new Creator<BookEntity>() {
        @Override
        public BookEntity createFromParcel(Parcel in) {
            return new BookEntity(in);
        }

        @Override
        public BookEntity[] newArray(int size) {
            return new BookEntity[size];
        }
    };

    public void readFromParcel(Parcel in) {
        id = in.readString();
        name = in.readString();
//        Loger.d(TAG, "<--readFromParcel(), this=" + this);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Loger.d(TAG, "-->writeToParcel(), flags=" + flags);
        dest.writeString(id);
        dest.writeString(name);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "BookEntity{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
