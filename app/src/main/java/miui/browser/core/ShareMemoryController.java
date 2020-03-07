package miui.browser.core;

import android.content.Context;
import android.os.Parcelable;

//别删Context参数
public class ShareMemoryController {

    public static String readString(Context context, String fdKey) {
        return (String) ShareMemoryContentProvider.call(ShareMemoryConstant.READ, null, fdKey);
    }

    public static String writeString(Context context, String data) {
        return (String) ShareMemoryContentProvider.call(ShareMemoryConstant.WRITE, null, data);

    }

    public static <T> T readData(Context context, String fdKey) {
        return (T) ShareMemoryContentProvider.call(ShareMemoryConstant.READ, null, fdKey);
    }

    public static <T> String writeData(Context context, T data) {
        return (String) ShareMemoryContentProvider.call(ShareMemoryConstant.WRITE, null, data);
    }

    //跨进程调用，目前暂不需要，关闭该接口
    public static <T extends Parcelable> T readDataWithMultiProcess(Context context, String fdKey) {
        throw new RuntimeException("doesn't support!");
    }

    public static <T extends Parcelable> String writeDataWithMultiProcess(Context context, T data) {
        throw new RuntimeException("doesn't support!");
    }

    public static void close(Context context, String fdKey) {
        ShareMemoryContentProvider.call(ShareMemoryConstant.CLOSE, null, fdKey);
    }
}
