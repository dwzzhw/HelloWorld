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
import com.tencent.qqsports.commentbar.submode.FacePanelLongPressDetector;
import com.tencent.qqsports.commentbar.submode.IFaceItemLongPressListener;
import com.tencent.qqsports.commentbar.submode.IFacePanelListener;
import com.tencent.qqsports.face.FacePageItems;

import java.util.List;

public class FacePagerAdapter extends PagerAdapter {
    private static final String TAG = "FacePagerAdapter";
    private Context mContext;
    private List<FacePageItems> mFacePageItemsList;
    private AdapterView.OnItemClickListener mGridViewItemClickListener;
    private int mGridContentContainerPaddingLR;
    private int mGridContentContainerPaddingTop;
    private int mFaceLogoSize;

    IFaceItemLongPressListener mFaceItemLongPressListener;
    private IFacePanelListener mFacePanelListener;

    public FacePagerAdapter(Context context, List<FacePageItems> facePageItemsList, AdapterView.OnItemClickListener gridViewItemClickListener, IFaceItemLongPressListener faceItemLongPressListener, IFacePanelListener facePanelListener) {
        mContext = context;
        mFacePageItemsList = facePageItemsList;
        mGridViewItemClickListener = gridViewItemClickListener;

        mGridContentContainerPaddingLR = CApplication.getDimensionPixelSize(R.dimen.comment_face_panel_content_padding_LR);
        mGridContentContainerPaddingTop = CApplication.getDimensionPixelSize(R.dimen.comment_face_panel_content_padding_top);
        mFaceLogoSize = CApplication.getDimensionPixelSize(R.dimen.comment_face_panel_content_item_size);

        mFaceItemLongPressListener = faceItemLongPressListener;
        mFacePanelListener = facePanelListener;
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

    private View createGridViewFromPageItems(FacePageItems pageItems) {
        GridView gridView = null;
        if (pageItems != null) {
            int rootViewPaddingTop = mGridContentContainerPaddingTop + pageItems.getGridItemVerticalSpacing() / 2;
            int horizontalSpacing = pageItems.getGridItemPaddingLR() * 2;
            int verticalSpacing = pageItems.getGridItemVerticalSpacing();
            int columnCnt = pageItems.getPanelColumnCnt();
            int rowCnt = pageItems.getPanelRowCnt();
            int gridItemTotalHeight = mFaceLogoSize + verticalSpacing;

            gridView = (GridView) LayoutInflater.from(mContext).inflate(R.layout.face_gridview, null);
            gridView.setNumColumns(columnCnt);
//            gridView.setHorizontalSpacing(horizontalSpacing);
//            gridView.setVerticalSpacing(verticalSpacing);
            gridView.setPadding(mGridContentContainerPaddingLR, rootViewPaddingTop,
                    mGridContentContainerPaddingLR, 0);
            FaceGridAdapter faceItemGridAdapter = new FaceGridAdapter(mContext, pageItems, mFacePanelListener, gridItemTotalHeight);
            gridView.setAdapter(faceItemGridAdapter);
            gridView.setTag(pageItems);

            gridView.setOnItemClickListener(mGridViewItemClickListener);
            gridView.setOnTouchListener(new FacePanelLongPressDetector(pageItems, mFaceItemLongPressListener, mGridContentContainerPaddingLR, rootViewPaddingTop, horizontalSpacing, verticalSpacing, columnCnt, rowCnt));
        }
        return gridView;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}