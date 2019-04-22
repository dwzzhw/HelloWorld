package com.loading.modules.interfaces.login;

import android.content.Context;
import android.content.Intent;

import com.loading.modules.IModuleInterface;
import com.loading.modules.annotation.Repeater;
import com.loading.modules.data.jumpdata.AppJumpParam;
import com.loading.modules.interfaces.login.data.UserInfo;
import com.loading.modules.interfaces.login.data.WXUserInfoPO;

import java.util.List;
import java.util.Map;

/**
 * the login module interface
 */
@Repeater
public interface ILoginService extends IModuleInterface {
    boolean isLogining();
    boolean isLogined();
    boolean isQQLogined();
    boolean isOpenQQLogined();
    boolean isOriginQQLogined();
    boolean isWxLogined();

    Map<String, String> getCookiesMap();
    String getCookie();

    void addLoginStatusListener(LoginStatusListener statusListener);
    void removeLoginStatusListener(LoginStatusListener statusListener);

    String getOpenId();
    String getQQNum();
    String getSkey();
    String getPayToken();
    String getCommonId(); //微信登录态的 unionId
    String getPf();

    String getUserLskey();
    String getVuid();
    String getSessionKey();
    String getAccessToken();
    String getUid();

    UserInfo getUserInfo();
    String getUserLogo();
    String getUserId();
    String getUserNickName();
    boolean isAdmin();

    int getVipStatus();
    String getVipStatusMark();
    boolean syncVipStatus(int nVipStatus);

    void startLoginActivity(Context context);
    void startLoginActivity(Context context, AppJumpParam jumpParam);
    void startLoginActivity(Context context, String switchLoginType, String titleReason, String subTitleReason);
    void startWxEntryActivity(Context context, int type);
    boolean onRefreshUserInfo(Object newDataObj, boolean isRefresh);
    void refreshLogin(ILoginRefreshListener refreshListener);
    void refreshLoginForLost(ILoginRefreshListener refreshListener);
    void checkAndRefreshLogin();
    void onLoginExpiredReLogin(Context context);
    boolean onQQLoginActivityResult(int requestCode, int resultCode, Intent data);
    boolean onUiResumeCancelWx();
    boolean isCanFastLoginQQ(boolean isShowTip);
    boolean checkWeixinInstalled(boolean showTip);
    void onWXLoginFail(int errCode, String msg, boolean isRefresh);
    void getWXTokenFromNet(final WXUserInfoPO.WXUserInfo wxUserInfo);
    void onLogout();
    void onLoginCancel();
}
