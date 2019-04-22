package com.loading.tobedetermine;

import java.io.Serializable;

/**
 * 大家聊中附加的数据结构，通用
 */

public class CommentStructInfo implements Serializable {
    private static final long serialVersionUID = -3995459073475798830L;
    private String leftName;
    private String rightName;
    private String leftIcon;
    private String rightIcon;
    private String title;
    private String topLogo;
    private String leftPoints;
    private String rightPoints;
    private String rightPropsId;
    private String leftPropsId;
    private String propsIcon;


    public String getLeftName() {
        return filterNullToEmptyStr(leftName);
    }

    public String getRightName() {
        return filterNullToEmptyStr(rightName);
    }

    public String getLeftIcon() {
        return filterNullToEmptyStr(leftIcon);
    }

    public String getRightIcon() {
        return filterNullToEmptyStr(rightIcon);
    }

    public String getTitle() {
        return filterNullToEmptyStr(title);
    }

    public String getAdPic() {
        return filterNullToEmptyStr(topLogo);
    }

    public String getLeftSupport() {
        return filterNullToEmptyStr(leftPoints);
    }

    public String getRightSupport() {
        return filterNullToEmptyStr(rightPoints);
    }

    public String getLeftPropsId() {
        return leftPropsId;
    }

    public String getRightPropsId() {
        return rightPropsId;
    }

    public String getPropsIcon() {
        return propsIcon;
    }

    public void setPropsIcon(String propsIcon) {
        this.propsIcon = propsIcon;
    }

    private String filterNullToEmptyStr(String str) {
        return str == null ? "" : str;
    }
}
