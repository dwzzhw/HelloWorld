package com.loading.tobedetermine.commentbar;

import com.loading.modules.interfaces.login.LoginModuleMgr;

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
