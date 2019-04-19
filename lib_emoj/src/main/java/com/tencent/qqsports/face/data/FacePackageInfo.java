package com.tencent.qqsports.face.data;

import com.loading.modules.interfaces.face.data.FaceItem;

import java.io.Serializable;
import java.util.List;

public class FacePackageInfo implements Serializable {
    private static final long serialVersionUID = 7441020740508374501L;
    public String groupId;
    public String groupName;
    public String icon;
    public String iconHit;
    public List<FaceItem> encoder;

    public List<FaceItem> getFaceNameList() {
        return encoder;
    }
}