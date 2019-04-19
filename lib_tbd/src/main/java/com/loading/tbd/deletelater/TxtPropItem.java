package com.loading.deletelater;

import android.graphics.Color;
import android.text.TextUtils;


import com.example.loading.helloworld.R;
import com.loading.common.component.CApplication;
import com.loading.common.utils.CommonUtil;
import com.loading.common.utils.Loger;

import java.io.Serializable;

public class TxtPropItem implements Serializable {
    private static final long serialVersionUID = 4979564670020215219L;
    private static final String TAG = "TxtPropItem";
    public static final String TXT_PROP_TYPE_COLOR = "201";  //文字颜色
    public static final String TXT_PROP_TYPE_BG = "202";     //文字背景
    public static final String TXT_PROP_TYPE_EE = "203";     //进入特效

    public static final String PROP_STATUS_AVAILABLE = "1";     //可以使用
    public static final String PROP_STATUS_RUN_OUT = "2";       //没有道具
    public static final String PROP_STATUS_LOCKED = "3";        //没有权限（目前指不是会员）

    private String id;
    private String type;    //效果类型：1文字，2背景，3字体，4动画
    private String img;     //特效图片
    private String lockStatus;  //特效状态：1可以使用，2没有道具，3没有权限（目前指不是会员）

    private PropLockInfo lockInfo;
    private String num;     //剩余数量
    private Param params;   //预览参数
    private String postfix; //背景模板上展示的后缀内容

    public String getBossSource() {
        String source = null;
        switch (type) {
            case TXT_PROP_TYPE_EE:
                source = "1";
                break;
            case TXT_PROP_TYPE_COLOR:
                source = "3";
                break;
            case TXT_PROP_TYPE_BG:
                source = "2";
                break;
        }
        return source;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public String getImg() {
        return img;
    }

    public int getNum() {
        return CommonUtil.optInt(num, -1);
    }

    public Param getParams() {
        return params;
    }

    private TxtPropItem(String propType, String propId) {
        this.id = propId;
        this.type = propType;
//        //add test data for ui debug
//        num = String.valueOf((int) (Math.random() * 4));
//        double random = Math.random();
//        if (random < 0.5) {
//            lockStatus = PROP_STATUS_AVAILABLE;
//        } else if (random < 0.8) {
//            lockStatus = PROP_STATUS_RUN_OUT;
//        } else {
//            lockStatus = PROP_STATUS_LOCKED;
//        }
    }

    public static TxtPropItem newInstance(String propType, String propName) {
        return new TxtPropItem(propType, propName);
    }

    public static TxtPropItem newInstance(String propType, String propName, String bgUrl) {
        TxtPropItem item = new TxtPropItem(propType, propName);
        item.params = new Param();
        item.params.backgroundUrl = bgUrl;
        return item;
    }

    public boolean isEnterEffectType() {
        return TXT_PROP_TYPE_EE.equals(type);
    }

    public boolean isBGType() {
        return TXT_PROP_TYPE_BG.equals(type);
    }

    public boolean isColorType() {
        return TXT_PROP_TYPE_COLOR.equals(type);
    }

    public int getSelectedBorderColor() {
        int color = 0;
        if (params != null && !TextUtils.isEmpty(params.color)) {
            if (params.color.length() == 9 && params.color.startsWith("#")) {
                color = Color.parseColor("#" + params.color.substring(3));
            } else {
                color = Color.parseColor(params.color);
            }
        } else {
            if (isEnterEffectType()) {
                color = CApplication.getColorFromRes(R.color.blue_primary);
            } else {
                color = CApplication.getColorFromRes(R.color.red);
            }
        }
        return color;
    }

    public int getParamColorValue() {
        int color = 0;
        if (params != null && !TextUtils.isEmpty(params.color)) {
            try {
                color = Color.parseColor(params.color);
            } catch (Exception e) {
                Loger.w(TAG, "-->getParamColorValue() fail, ", e);
            }
        }
        return color;
    }

    public boolean isLocked() {
        return PROP_STATUS_LOCKED.equals(lockStatus);
    }

    /**
     * 需要计次的道具，当前可用数量为0
     *
     * @return
     */
    public boolean isRunningOut() {
        boolean isRunningOut = false;
        switch (type) {
            case TXT_PROP_TYPE_EE:
                isRunningOut = PROP_STATUS_RUN_OUT.equals(lockStatus) || getNum() <= 0;
                break;
            case TXT_PROP_TYPE_COLOR:
            case TXT_PROP_TYPE_BG:
                //此类道具忽略具体数量，仅根据状态判断
                isRunningOut = PROP_STATUS_RUN_OUT.equals(lockStatus);
                break;
        }
        return isRunningOut;
    }

    public PropLockInfo getLockInfo() {
        return lockInfo;
    }

    public String getPreviewBgUrl() {
        return params != null ? params.backgroundUrl : null;
    }

    public boolean isTheSameItem(TxtPropItem propItem) {
        return propItem != null
                && TextUtils.equals(type, propItem.type)
                && TextUtils.equals(id, propItem.id);
    }

    public String getContentSuffix() {
        return postfix;
    }

    public static class Param implements Serializable {
        private static final long serialVersionUID = -3793542109454866524L;
        public String color;            //如 #B6985A
        public String backgroundUrl;
    }
}
