package com.example.loading.helloworld;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by loading on 4/21/16.
 */
public class MyTestParcel2 implements Parcelable {
    private static final String TAG = "MyTestParcel";
    public int data;

    public MyTestParcel2(int data) {
        this.data = data;
    }

    protected MyTestParcel2(Parcel in) {
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(data);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MyTestParcel2> CREATOR = new Creator<MyTestParcel2>() {
        @Override
        public MyTestParcel2 createFromParcel(Parcel in) {
            MyTestParcel2 parcel = new MyTestParcel2(in);
            parcel.data = in.readInt();
            return  parcel;
        }

        @Override
        public MyTestParcel2[] newArray(int size) {
            return new MyTestParcel2[size];
        }
    };

    @Override
    public String toString() {
        return "MyTestParcel{" +
                "data=" + data +
                '}';
    }
}
