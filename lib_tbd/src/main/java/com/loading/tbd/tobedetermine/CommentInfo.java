package com.loading.tobedetermine;

import android.text.SpannableStringBuilder;
import android.text.TextUtils;


import com.loading.common.utils.CommonUtil;
import com.loading.common.utils.Loger;
import com.loading.deletelater.TxtPropItem;
import com.loading.modules.data.jumpdata.AppJumpParam;
import com.loading.modules.interfaces.upload.data.UploadPicPojo;

import java.io.Serializable;
import java.util.List;

/**
 * Created by hzhang on 10/10/16.
 */
public class CommentInfo implements Serializable, Comparable<CommentInfo> {
    private static final long serialVersionUID = -8658001221019499168L;
    public static final String FADE_PAYLOAD = "fade";


    public static final int MSG_TYPE_NORMAL = 0; //普通消息
    public static final int MSG_TYPE_PRIZE = 1; //中奖消息
    public static final int MSG_TYPE_PROP = 2; //道具消息
    public static final int MSG_TYPE_MC = 3; //直播员广播
    public static final int MSG_TYPE_GUESS = 4; //竞猜消息
    public static final int MSG_TYPE_AD = 5; //广告消息
    public static final int MSG_TYPE_VS = 6; //对阵消息

    public String id; // 评论Id
    public String time; // 评论发表时间
    public String content; // 评论内容
    public CommentUserInfo userinfo; // 评论用户信息
    private String systemCommentType;
    public UploadPicPojo.UpPicInfo picInfo; // 评论图片信息
    public AdInfo adInfo;
    private CommentStructInfo structData;
    public String standSelf; //立场

    private TxtPropItem txtPropInfo;

    public transient SpannableStringBuilder spannableContent;
    public transient List imageSpans;
    public transient boolean isWelcome;
    public transient boolean isShow = true; //是否半透明

    public CommentInfo() {
        this("", "");
    }

    public CommentInfo(String userName, String content) {
        userinfo = new CommentUserInfo();
        userinfo.nick = userName;
        this.content = content;
    }

    public boolean isValidType() {
        int commentType = getCommentType();
        return commentType == MSG_TYPE_NORMAL ||
                commentType == MSG_TYPE_PRIZE ||
                commentType == MSG_TYPE_PROP ||
                commentType == MSG_TYPE_MC ||
                commentType == MSG_TYPE_GUESS ||
                commentType == MSG_TYPE_AD ||
                commentType == MSG_TYPE_VS;
    }

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }

    public boolean hasTxtPropEnterEffect() {
        return this.txtPropInfo != null && txtPropInfo.isEnterEffectType();
    }

    public boolean hasBadges() {
        return userinfo != null && !CommonUtil.isEmpty(userinfo.badge);
    }

    public List<CommentUserInfo.Badge> getBadges() {
        return userinfo != null ? userinfo.badge : null;
    }

    public void setBadges(List<CommentUserInfo.Badge> badges) {
        if (userinfo != null) {
            userinfo.badge = badges;
        }
    }

    public void setSystemCommentType(String systemCommentType) {
        this.systemCommentType = systemCommentType;
    }

    public int getCommentType() {
        int type;
        try {
            type = !TextUtils.isEmpty(systemCommentType) ? Integer.parseInt(systemCommentType) : MSG_TYPE_NORMAL;
        } catch (Exception e) {
            type = -1;
        }
        return type;
    }

    public boolean isNormalType() {
        return getCommentType() == MSG_TYPE_NORMAL;
    }

    public boolean isVsType() {
        return getCommentType() == MSG_TYPE_VS;
    }

    public boolean isPropType() {
        return getCommentType() == MSG_TYPE_PROP;
    }

    public boolean isAdType() {
        return getCommentType() == MSG_TYPE_AD;
    }

    public boolean isGuessType() {
        return getCommentType() == MSG_TYPE_GUESS;
    }

    public boolean isOwnComment() {
        return userinfo != null && userinfo.isOwnComment();
    }

    public String getVipStatus() {
        return userinfo != null ? userinfo.getVipStatus() : "";
    }

    public int getSptType() {
        int sptType = 0;
        try {
            sptType = Integer.parseInt(standSelf);
        } catch (Exception e) {
            Loger.e("QQSports", "exception: " + e);
        }
        return sptType;
    }

    public boolean isEqualTo(CommentInfo tCommentPo) {
        boolean isSame = false;
        if (tCommentPo != null && id != null) {
            isSame = id.equals(tCommentPo.getId());
        }
        return isSame;
    }

    public long getCommentTime() {
        long result = 0l;
        try {
            result = Long.parseLong(time);
        } catch (Exception e) {
            Loger.e("QQSports", "comment parse long: " + e);
        }
        return result;
    }

    public static CommentInfo generateComment(String replyCommentId,
                                              String generateId,
                                              String content,
                                              String time) {

        CommentInfo info = new CommentInfo();
        info.systemCommentType = String.valueOf(CommentInfo.MSG_TYPE_NORMAL);
        if (TextUtils.isEmpty(generateId) || "".equals(generateId)) {
            generateId = String.valueOf(System.currentTimeMillis());
        }
        info.id = generateId;
        if (TextUtils.isEmpty(time)) {
            info.time = String.valueOf(System.currentTimeMillis() / 1000);
        } else {
            info.time = time;
        }

        info.content = content;
        return info;
    }

    public String getId() {
        return id;
    }

    public boolean hasValidId() {
        return !TextUtils.isEmpty(id);
    }

    public String getContent() {
        return CommonUtil.filterNullToEmptyStr(content);
    }

    public void setContent(String content) {
        this.content = content;
    }

    public CommentUserInfo getUserinfo() {
        return userinfo;
    }

    public String getUserName() {
        return userinfo == null || TextUtils.isEmpty(userinfo.getNick()) ? "" : userinfo.getNick();
    }

    public String getUserId() {
        return userinfo == null ? "" : userinfo.getUserid();
    }

    public String getEncryptedUserid() {
        return userinfo == null ? "" : userinfo.getEncryptUserId();
    }

    public String getUnidex() {
        return userinfo == null ? "" : userinfo.getUidex();
    }

    public void setUserinfo(CommentUserInfo userinfo) {
        this.userinfo = userinfo;
    }

    public void setPicInfo(UploadPicPojo.UpPicInfo picInfo) {
        this.picInfo = picInfo;
    }

    public CommentStructInfo getStructData() {
        return structData;
    }

    public String getTxtPropItemBgResUrl() {
        return txtPropInfo != null ? txtPropInfo.getPreviewBgUrl() : null;
    }

    public void setTxtPropInfo(TxtPropItem txtPropInfo) {
        this.txtPropInfo = txtPropInfo;
    }

    public TxtPropItem getTxtPropInfo() {
        return txtPropInfo;
    }

    public String getPropsIcon() {
        return isPropType() ? (structData == null ? null : structData.getPropsIcon()) : null;
    }

    public void setPropsIcon(String icon) {
        if (structData == null) {
            structData = new CommentStructInfo();
        }
        structData.setPropsIcon(icon);
    }

    public String getTxtPropContentSuffix() {
        return txtPropInfo != null ? txtPropInfo.getContentSuffix() : null;
    }

    @Override
    public String toString() {
        return "CommentInfo [id=" + id + ", time=" + time + ", content=" + content +
                ", userinfo=" + userinfo + ", picInfo=" + picInfo + "]";
    }

    /*
        比较两条评论是否一样。 区别于compareTo == 0的情况
     */

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof CommentInfo) {
            if (!TextUtils.isEmpty(getId()) && TextUtils.equals(getId(), ((CommentInfo) obj).getId())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        if (id != null) {
            return id.hashCode();
        } else {
            return super.hashCode();
        }
    }

    /**
     * 比较两条评论的发表时间，本条时间减去参数时间
     * <p>
     * 注意 compareTo == 0时， 不代表eqauls == true
     *
     * @param tComment
     * @return >0 本条更新 <0参数更新 ==0 相同时间或有参数非法
     */
    @Override
    public int compareTo(CommentInfo tComment) {
        int result = 0;
        if (TextUtils.isEmpty(time) || tComment == null || TextUtils.isEmpty(tComment.time)) {
            return result;
        }
        long myTime = 0l;
        long tTime = 0l;
        try {
            myTime = Long.valueOf(time);
            tTime = Long.valueOf(tComment.time);
        } catch (Exception e) {
            Loger.e("SingleCommentPO", "format time exception ....");
        }

        return (int) (myTime - tTime);
    }

    public AdInfo getAdInfo() {
        return adInfo;
    }

    public static class AdInfo implements Serializable {
        private static final long serialVersionUID = 501197963550894134L;
        private String button;
        private String goodsName;
        private String icon;
        private String tip1;
        private String tip2;
        private String adId;
        private AppJumpParam jumpData;

        public String getButton() {
            return CommonUtil.filterNullToEmptyStr(button);
        }

        public String getGoodsName() {
            return CommonUtil.filterNullToEmptyStr(goodsName);
        }

        public String getIcon() {
            return CommonUtil.filterNullToEmptyStr(icon);
        }

        public AppJumpParam getJumpData() {
            return jumpData;
        }

        public String getTip1() {
            return CommonUtil.filterNullToEmptyStr(tip1);
        }

        public String getTip2() {
            return CommonUtil.filterNullToEmptyStr(tip2);
        }

        public String getAdId() {
            return adId;
        }
    }


    public static CommentInfo newWelcomeMsg(String welcomeMsg) {
        if (!TextUtils.isEmpty(welcomeMsg)) {
            CommentInfo commentInfo = new CommentInfo();
            commentInfo.content = welcomeMsg;
            commentInfo.systemCommentType = String.valueOf(MSG_TYPE_MC);
            commentInfo.id = null;
            commentInfo.userinfo = new CommentUserInfo();
            commentInfo.userinfo.nick = "";
            commentInfo.isWelcome = true;
            return commentInfo;
        } else {
            return null;
        }
    }
}
