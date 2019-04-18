package com.tencent.qqsports.commentbar.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.loading.common.component.CApplication;
import com.tencent.qqsports.commentbar.R;
import com.tencent.qqsports.face.FacePageItems;

import java.util.List;

public class FacePagerAdapter extends PagerAdapter {
    private static final String TAG = "FacePagerAdapter";
    private Context mContext;
    private List<FacePageItems> mFacePageItemsList;
    private AdapterView.OnItemClickListener mGridViewItemClickListener;
    private int mGridContentContainerPaddingLR;
    private int mGridContentContainerPaddingTop;

    public FacePagerAdapter(Context context, List<FacePageItems> facePageItemsList, AdapterView.OnItemClickListener gridViewItemClickListener) {
        mContext = context;
        mFacePageItemsList = facePageItemsList;
        mGridViewItemClickListener = gridViewItemClickListener;

        mGridContentContainerPaddingLR = CApplication.getDimensionPixelSize(R.dimen.comment_face_panel_content_padding_LR);
        mGridContentContainerPaddingTop = CApplication.getDimensionPixelSize(R.dimen.comment_face_panel_content_padding_top);
    }

    @Override
    public int getCount() {
        return mFacePageItemsList == null ? 0 : mFacePageItemsList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if (object instanceof View) {
            container.removeView((View) object);// 删除页卡
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) { // 这个方法用来实例化页卡
        View itemView = null;
        if (mFacePageItemsList != null && position >= 0 && position < mFacePageItemsList.size()) {
            FacePageItems pageItems = mFacePageItemsList.get(position);
            itemView = createGridViewFromPageItems(pageItems);
            container.addView(itemView);// 添加页卡
        }
        return itemView;
    }

    private GridView createGridViewFromPageItems(FacePageItems pageItems) {
        GridView itemPanel = null;
        if (pageItems != null) {
            itemPanel = (GridView) LayoutInflater.from(mContext).inflate(R.layout.face_gridview, null);
            itemPanel.setNumColumns(pageItems.getPanelColumnCnt());
            itemPanel.setHorizontalSpacing(pageItems.getGridItemPaddingLR() * 2);
            itemPanel.setVerticalSpacing(pageItems.getGridItemVerticalSpacing());
            itemPanel.setPadding(mGridContentContainerPaddingLR, mGridContentContainerPaddingTop + pageItems.getGridItemVerticalSpacing(),
                    mGridContentContainerPaddingLR, 0);
            itemPanel.setAdapter(new FaceGridAdapter(mContext, pageItems));
            itemPanel.setTag(pageItems);

            itemPanel.setOnItemClickListener(mGridViewItemClickListener);
        }
        return itemPanel;
    }
}
