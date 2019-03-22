package com.example.loading.helloworld.gson;

import android.content.Context;
import android.util.Log;

import com.loading.common.utils.Utils;
import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;

/**
 * Created by loading on 3/14/17.
 */

public class GsonTest {
    private static final String TAG = "GsonTest";

    public static final void testGson(Context context) {
        String jsonStr = Utils.readAssertResource(context, "myJson.txt");
        Gson gson = new Gson();

        GsonDataPO resultData = gson.fromJson(jsonStr, GsonDataPO.class);
        Log.i(TAG, "-->testGson(), resultData=" + resultData);
    }


    public static class GsonDataPO {
        public int code;
        public List<GsonItem> data;
//        public GsonItem data;

        @Override
        public String toString() {
            return "GsonDataPO{" +
                    "code=" + code +
                    ", data=" + data +
                    '}';
        }
    }

    public static class GsonItem {
        public String id;
        public String text;
        public float[] geo;
        public UserInfo user;

        @Override
        public String toString() {
            return "GsonItem{" +
                    "id='" + id + '\'' +
                    ", text='" + text + '\'' +
                    ", geo=" + Arrays.toString(geo) +
                    ", user=" + user +
                    '}';
        }
    }

    public static class UserInfo {
        public String name;
        public int followers_count;

        @Override
        public String toString() {
            return "UserInfo{" +
                    "name='" + name + '\'' +
                    ", followers_count=" + followers_count +
                    '}';
        }
    }
}
