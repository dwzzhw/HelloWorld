package com.loading.modules.data.jumpdata;

import android.text.TextUtils;

import java.io.Serializable;
import java.util.HashMap;

public class JumpParam extends HashMap<String, Object> implements Serializable, Cloneable {
    private static final long serialVersionUID = -4397407763626880661L;

    public JumpParam() {
        super();
    }

    private String getValueAsString(String key) {
        Object valueObj = !TextUtils.isEmpty(key) ? get(key) : null;
        return valueObj instanceof String ? (String) valueObj : null;
    }

    public String getUrl() {
        return getValueAsString(AppJumpParam.EXTRA_KEY_URL);
    }

    public String getScheme() {
        return getValueAsString(AppJumpParam.EXTRA_KEY_SCHEME);
    }

    public String getPackageName() {
        return getValueAsString(AppJumpParam.EXTRA_KEY_PACKAGE_NAME);
    }

    public String getPageType() {
        return getValueAsString(AppJumpParam.EXTRA_KEY_PAGE_TYPE);
    }

    public String getTitle() {
        return getValueAsString(AppJumpParam.EXTRA_KEY_TITLE);
    }

    public void setTitle(String title) {
        put(AppJumpParam.EXTRA_KEY_TITLE, title);
    }

    public String getColumnId() {
        return getValueAsString(AppJumpParam.EXTRA_KEY_COLUMN_ID);
    }

    public String getCompetitionId() {
        return getValueAsString(AppJumpParam.EXTRA_KEY_COMPETITION_ID);
    }

    public String getTab() {
        return getValueAsString(AppJumpParam.EXTRA_KEY_TAB);
    }

    public void setTab(String tab) {
        put(AppJumpParam.EXTRA_KEY_TAB, tab);
    }

    public String getMid() {
        return getValueAsString(AppJumpParam.EXTRA_KEY_MID);
    }

    public void setMid(String mid) {
        put(AppJumpParam.EXTRA_KEY_MID, mid);
    }

    public String getTopicId() {
        return getValueAsString(AppJumpParam.EXTRA_KEY_TOPIC_ID);
    }

    public void setTopicId(String topicId) {
        put(AppJumpParam.EXTRA_KEY_TOPIC_ID, topicId);
    }

    public String getAtype() {
        return getValueAsString(AppJumpParam.EXTRA_KEY_ATYPE);
    }

    public void setAtype(String aType) {
        put(AppJumpParam.EXTRA_KEY_ATYPE, aType);
    }

    public String getId() {
        return getValueAsString(AppJumpParam.EXTRA_KEY_ID);
    }

    public void setId(String id) {
        put(AppJumpParam.EXTRA_KEY_ID, id);
    }

    public String getCid() {
        return getValueAsString(AppJumpParam.EXTRA_KEY_CID);
    }

    public void setCid(String cid) {
        put(AppJumpParam.EXTRA_KEY_CID, cid);
    }

    public String getVid() {
        return getValueAsString(AppJumpParam.EXTRA_KEY_VID);
    }

    public void setVid(String vid) {
        put(AppJumpParam.EXTRA_KEY_VID, vid);
    }

    public String getLiveId() {
        return getValueAsString(AppJumpParam.EXTRA_KEY_LIVE_ID);
    }

    public void setLiveId(String liveId) {
        put(AppJumpParam.EXTRA_KEY_LIVE_ID, liveId);
    }

    public String getModuleId() {
        return getValueAsString(AppJumpParam.EXTRA_KEY_MODULE_ID);
    }

    public void setModuleId(String moduleId) {
        put(AppJumpParam.EXTRA_KEY_MODULE_ID, moduleId);
    }

    public String getServiceId() {
        return getValueAsString(AppJumpParam.EXTRA_KEY_SERVICE_ID);
    }

    public String getCallbackName() {
        return getValueAsString(AppJumpParam.EXTRA_KEY_CALLBACK_NAME);
    }

    public String getFrom() {
        return getValueAsString(AppJumpParam.EXTRA_KEY_FROM);
    }

    public String getQuitToHome() {
        return getValueAsString(AppJumpParam.EXTRA_KEY_QUIT_TO_HOME);
    }

    public void setFrom(String from) {
        put(AppJumpParam.EXTRA_KEY_FROM, from);
    }

    public String getSetId() {
        return getValueAsString(AppJumpParam.EXTRA_KEY_SETID);
    }

    public String getTargetCode() {
        return getValueAsString(AppJumpParam.EXTRA_KEY_TARGET_CODE);
    }

    public boolean isNeedLogin() {
        String needLogin = getValueAsString(AppJumpParam.EXTRA_KEY_NEED_LOGIN);
        return TextUtils.equals("1", needLogin);
    }

    public String getImmerseType() {
        return getValueAsString(AppJumpParam.EXTRA_KEY_IMMERSE_TYPE);
    }

    public String getCategory() {
        return getValueAsString(AppJumpParam.EXTRA_KEY_CATEGORY);
    }

    public String getSourceType() {
        return getValueAsString(AppJumpParam.EXTRA_KEY_SOURCE_TYPE);
    }

    public String getSourceId() {
        return getValueAsString(AppJumpParam.EXTRA_KEY_SOURCE_ID);
    }

    public void setUrl(String url) {
        put(AppJumpParam.EXTRA_KEY_URL, url);
    }

    public String getNeedLoading() {
        return getValueAsString(AppJumpParam.EXTRA_KEY_NEED_LOADING);
    }

    public boolean isNeedLoading() {
        return TextUtils.equals("1", getNeedLoading());
    }

    public boolean getShowWhenComplete() {
        String value = getValueAsString(AppJumpParam.EXTRA_KEY_SHOW_WHEN_COMPLETE);
        return !TextUtils.equals("0", value);
    }

    public void setBbsHandleMsgId(String msgId) {
        put(AppJumpParam.EXTRA_KEY_BBS_MESSAGE_ID, msgId);
    }

    public void setBbsHandleMsgType(String msgType) {
        put(AppJumpParam.EXTRA_KEY_BBS_MESSAGE_TYPE, msgType);
    }

    public void setBbsHandleMsgFragType(String fragType) {
        put(AppJumpParam.EXTRA_KEY_BBS_MESSAGE_FRAG_TAG, fragType);
    }
}
