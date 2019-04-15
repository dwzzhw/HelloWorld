package com.loading.modules;

public interface IModuleInterface {
    default void onCreate() { }
    default void onDestroy() { }
}

