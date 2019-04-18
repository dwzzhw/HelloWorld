package com.loading.modules.interfaces.login.data;


import com.loading.common.utils.CommonUtils;

import java.io.Serializable;

// public class WXUserInfoPO implements java.io.Serializable {
public class WXUserInfoPO implements Serializable {
    static final long serialVersionUID = -9072701187805339368L;
    private int code;
    private String msg;
    private WXUserInfo data;
    private String version;

    public static class WXUserInfo implements java.io.Serializable {
        static final long serialVersionUID = -9072701187805339369L;
        public String code;
        public String access_token;
        public String refresh_token;
        public String openid;
        public String scope;
        public String unionid;
        public int expires_in;

        private String errcode;
        private String errmsg;


        public String getErrmsg() {
            return errmsg;
        }

        public int getErrcode() {
            return CommonUtils.optInt(errcode, 0);
        }

        @Override
        public String toString() {
            return "code: " + code +
                    ", access_token: " + access_token +
                    ", refresh_token: " + refresh_token +
                    ", openid: " + openid +
                    ", unionId: " + unionid +
                    ", scope: " + scope;
        }
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public WXUserInfo getData() {
        return data;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
