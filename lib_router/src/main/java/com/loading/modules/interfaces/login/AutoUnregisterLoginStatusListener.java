package com.loading.modules.interfaces.login;

/**
 * Created by huazhang on 2018/1/25.
 * the default login status listener
 */
public class AutoUnregisterLoginStatusListener implements LoginStatusListener{
    @Override
    public void onLoginSuccess() {
        LoginModuleMgr.removeLoginStatusListener(this);
    }

    @Override
    public void onLoginCancel() {
        LoginModuleMgr.removeLoginStatusListener(this);
    }

    @Override
    public void onLogout(boolean isSuccess) {
        LoginModuleMgr.removeLoginStatusListener(this);
    }
}
