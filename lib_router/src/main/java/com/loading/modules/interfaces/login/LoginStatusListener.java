package com.loading.modules.interfaces.login;

public interface LoginStatusListener {
    void onLoginSuccess();
    void onLoginCancel();
    void onLogout(boolean isSuccess);
}
