package com.loading.modules.interfaces.login;

/**
 * Created by yudongjin on 2018/1/17.
 */
public interface LoginStatusListener {
    void onLoginSuccess();
    void onLoginCancel();
    void onLogout(boolean isSuccess);
}
