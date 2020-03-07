package miui.browser.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

//ContentProvider
class ShareMemoryContentProvider {

    private static AtomicInteger sFdKey = new AtomicInteger();
    private static Map<String, Object> sUsedMemoryData = new ConcurrentHashMap<>();

    public static Object call(String method, String arg, Object extras) {
        Object result = null;
        switch (method) {
            case ShareMemoryConstant.READ:
                result = read(extras);
                break;
            case ShareMemoryConstant.WRITE:
                result = write(extras);
                break;
            case ShareMemoryConstant.CLOSE:
                result = close(extras);
                break;
        }
        return result;
    }

    private static Object read(Object extras) {
        if (!(extras instanceof String)) {
            throw new IllegalArgumentException("request data must be fdKey!");
        }
        String fdKey = (String) extras;
        return sUsedMemoryData.get(fdKey);
    }

    private static String write(Object extras) {
        String fdKey = generateKey();
        sUsedMemoryData.put(fdKey, extras);
        return fdKey;
    }

    private static boolean close(Object extras) {
        if (!(extras instanceof String)) {
            throw new IllegalArgumentException("request data must be fdKey!");
        }
        String fdKey = (String) extras;
        return sUsedMemoryData.remove(fdKey) != null;
    }

    private static String generateKey() {
        int key = sFdKey.getAndAdd(1);
        return String.valueOf(key);
    }
}
