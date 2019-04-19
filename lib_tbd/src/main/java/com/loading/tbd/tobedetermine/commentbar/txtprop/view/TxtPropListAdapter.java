package com.loading.tobedetermine.commentbar.txtprop.view;

import android.content.Context;

import com.loading.common.component.CApplication;
import com.loading.common.utils.SystemUtils;
import com.tencent.qqsports.commentbar.R;

public class TxtPropListAdapter extends BeanBaseRecyclerAdapter {
    public static final int TYPE_ENTER_EFFECT = 1;
    public static final int TYPE_TXT_BG = 2;
    public static final int TYPE_TXT_COLOR = 3;
    public static final int TYPE_DIVIDER = 4;

    private TxtPropItemWrapper.ITxtPropInfoSupplier mPropInfoSupplier;

    public TxtPropListAdapter(Context context, TxtPropItemWrapper.ITxtPropInfoSupplier propInfoSupplier) {
        super(context);
        mPropInfoSupplier = propInfoSupplier;
    }

    @Override
    protected ListViewBaseWrapper createWrapper(int viewType) {
        ListViewBaseWrapper wrapper = null;
        switch (viewType) {
            case TYPE_ENTER_EFFECT:
            case TYPE_TXT_BG:
            case TYPE_TXT_COLOR:
                wrapper = new TxtPropItemWrapper(mContext, mPropInfoSupplier);
                break;
            case TYPE_DIVIDER: 
                CommonGrpVerticalLineWrapper dividerWrapper = new CommonGrpVerticalLineWrapper(mContext);
                dividerWrapper.setPadding(0);
                dividerWrapper.setLineColor(CApplication.getColorFromRes(R.color.std_white1));
                dividerWrapper.setLineWidth((int) (SystemUtils.dpToPx(0.5f)));
                dividerWrapper.setLineHeight(SystemUtils.dpToPx(12));
                wrapper = dividerWrapper;
                break;
        }
        return wrapper;
    }

    @Override
    public boolean isChildSelectable(int adjustPos) {
        return true;
    }
}