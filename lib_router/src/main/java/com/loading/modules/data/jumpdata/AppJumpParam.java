package com.loading.modules.data.jumpdata;

import android.text.TextUtils;

import com.loading.common.utils.Loger;

import java.io.Serializable;
import java.util.Map;
import java.util.Properties;

/**
 * Created by Mr. Orange on 16/1/15.
 * the app jump param
 */
@SuppressWarnings("WeakerAccess")
public final class AppJumpParam implements Serializable, Cloneable {
    private static final String TAG = "AppJumpParam";
    private static final long serialVersionUID = 1463755036671395608L;
    /**
     * 各个页面解析Intent所统一使用的Key值
     * 此处的Key需要与Param里的字段名保持一致
     */
    public static final String EXTRA_KEY_PAGE_TYPE = "pageType";

    public static final String EXTRA_KEY_PARAM_MAP = "jumpParamMap"; //所有参数打包作为Map整体传递
    public static final String EXTRA_KEY_JUMPDATA = "jumpdata";
    public static final String EXTRA_KEY_URL = "url";
    public static final String EXTRA_KEY_TITLE = "title";
    public static final String EXTRA_KEY_COLUMN_ID = "columnId";
    public static final String EXTRA_KEY_COMPETITION_ID = "competitionId";
    public static final String EXTRA_KEY_COMPETITION_NAME = "competitionName";
    public static final String EXTRA_KEY_IS_RANK = "isRank";
    public static final String EXTRA_KEY_TAB = "tab";
    public static final String EXTRA_KEY_SUB_TAB = "subTab";
    public static final String EXTRA_KEY_MID = "mid";
    public static final String EXTRA_KEY_MIDS = "mids";
    public static final String EXTRA_KEY_TOPIC_ID = "topicId";
    public static final String EXTRA_KEY_TOPIC_REPLY_ID = "replyId";
    public static final String EXTRA_KEY_ATYPE = "atype";
    public static final String EXTRA_KEY_ID = "id";
    public static final String EXTRA_KEY_CID = "cid";
    public static final String EXTRA_KEY_VID = "vid";
    public static final String EXTRA_KEY_PROGRAMID = "programdId";
    public static final String EXTRA_KEY_IS_LIVE_VIDEO = "isLiveVideo";
    public static final String EXTRA_KEY_LIVE_ID = "liveId";
    public static final String EXTRA_KEY_MODULE_ID = "moduleId";
    public static final String EXTRA_KEY_FROM = "from";
    public static final String EXTRA_KEY_QUIT_TO_HOME = "isExtQuitToHome";
    public static final String EXTRA_KEY_EVENT = "event";
    public static final String EXTRA_KEY_SERVICE_ID = "serviceId"; //for VIPBuyAcitivity
    public static final String EXTRA_KEY_PACKAGE_ID = "packageId";
    public static final String EXTRA_KEY_IS_TEAM_SEL = "teamSelect"; //if buy team
    public static final String EXTRA_KEY_CALLBACK_NAME = "callbackName";
    public static final String EXTRA_KEY_SETID = "setId";
    public static final String EXTRA_KEY_UPLOAD = "uploadKey";
    public static final String EXTRA_KEY_REPORT_INFO = "report";
    public static final String EXTRA_KEY_TARGET_CODE = "targetCode";
    public static final String EXTRA_KEY_ROOM_VID = "roomVid";
    public static final String EXTRA_KEY_ROOM_SWITCH = "switchChatRoom";
    public static final String EXTRA_KEY_NEWS_ID = "newsId";
    public static final String EXTRA_KEY_CATEGORY = "category";
    public static final String EXTRA_KEY_FIRST_ITEM_ID = "firstId";
    public static final String EXTRA_KEY_SOURCE_TYPE = "sourceType";
    public static final String EXTRA_KEY_SOURCE_ID = "sourceId"; //ar play file url
    public static final String EXTRA_KEY_LOCATE_COMMENT = "isLocateComment";
    public static final String EXTRA_KEY_MINIPROGRAM_SOURCE_ID = "sourceId";
    public static final String EXTRA_KEY_MINIPROGRAM_JUMP_PATH = "jumpPath";
    public static final String EXTRA_KEY_SCHEME = "scheme";
    public static final String EXTRA_KEY_PACKAGE_NAME = "packageName";
    public static final String EXTRA_KEY_NEED_LOGIN = "needLogin";
    public static final String EXTRA_KEY_ITEM_ID = "itemId";
    public static final String EXTRA_KEY_GROUP_ID = "groupId";
    public static final String EXTRA_KEY_COVER_URL = "coverUrl";
    public static final String EXTRA_KEY_SHOWLOADING = "showLoading";
    public static final String EXTRA_KEY_FULL_SCREEN = "fullScreen";

    //这个参数用来中转老的203到204或205，临时方案，待老的203不再使用时可移除
    public static final String EXTRA_KEY_IMMERSE_TYPE = "immerseType";
    public static final String EXTRA_KEY_NEED_LOADING = "needLoading";
    public static final String EXTRA_KEY_SHOW_WHEN_COMPLETE = "showWhenComplete";
    public static final String EXTRA_KEY_PAGE_TAB = "pageTab";
    //搜索
    public static final String EXTRA_KEY_SEARCH_KEY = "searchKey";

    public static final String EXTRA_KEY_BBS_MESSAGE_FRAG_TAG = "bbsMessageFragTag";
    public static final String EXTRA_KEY_BBS_MESSAGE_ID = "bbsMessageId";
    public static final String EXTRA_KEY_BBS_MESSAGE_TYPE = "bbsMessageType";
    public static final String EXTRA_KEY_BBS_MESSAGE_DELETE_REPLY = "bbsMessageDeleteReply";

    private AppJumpParam() {
    }

    public int type = -1;
    public JumpParam param;

    public JumpParam getParam() {
        return param;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        AppJumpParam appJumpParam = (AppJumpParam) super.clone();
        if (appJumpParam != null) {
            appJumpParam.param = (param != null ? (JumpParam) param.clone() : null);
        }
        return appJumpParam;
    }

    public AppJumpParam cloneInstance() {
        AppJumpParam appJumpParam = null;
        try {
            appJumpParam = (AppJumpParam) clone();
        } catch (Exception e) {
            Loger.e(TAG, "exception: " + e);
        }
        return appJumpParam;
    }

    @Override
    public String toString() {
        return "AppJumpParam{" +
                "type=" + type +
                ", param=" + param +
                '}';
    }

    public static AppJumpParam newInstance() {
        AppJumpParam resultParam = new AppJumpParam();
        resultParam.makeParamNotEmpty();
        return resultParam;
    }

    public static AppJumpParam newInstance(int type) {
        AppJumpParam resultParam = new AppJumpParam();
        resultParam.type = type;
        resultParam.makeParamNotEmpty();
        return resultParam;
    }

    public void putParam(String key, String value) {
        if (!TextUtils.isEmpty(key)) {
            makeParamNotEmpty();
            param.put(key, value);
        }
    }

    public void putParam(String key, int value) {
        if (!TextUtils.isEmpty(key)) {
            makeParamNotEmpty();
            param.put(key, value);
        }
    }

    public void putParam(String key, boolean value) {
        if (!TextUtils.isEmpty(key)) {
            makeParamNotEmpty();
            param.put(key, value);
        }
    }

    public void removeParam(String key) {
        if (!TextUtils.isEmpty(key) && param != null) {
            param.remove(key);
        }
    }

    public String getJumpCallbackName() {
        return param != null ? param.getCallbackName() : null;
    }

    public String getUrl() {
        return param != null ? param.getUrl() : null;
    }

    public String getPageType() {
        return param != null ? param.getPageType() : null;
    }

    public AppJumpParam cloneCopy() {
        AppJumpParam cloneItem = AppJumpParam.newInstance();
        cloneItem.type = this.type;
        if (this.param != null) {
            for (String paramItemKey : param.keySet()) {
                cloneItem.param.put(paramItemKey, param.get(paramItemKey));
            }
        }
        return cloneItem;
    }

    public String getParamFrom() {
        return param != null ? param.getFrom() : null;
    }

    public void setParamFrom(String from) {
        makeParamNotEmpty();
        param.setFrom(from);
    }

    public String getMatchId() {
        return param != null ? param.getMid() : null;
    }

    public void setMid(String mid) {
        makeParamNotEmpty();
        param.setMid(mid);
    }

    public void setVid(String vid) {
        makeParamNotEmpty();
        param.setVid(vid);
    }

    public void setCid(String cid) {
        makeParamNotEmpty();
        param.setCid(cid);
    }

    public void setLiveId(String liveId) {
        makeParamNotEmpty();
        param.setLiveId(liveId);
    }

    public void setTabType(String tabType) {
        makeParamNotEmpty();
        param.setTab(tabType);
    }

    public void setNewsId(String newsId) {
        makeParamNotEmpty();
        param.setId(newsId);
    }

    public void setNewsAtype(String atype) {
        makeParamNotEmpty();
        param.setAtype(atype);
    }

    public void setUrl(String url) {
        makeParamNotEmpty();
        param.setUrl(url);
    }

    public void setTitle(String title) {
        makeParamNotEmpty();
        param.setTitle(title);
    }

    public void setModuleId(String moduleId) {
        makeParamNotEmpty();
        param.setModuleId(moduleId);
    }

    public void setTopicId(String topicId) {
        makeParamNotEmpty();
        param.setTopicId(topicId);
    }

    public void setBbsHandleMsgId(String msgId) {
        makeParamNotEmpty();
        param.setBbsHandleMsgId(msgId);
    }

    public void setBbsHandleMsgType(String msgType) {
        makeParamNotEmpty();
        param.setBbsHandleMsgType(msgType);
    }

    public void setBbsHandleMsgFragType(String msgFragType) {
        makeParamNotEmpty();
        param.setBbsHandleMsgFragType(msgFragType);
    }

    private void makeParamNotEmpty() {
        if (param == null) {
            param = new JumpParam();
        }
    }

    public String getParamTitle() {
        return param == null ? null : param.getTitle();
    }

    public String getTabType() {
        return param != null ? param.getTab() : null;
    }

    public static void putJumpParamToProperty(AppJumpParam appJumpParam, Properties properties) {
        //上报jumpdata内带的所有参数
        JumpParam jumpParam = appJumpParam != null ? appJumpParam.getParam() : null;
        if (properties != null && jumpParam != null && jumpParam.size() > 0) {
            for (Map.Entry<String, Object> entry : jumpParam.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                if (key != null && value != null) {
                    properties.put(key, value);
                }
            }
            properties.put(EXTRA_KEY_PAGE_TYPE, appJumpParam.type);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AppJumpParam that = (AppJumpParam) o;

        if (type != that.type) return false;
        return param != null ? param.equals(that.param) : that.param == null;
    }

    @Override
    public int hashCode() {
        int result = type;
        result = 31 * result + (param != null ? param.hashCode() : 0);
        return result;
    }
}