package com.loading.modules.interfaces.commentpanel.data;

import com.loading.modules.data.MediaEntity;

import java.io.Serializable;
import java.util.ArrayList;

public class DraftItem implements Serializable {
    private static final long serialVersionUID = -3250355557125156612L;

    private String contentStr;
    private ArrayList<MediaEntity> selectedMediaList;
    private Object txtPropItem;

    public DraftItem(String contentStr, ArrayList<MediaEntity> selectedMediaList, Object txtPropItem) {
        this.contentStr = contentStr;
        this.selectedMediaList = selectedMediaList;
        this.txtPropItem = txtPropItem;
    }

    public void updateContent(String contentStr, ArrayList<MediaEntity> selectedMediaList, Object txtPropItem) {
        this.contentStr = contentStr;
        this.selectedMediaList = selectedMediaList;
        this.txtPropItem = txtPropItem;
    }

    public String getContentStr() {
        return contentStr;
    }

    public void setContentStr(String contentStr) {
        this.contentStr = contentStr;
    }

    public ArrayList<MediaEntity> getSelectedMediaList() {
        return selectedMediaList;
    }

    public void setSelectedMediaList(ArrayList<MediaEntity> selectedMediaList) {
        this.selectedMediaList = selectedMediaList;
    }

    public Object getTxtPropItem() {
        return txtPropItem;
    }

    public void setTxtPropItem(Object txtPropItem) {
        this.txtPropItem = txtPropItem;
    }
}

