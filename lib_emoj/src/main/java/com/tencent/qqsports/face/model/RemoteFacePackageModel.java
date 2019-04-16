package com.tencent.qqsports.face.model;

import android.text.format.DateUtils;

import com.tencent.qqsports.face.data.RemoteFacePackageInfo;
import com.tencent.qqsports.face.data.RemoteFaceResPO;

import java.util.List;

public class RemoteFacePackageModel extends BaseDataModel<RemoteFaceResPO> {
    private final static long UPDATE_PERIOD = 1 * DateUtils.HOUR_IN_MILLIS;

    public RemoteFacePackageModel(IDataListener mDataListener) {
        super(mDataListener);
    }

    @Override
    protected String getUrl(int tag) {
        return URLConstants.getUrl() + "sticker/official";
    }

    @Override
    protected Class<?> getClazz() {
        return RemoteFaceResPO.class;
    }

    @Override
    protected long getCacheTime() {
        return 2 * DateUtils.HOUR_IN_MILLIS;
    }

    public List<RemoteFacePackageInfo> getRemoteFacePackageList() {
        return mResponseData != null ? mResponseData.stickers : null;
    }

    public String getDataVersion() {
        return null;
    }

    public boolean needUpdate() {
        return mResponseData == null || System.currentTimeMillis() - mResponseData.getLastUpdateTime() > UPDATE_PERIOD;
    }
}
