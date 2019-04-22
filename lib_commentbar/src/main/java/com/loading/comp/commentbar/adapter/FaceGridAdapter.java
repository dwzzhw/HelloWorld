package com.loading.comp.commentbar.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.loading.common.component.CApplication;
import com.loading.common.utils.Loger;
import com.loading.comp.commentbar.R;
import com.loading.comp.commentbar.submode.IFacePanelListener;
import com.loading.modules.interfaces.face.data.FacePageItems;

public class FaceGridAdapter extends BaseAdapter {
    private static final String TAG = "FaceGridAdapter";
    private Context mContext;
    private FacePageItems mFacePageItems;
    private IFacePanelListener mFacePanelListener;
    private int mConvertViewTargetHeight;

    public FaceGridAdapter(Context context, FacePageItems facePageItems, IFacePanelListener facePanelListener, int convertViewHeight) {
        mContext = context;
        mFacePageItems = facePageItems;
        mFacePanelListener = facePanelListener;
        mConvertViewTargetHeight = convertViewHeight;
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
            convertView.setLayoutParams(new GridView.LayoutParams(GridView.AUTO_FIT, mConvertViewTargetHeight > 0 ? mConvertViewTargetHeight : CApplication.getDimensionPixelSize(R.dimen.comment_face_panel_content_item_size)));
        }
        ImageViewHolder viewHolder;
        if (!(convertView.getTag() instanceof ImageViewHolder)) {
            viewHolder = new ImageViewHolder();
            viewHolder.faceContentView = convertView.findViewById(R.id.face_content_view);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ImageViewHolder) convertView.getTag();
        }

        ImageView contentView = viewHolder != null ? viewHolder.faceContentView : null;
        if (contentView != null) {
            if (position == getCount() - 1) {
                contentView.setImageResource(R.drawable.btn_face_delete_selector);
                boolean enableDeleteBtn = mFacePanelListener == null || mFacePanelListener.needEnableDeleteBtn();
                Loger.d(TAG, "-->getDeleteBtnView(), enableDeleteBtn=" + enableDeleteBtn);
                contentView.setEnabled(enableDeleteBtn);
            } else {
                Bitmap faceBitmap = mFacePageItems.getFaceBitmapAtGroupPosition(position);
                if (faceBitmap != null) {
                    contentView.setImageBitmap(faceBitmap);
                } else {
                    contentView.setImageResource(R.drawable.emo_face_null);
                }
            }
        }

        return convertView;
    }

    class ImageViewHolder {
        ImageView faceContentView;
    }

}
