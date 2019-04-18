package com.loading.modules;

import android.widget.Toast;

import com.loading.common.BuildConfig;
import com.loading.common.component.CApplication;
import com.loading.common.utils.Loger;
import com.loading.common.utils.SystemUtils;
import com.loading.common.utils.UiThreadUtil;
import com.loading.common.widget.TipsToast;

import java.util.HashMap;
import java.util.Map;

public class ModuleManager {
    public static final String TAG = "ModuleManager";

    private static Map<Class, IModuleInterface> REGISTERED_MODULE_MAP = new HashMap<>();

    public static void register(Class moduleInterface, IModuleInterface implementation) {
        if (moduleInterface != null && implementation != null) {
            Object currentImpl = REGISTERED_MODULE_MAP.get(moduleInterface);
            if (currentImpl != null) {
                if (currentImpl == implementation) {
                    Loger.w(TAG, moduleInterface + " is already registered....");
                } else if (BuildConfig.DEBUG) {
                    throw new IllegalStateException(moduleInterface + " should not be registered with another implementation...");
                } else {
                    Loger.w(TAG, moduleInterface + " is already registered, skip current register");
                }
            } else {
                REGISTERED_MODULE_MAP.put(moduleInterface, implementation);
                Loger.i(TAG, "-->register(), interface=" + moduleInterface.getSimpleName() + ", implementation=" + implementation);
            }
        }
    }

    public static void unregister(Class moduleInterface) {
        if (moduleInterface != null) {
            REGISTERED_MODULE_MAP.remove(moduleInterface);
        }
    }

    public static synchronized <T> T get(Class<T> interfaceClass) {
        T result = null;
        if (interfaceClass != null) {
            Object implObj = REGISTERED_MODULE_MAP.get(interfaceClass);
            if (implObj == null) {
                Loger.w(TAG, "---get()---, [" + interfaceClass.getSimpleName() + "] is not registered....");
                if (BuildConfig.DEBUG) {
                    if (SystemUtils.isMainProcess()) {
//                        throw new IllegalStateException("Can't find service by interface : " + interfaceClass.getSimpleName());
                        TipsToast.getInstance().showTipsError("Can't find service by interface : " + interfaceClass.getSimpleName());
                    } else {
                        UiThreadUtil.runOnUiThread(() -> Toast.makeText(CApplication.getAppContext(), "Warning: call module method in another process!", Toast.LENGTH_SHORT).show());
                    }
                }
            } else if (interfaceClass.isInstance(implObj)) {
                //noinspection unchecked
                result = (T) implObj;
            }
        }
        return result;
    }
}
