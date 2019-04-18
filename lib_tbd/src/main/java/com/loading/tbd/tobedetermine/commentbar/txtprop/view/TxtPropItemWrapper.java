package com.loading.tobedetermine.commentbar.txtprop.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.loading.common.utils.Loger;
import com.tencent.qqsports.commentbar.R;

public class TxtPropItemWrapper extends ListViewBaseWrapper {
    private static final String TAG = "TxtPropItemWrapper";
    private TxtPropItemView mPropView;
    private TxtPropItem mPropData;
    private ITxtPropInfoSupplier mPropInfoSupplier;

    public TxtPropItemWrapper(Context context, ITxtPropInfoSupplier propInfoSupplier) {
        super(context);
        mPropInfoSupplier = propInfoSupplier;
    }

    @Override
    public View inflateConvertView(LayoutInflater inflater, int chdPos, int grpPos, boolean isLastChild, boolean isExpanded, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.txt_prop_item_wrapper_layout, parent, false);
        mPropView = (TxtPropItemView) convertView.findViewById(R.id.txt_prop_item);
        return convertView;
    }

    @Override
    public void fillDataToView(Object grpData, Object childData, int chdPos, int grpPos, boolean isLastChild, boolean isExpanded) {
        Loger.d(TAG, "-->fillDataToView(), grpData=" + grpData + ", childData=" + childData + ", chdPos=" + chdPos);
        if (childData instanceof TxtPropItem) {
            mPropData = (TxtPropItem) childData;

            mPropView.updatePropData(mPropData, mPropInfoSupplier != null ? mPropInfoSupplier.isSelectedItem(mPropData) : false);
        }
    }

    public interface ITxtPropInfoSupplier {
        boolean isSelectedItem(TxtPropItem propItem);
    }
}
