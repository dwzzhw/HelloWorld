package com.tencent.qqsports.commentbar.txtprop.data;

import android.content.Context;
import android.text.TextUtils;

import com.tencent.qqsports.commentbar.txtprop.view.TxtPropListAdapter;
import com.tencent.qqsports.common.util.CollectionUtils;
import com.tencent.qqsports.config.URLConstants;
import com.tencent.qqsports.httpengine.datamodel.BaseDataModel;
import com.tencent.qqsports.httpengine.datamodel.IDataListener;
import com.tencent.qqsports.recycler.beanitem.CommonBeanItem;
import com.tencent.qqsports.recycler.beanitem.IBeanItem;
import com.tencent.qqsports.servicepojo.match.IMatchIdQueryListener;
import com.tencent.qqsports.servicepojo.prop.TxtPropItem;

import java.util.ArrayList;
import java.util.List;

public class TxtPropItemListModel extends BaseDataModel<TxtPropItemListPO> {
    private TxtPropItemListPO mTxtPropList;
    private List<IBeanItem> mPropBeanList = null;
    private boolean isFullScreenMode;
    private Context mContainerContext;

    public TxtPropItemListModel(Context context, IDataListener mDataListener, boolean isFullScreenMode) {
        super(mDataListener);
        mContainerContext = context;
        this.isFullScreenMode = isFullScreenMode;
    }

    @Override
    protected String getUrl(int tag) {
        String midParamStr = "";
        if (mContainerContext instanceof IMatchIdQueryListener) {
            String mid = ((IMatchIdQueryListener) mContainerContext).getMatchMid();
            if (!TextUtils.isEmpty(mid)) {
                midParamStr = "&mid=" + mid;
            }
        }
        return URLConstants.getUrl() + "backpack/messageProps?fullscreen=" + (isFullScreenMode ? 1 : 0) + midParamStr;
    }

    @Override
    protected Class<?> getClazz() {
        return TxtPropItemListPO.class;
    }

    @Override
    protected void onGetResponse(TxtPropItemListPO responseData, int tag) {
        super.onGetResponse(responseData, tag);
        mTxtPropList = responseData;
        convert2BeanList();
    }

    private void convert2BeanList() {
        if (mTxtPropList != null && mTxtPropList.contentEffects != null) {
            if (mPropBeanList != null) {
                mPropBeanList.clear();
            }
            mPropBeanList = new ArrayList<>();
            for (int i = 0; i < mTxtPropList.contentEffects.size(); i++) {
                List<TxtPropItem> propList = mTxtPropList.contentEffects.get(i);
                if (!CollectionUtils.isEmpty(propList)) {
                    if (mPropBeanList.size() > 0) {
                        mPropBeanList.add(CommonBeanItem.newInstance(TxtPropListAdapter.TYPE_DIVIDER, null));
                    }
                    for (int j = 0; j < propList.size(); j++) {
                        TxtPropItem txtPropItem = propList.get(j);
                        int wrapperType = getTxtPropItemAdapterType(txtPropItem);
                        if (wrapperType > 0) {
                            mPropBeanList.add(CommonBeanItem.newInstance(wrapperType, txtPropItem));
                        }
                    }
                }
            }
        }
    }

    private int getTxtPropItemAdapterType(TxtPropItem txtPropItem) {
        int mapViewType = 0;
        if (txtPropItem != null) {
            switch (txtPropItem.getType()) {
                case TxtPropItem.TXT_PROP_TYPE_EE:
                    mapViewType = TxtPropListAdapter.TYPE_ENTER_EFFECT;
                    break;
                case TxtPropItem.TXT_PROP_TYPE_BG:
                    mapViewType = TxtPropListAdapter.TYPE_TXT_BG;
                    break;
                case TxtPropItem.TXT_PROP_TYPE_COLOR:
                    mapViewType = TxtPropListAdapter.TYPE_TXT_COLOR;
                    break;
            }
        }
        return mapViewType;
    }

    public List<IBeanItem> getPropBeanList() {
        return mPropBeanList;
    }
}
