package com.tencent.qqsports.face.data;

import com.loading.common.utils.CommonUtils;

import java.io.Serializable;
import java.util.List;

public class RemoteFaceResPO  implements Serializable {
    private static final long serialVersionUID = -4890785123877056260L;
    public List<RemoteFacePackageInfo> stickers;

    public boolean isTheSameResp(RemoteFaceResPO otherResp) {
        boolean result = otherResp != null && !CommonUtils.isEmpty(stickers) && otherResp.stickers != null && stickers.size() == otherResp.stickers.size();
        if (result) {
            for (int i = 0; i < stickers.size(); i++) {
                RemoteFacePackageInfo thisInfo = stickers.get(i);
                RemoteFacePackageInfo otherInfo = otherResp.stickers.get(i);
                if (thisInfo == null || !thisInfo.isTheSamePackage(otherInfo)) {
                    result = false;
                    break;
                }
            }
        }
        return result;
    }
}
