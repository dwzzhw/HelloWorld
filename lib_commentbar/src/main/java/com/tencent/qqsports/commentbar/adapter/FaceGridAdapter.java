package com.tencent.qqsports.commentbar.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.tencent.qqsports.commentbar.R;
import com.tencent.qqsports.face.FacePageItems;
import com.tencent.qqsports.common.CApplication;

public class FaceGridAdapter extends BaseAdapter {
    private Context mContext;
    private FacePageItems mFacePageItems;

    public FaceGridAdapter(Context context,
                           FacePageItems facePageItems) {
        mContext = context;
        mFacePageItems = facePageItems;
    }

    @Override
    public int getCount() {
        return mFacePageItems != null ? mFacePageItems.getFaceCntPerPage() + 1 : 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.face_gridview_item, parent, false);
            convertView.setLayoutParams(new GridView.LayoutParams(GridView.AUTO_FIT, CApplication.getDimensionPixelSize(R.dimen.comment_face_panel_content_item_size)));
        }
        if (position == getCount() - 1) {
            ((ImageView) convertView).setImageResource(R.drawable.btn_face_delete_selector);
            return convertView;
        }

        Bitmap faceBitmap = mFacePageItems.getFaceBitmapAtGroupPosition(position);
        if (faceBitmap != null) {
            ((ImageView) convertView).setImageBitmap(faceBitmap);
        } else {
            ((ImageView) convertView).setImageResource(R.drawable.emo_face_null);
        }
        return convertView;
    }

}
