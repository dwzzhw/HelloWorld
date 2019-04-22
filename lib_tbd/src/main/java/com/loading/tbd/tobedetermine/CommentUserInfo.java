package com.loading.tobedetermine;

import android.text.TextUtils;

import java.io.Serializable;
import java.util.List;

public class CommentUserInfo implements Serializable {
    private static final long serialVersionUID = 7039432253024282224L;
    public static final String COMMENT_OWNER_SEX_UNKNOWN = "";
    public static final String COMMENT_OWNER_SEX_MALE = "1";
    public static final String COMMENT_OWNER_SEX_FEMALE = "2";

    public static final String COMMENT_OWNER_OTHERS = "0";
    public static final String COMMENT_OWNER_MYSELF = "1";

    public String userid; // 用户
    public String userIdx; //用来过滤的userid
    public String nick; // 昵称
    public String head; // 头像URL
    public String gender;
    public String region; // 地区 (中国:北京:海淀)
    public String uidex;
    public String upnum;
    public String commentnum;
    public String own;// 标明是否为自己发表的评论
    public String vipType;

    public List<Badge> badge;


    public boolean isOwnComment() {
        return TextUtils.equals(COMMENT_OWNER_MYSELF, own);
    }

    public void setOwn(boolean isOwn) {
        this.own = isOwn ? COMMENT_OWNER_MYSELF : COMMENT_OWNER_OTHERS;
    }

    public void setVip(String status) {
        this.vipType = status;
    }

    public String getVipStatus() {
        return vipType == null ? "" : vipType;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getEncryptUserId() {
        return userIdx;
    }

    public void setEncyptId(String userIdx) {
        this.userIdx = userIdx;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getUidex() {
        return uidex;
    }

    public void setUidex(String uidex) {
        this.uidex = uidex;
    }

    public String getUpnum() {
        return upnum;
    }

    public void setUpnum(String upnum) {
        this.upnum = upnum;
    }

    public String getCommentnum() {
        return commentnum;
    }

    public void setCommentnum(String commentnum) {
        this.commentnum = commentnum;
    }


    @Override
    public String toString() {
        return "CommentUserInfo [userid=" + userid + ", nick=" + nick + ", head=" + head +
                ", gender=" + gender + ", region=" + region
                + ", uidex=" + uidex + ", upnum=" + upnum +
                ", commentnum=" + commentnum + ", own=" + own + "]";
    }

    public static class Badge implements Serializable {
        private static final long serialVersionUID = 2848646918675514084L;
        private String url;
        private int width;
        private int height;
        private transient boolean isLocal = false; //用来处理本地添加的标签重用
        private transient boolean isHead = true; //添加到文字头部还是尾部

        public String getUrl() {
            return url;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public boolean isLocal() {
            return isLocal;
        }

        public void setLocal() {
            isLocal = true;
        }

        public double getWhRatio() {
            if (width > 0 && height > 0) {
                return width * (1.0) / height;
            }
            return 0;
        }

        public void setHead(boolean head) {
            this.isHead = head;
        }

        public boolean isHead() {
            return isHead;
        }
    }
}
