package com.loading.modules.interfaces.login.data;

import android.text.TextUtils;
import android.text.format.DateUtils;

import com.loading.common.utils.Loger;

import java.util.List;

@SuppressWarnings("unused")
public class UserInfo implements java.io.Serializable {
    private static final String TAG = "UserInfo";
    private static final long serialVersionUID = -9072701187805339358L;
    private static final String COOKIE_SEPERATOR = ";";

    private static final long PROTECT_REFRESH_INTERVAL = DateUtils.MINUTE_IN_MILLIS;
    private static final long PROTECT_STRONGLOGIN_REFRESH_INTERVAL = DateUtils.HOUR_IN_MILLIS;

    public int type = -1;
    private long expireTime = -1;
    private long mRefreshTime = -1;

    //qq info depreated fields
    public String uin = "";
    private String lskey;
    private String skey;
    private String a2key;

    //wx info
    private String pay_token;
    public String access_token;
    public String refresh_token;
    public String openid;
    private String scope;
    private String unionid;
    private String pf;

    //common info with both qq and wx
    public String nick;
    public String icon;
    public String uid = "";

    //vip info
    private int vip;
    private String vipSig; //VIP状态，分为不同的会员包
    public int pollInterval;
    public String endTime;
    public String vuid;
    public String sessionKey;

    private int isAdmin;

    public UserInfo() {
    }

    public int getVipStatus() {
        return vip;
    }

    public String getUin() {
        return uin;
    }

    public void setUin(String uin) {
        this.uin = uin;
    }

    public String getUid() {
        return !TextUtils.isEmpty(uid) ? uid : "";
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUnionId() {
        return unionid;
    }

    public void setPf(String pf) {
        this.pf = pf;
    }

    public String getPf() {
        return pf;
    }

    public boolean isAdmin() {
        return isAdmin == 1;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }


    public String getLskey() {
        return lskey;
    }

    public void setLskey(String lskey) {
        this.lskey = lskey;
    }

    public void setA2key(String a2key) {
        this.a2key = a2key;
    }

    private String getA2key() {
        return a2key;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void clearInfo() {
        type = -1;
        uin = null;
        uid = null;
        nick = null;
        icon = null;

        lskey = null;
        skey = null;
        a2key = null;

        pay_token = null;
        access_token = null;
        refresh_token = null;
        openid = null;
        scope = null;
        unionid = null;

        mRefreshTime = -1L;
        expireTime = -1L;

        vip = 0;
        vipSig = null;
        endTime = null;
        pollInterval = 0;
        vuid = null;
        sessionKey = null;
    }

    public void setExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }

    public void setmRefreshTime(long tExpireTime) {
        mRefreshTime = tExpireTime;
    }

    public long getmRefreshTime() {
        return mRefreshTime;
    }

    public boolean isNeedRefresh() {
        boolean isNeedRefresh = false;
        Loger.d(TAG, "mRefreshTime: " + mRefreshTime + ", currTime: " + System.currentTimeMillis());
        if (mRefreshTime - System.currentTimeMillis() < PROTECT_REFRESH_INTERVAL) {
            isNeedRefresh = true;
        }
        return isNeedRefresh;
    }

    public boolean isStrongLoginNeedRefresh() {
        boolean isRefresh = false;
        if (mRefreshTime - System.currentTimeMillis() <
                PROTECT_STRONGLOGIN_REFRESH_INTERVAL) {
            isRefresh = true;
        }
        return isRefresh;
    }

    public boolean isExpired() {
        boolean isExpired = false;
        if (expireTime > 0) {
            Loger.d(TAG, "expireTime : " + expireTime + ", currentTime: " + System.currentTimeMillis());
            isExpired = ((expireTime - System.currentTimeMillis()) < PROTECT_REFRESH_INTERVAL);
        }
        return isExpired;
    }

    public String getVUid() {
        return vuid;
    }

    public boolean isVip() {
        return vip > 0;
    }

    public String getVipSig() {
        return vipSig;
    }

    public String getSkey() {
        return skey;
    }

    public void setSkey(String skey) {
        this.skey = skey;
    }

    public void syncFromFastQQ(UserInfo tUserInfo) {
        if (tUserInfo != null) {
            if ( !TextUtils.isEmpty(tUserInfo.getUin()) ) {
                setUin(tUserInfo.getUin());
            }

            if ( !TextUtils.isEmpty(tUserInfo.getNick()) ) {
                setNick(tUserInfo.getNick());
            }

            if ( !TextUtils.isEmpty(tUserInfo.getIcon()) ) {
                setIcon(tUserInfo.getIcon());
            }

            if ( !TextUtils.isEmpty(tUserInfo.getA2key()) ) {
                setA2key(tUserInfo.getA2key());
            }

            if ( !TextUtils.isEmpty(tUserInfo.getLskey()) ) {
                setLskey(tUserInfo.getLskey());
            }

            if ( !TextUtils.isEmpty(tUserInfo.getSkey()) ) {
                setSkey(tUserInfo.getSkey());
            }

            if ( !TextUtils.isEmpty(tUserInfo.openid) ) {
                setOpenid(tUserInfo.openid);
            }

            if ( !TextUtils.isEmpty(tUserInfo.access_token) ) {
                setAccess_token(tUserInfo.access_token);
            }

            if ( !TextUtils.isEmpty(tUserInfo.pay_token) ) {
                setPayToken(tUserInfo.pay_token);
            }

            if ( !TextUtils.isEmpty(tUserInfo.pf) ) {
                setPf(tUserInfo.pf);
            }

            setExpireTime(tUserInfo.expireTime);
            if ( tUserInfo.mRefreshTime > 0L ) {
                setmRefreshTime(tUserInfo.mRefreshTime);
            }
        }
    }

    public boolean syncFromOwnLogin(UserInfo tUserInfo) {
        boolean isVipChange = false;
        if (tUserInfo != null) {
            //type must not be synced or error happened
            setNick(tUserInfo.getNick());
            setIcon(tUserInfo.getIcon());
            setUid(tUserInfo.getUid());

            isVipChange = isVipChange(tUserInfo.vip, tUserInfo.vipSig);
            vip = tUserInfo.vip;
            vipSig = tUserInfo.vipSig;

            endTime = tUserInfo.endTime;
            vuid = tUserInfo.vuid;
            sessionKey = tUserInfo.sessionKey;
            isAdmin = tUserInfo.isAdmin;
            if (tUserInfo.pollInterval > 0) {
                pollInterval = tUserInfo.pollInterval;
            }
        }
        return isVipChange;
    }

    public void syncFromFastWX(WXUserInfoPO.WXUserInfo wxUserInfo) {
        if (wxUserInfo != null) {
            this.access_token = wxUserInfo.access_token;
            this.refresh_token = wxUserInfo.refresh_token;
            this.openid = wxUserInfo.openid;
            this.scope = wxUserInfo.scope;
            this.unionid = wxUserInfo.unionid;
            long tExpiresInMs = wxUserInfo.expires_in * DateUtils.SECOND_IN_MILLIS;
            if (tExpiresInMs <= 0) {
                tExpiresInMs = 2 * DateUtils.HOUR_IN_MILLIS;//2小时
            }
            setmRefreshTime(System.currentTimeMillis() + tExpiresInMs);
        }
    }

    private boolean isVipChange(int nVip, String nVipSig) {
        return vip != nVip || !TextUtils.equals(vipSig, nVipSig);
    }

    public boolean syncVipStatus(int nVipStatus) {
        boolean isVipChanged = false;
        if (vip != nVipStatus) {
            vip = nVipStatus;
            isVipChanged = true;
        }
        return isVipChanged;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    @Override
    public String toString() {
        String str = "uin = " + uin;
        str += ", uid = " + uid;
        str += ", nick = " + nick;
        str += ", icon = " + icon;
        str += ", openid = " + openid;
        str += ", access_token = " + access_token;
        str += ", refresh_token = " + refresh_token;
        str += ", lskey = " + lskey;
        str += ", skey  = " + skey;
        str += ", a2key = " + a2key;
        str += ", mRefreshTime = " + mRefreshTime;
        str += ", expireTime = " + expireTime;
        str += ", loginType = " + type;
        str += ", vip = " + vip;
        str += ", vipSig = " + vipSig;
        str += ", endTime = " + endTime;
        str += ", pollInterval = " + pollInterval;
        str += ", vuid = " + vuid;
        str += ", sessionKey = " + sessionKey;
        return str;
    }

//    public void setGetKb(String getKb) {
//        this.getKb = getKb;
//    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public void setPayToken(String payToken) {
        this.pay_token = payToken;
    }

    public String getPayToken() {
        return pay_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }

    public void setVip(int vip) {
        this.vip = vip;
    }

    public void setVipSig(String vipSig) {
        this.vipSig = vipSig;
    }

    public void setPollInterval(int pollInterval) {
        this.pollInterval = pollInterval;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setVuid(String vuid) {
        this.vuid = vuid;
    }

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }

    public void setIsAdmin(int isAdmin) {
        this.isAdmin = isAdmin;
    }
}