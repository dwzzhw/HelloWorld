package com.tencent.qqsports.commentbar.utils;

import com.tencent.qqsports.modules.interfaces.login.LoginModuleMgr;
import com.tencent.qqsports.servicepojo.comment.CommentInfo;
import com.tencent.qqsports.servicepojo.comment.CommentUserInfo;

public class CommentUtils {
    public static CommentInfo generateMyCommentInfo(String content) {
        CommentInfo commentInfo = new CommentInfo();
        CommentUserInfo userInfo = new CommentUserInfo();
        userInfo.nick = LoginModuleMgr.getUserNickName();
        userInfo.head = LoginModuleMgr.getUserLogo();
        commentInfo.userinfo = userInfo;
        commentInfo.setContent(content);
        return commentInfo;
    }
}
