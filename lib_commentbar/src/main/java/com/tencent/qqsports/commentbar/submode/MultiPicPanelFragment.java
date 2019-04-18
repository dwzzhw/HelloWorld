package com.tencent.qqsports.commentbar.submode;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loading.common.component.CApplication;
import com.loading.common.utils.CommonUtils;
import com.loading.common.utils.Loger;
import com.loading.common.utils.UiThreadUtil;
import com.loading.modules.data.MediaEntity;
import com.loading.modules.interfaces.photoselector.ICameraGalleryGuideCallback;
import com.loading.modules.interfaces.photoselector.PhotoSelectorModuleMgr;
import com.tencent.qqsports.commentbar.R;
import com.tencent.qqsports.commentbar.videorecord.IPCCameraCallBack;
import com.tencent.qqsports.commentbar.view.AddMediaItemView;

import java.util.ArrayList;

/**
 * 支持多选的图片面板
 */
public class MultiPicPanelFragment extends PanelModeBaseFragment implements
        ICameraGalleryGuideCallback,
        IPCCameraCallBack, AddMediaItemView.IMediaItemClickListener {
    private static final String TAG = MultiPicPanelFragment.class.getSimpleName();
    HorizontalScrollView mScrollView = null;
    LinearLayout mScrollContent = null;
    TextView mNotice = null;

    private AddMediaItemView mDefaultMediaItemView = null;
    private ArrayList<MediaEntity> mOriPaths;

    private IPicPanelListener mPicPanelListener;

    private int ADD_PIC_MAX_CNT;
    private boolean mSupportVideo;

    private int mPicItemWidth = 0;
    private int mPicItemHeight = 0;

    public static MultiPicPanelFragment newInstance(int panelTargetHeight) {
        MultiPicPanelFragment facePanel = new MultiPicPanelFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_TARGET_PANEL_HEIGHT, panelTargetHeight);
        facePanel.setArguments(bundle);
        return facePanel;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            int targetPanelHeight = bundle.getInt(KEY_TARGET_PANEL_HEIGHT, -1);
            setTargetHeight(targetPanelHeight);
        }
        mPicItemWidth = CApplication.getDimensionPixelSize(R.dimen.comment_sub_panel_add_pic_item_width);
        mPicItemHeight = CApplication.getDimensionPixelSize(R.dimen.comment_sub_panel_add_pic_item_height);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.comment_sub_panel_multi_pic_layout;
    }

    @Override
    protected void initView(Context context) {
        mScrollView = mRootView.findViewById(R.id.horizontal_scrollview);
        mScrollContent = mRootView.findViewById(R.id.scroll_content);
        mNotice = mRootView.findViewById(R.id.notice);
        mDefaultMediaItemView = mRootView.findViewById(R.id.default_item_view);
        mFinishBtn = mRootView.findViewById(R.id.finishBtn);

        mDefaultMediaItemView.setMediaItemClickListener(this);
        mRootView.setBackgroundColor(CApplication.getColorFromRes(R.color.facepanel_bg_color));
        resetToDefaultView();
        updateFinishBtnEnableStatus();
    }

    @Override
    public void onAddMediaBtnClick() {
        Loger.d(TAG, "-->onAddMediaBtnClick()");
        showCameraGalleryGuideDialog();
    }

    @Override
    public void onMediaContentClick(View itemView, int mediaItemIndex) {
        Loger.d(TAG, "-->onMediaContentClick()");
        showCameraGalleryGuideDialog();
        if (!CommonUtils.isEmpty(mOriPaths) && mediaItemIndex >= 0 && mOriPaths.size() > mediaItemIndex) {
            MediaEntity selectedMediaEntry = mOriPaths.get(mediaItemIndex);
            if (selectedMediaEntry != null) {
                notifyPageJumpForPS();
                if (selectedMediaEntry.isImage()) {
                    PhotoSelectorModuleMgr.startPhotoPreviewPage(getContext(),
                            true,
                            true,
                            selectedMediaEntry.getPath(),
                            mOriPaths);

//                    PhotoSelectorModuleMgr.startPhotoSelectPage(CApplication.getAppContext(), 1, mOriPaths, mSupportVideo);
//                    HostAppModuleMgr.startPhotoGlancePage(getContext(), selectedMediaEntry.getImgUrl());
                } else if (selectedMediaEntry.isVideo() && getActivity() != null) {
                    HostAppModuleMgr.startVideoPreview(getActivity(), selectedMediaEntry, -1);
                }
            }
        }
    }

    @Override
    public void onDelMediaBtnClick(View itemView, int mediaItemIndex) {
        Loger.d(TAG, "-->onDelMediaBtnClick(), mediaItemIndex=" + mediaItemIndex);
        deleteCurrentPic(itemView, mediaItemIndex);
    }

    /**
     * 开启相册，以选择图片
     */
    private void showCameraGalleryGuideDialog() {
        if (getContext() instanceof FragmentActivity) {
            FragmentManager fragmentManager = ((FragmentActivity) getContext()).getSupportFragmentManager();
            if (fragmentManager != null) {
                HostAppModuleMgr.showCameraGalleryGuideDialog(fragmentManager, this,
                        mSupportVideo ? ICameraGalleryGuideCallback.SHOW_TYPE_VIDEO_AND_PIC : ICameraGalleryGuideCallback.SHOW_TYPE_PIC, TAG);
            }

            if (mPicPanelListener != null) {
                mPicPanelListener.onShowPicSelectDialog();
            }
        }
    }

    public void addSelectedMedia(ArrayList<MediaEntity> oriPaths) {
        mOriPaths = oriPaths;
        if (ADD_PIC_MAX_CNT == 1) {
            addMaxOneSelectedPic(oriPaths);
        } else {
            addMultiSelectedPics(oriPaths);
        }
    }

    /**
     * 最多只可选一张图
     * <p/>
     * 复用默认添加图片按钮（即启动相册的入口）
     */
    private void addMaxOneSelectedPic(ArrayList<MediaEntity> paths) {
        if (paths == null || paths.size() <= 0) {
            return;
        }

        mDefaultMediaItemView.setMediaContent(paths.get(0), 0);
        updateRelatedUI();
    }

    /**
     * 最多可以选多于一张图片
     */
    private void addMultiSelectedPics(ArrayList<MediaEntity> paths) {
        Loger.d(TAG, "--addMultiSelectedPics() paths:" + paths);
        if (paths == null || paths.size() == 0) {
            resetToDefaultView();
        } else {
            mDefaultMediaItemView.setVisibility(View.GONE);

            int existingItemCnt = mScrollContent.getChildCount();
            int curIndex = 0;
            int targetItemCnt = Math.min(paths.size() + 1, ADD_PIC_MAX_CNT);
            for (; curIndex < targetItemCnt; curIndex++) {
                AddMediaItemView itemView = null;
                if (curIndex < existingItemCnt) { //复用已存在的view
                    View v = mScrollContent.getChildAt(curIndex);
                    if (v instanceof AddMediaItemView && ((AddMediaItemView) v).getScaleRate() == 1) {
                        itemView = (AddMediaItemView) v;
                    } else {
                        mScrollContent.removeView(v);
                    }
                }
                if (itemView == null) { //不存在则全新添加
                    itemView = new AddMediaItemView(getContext());
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(mPicItemWidth, mPicItemHeight);
                    itemView.setTargetInitWH(mPicItemWidth, mPicItemHeight);
                    mScrollContent.addView(itemView, curIndex, lp);
                    itemView.setMediaItemClickListener(this);
                }

                MediaEntity mediaEntity = curIndex >= mOriPaths.size() ? null : mOriPaths.get(curIndex);
                itemView.setMediaContent(mediaEntity, curIndex);
            }
            for (int i = curIndex; i < existingItemCnt; i++) { //清理多余的View
                mScrollContent.removeViewAt(i);
            }
        }
        updateRelatedUI();
    }

    public void resetAllViews() {
        Loger.d(TAG, "-------->resetAllViews()");
        mScrollContent.removeAllViews();
        if (mOriPaths != null) {
            mOriPaths.clear();//清空数据
        }
        resetToDefaultView();
        mOriPaths = null;

    }

    private void resetToDefaultView() {
        Loger.d(TAG, "-->resetToDefaultView()");
        mDefaultMediaItemView.setMediaContent(null, 0);
        mDefaultMediaItemView.setVisibility(View.VISIBLE);

        mScrollContent.removeAllViews();
    }

    private void updateRelatedUI() {
        setNoticeText(getSelectedPicSize());
        updateFinishBtnEnableStatus();
        notifySelectedPicChanged();
    }

    /**
     * 删除当前图片
     * <p/>
     * 删除index位置的图片
     */
    private void deleteCurrentPic(final View v, final int index) {
        Loger.d(TAG, "-->deleteCurrentPic(), index=" + index);
        if (v instanceof AddMediaItemView && v != mDefaultMediaItemView) {
            collapse((AddMediaItemView) v, index);
        } else {
            removeMediaItemAt(index);
        }
    }

    private void collapse(final AddMediaItemView mediaItemView, int itemIndex) {
        ValueAnimator animator = ValueAnimator.ofFloat(1, 0);
        animator.setDuration(200);
        animator.addUpdateListener(animation -> {
            float curValue = (float) animation.getAnimatedValue();
            if (curValue == 0) { //动画已结束
                if (mediaItemView.getParent() instanceof ViewGroup) {  //移除该项
                    ((ViewGroup) mediaItemView.getParent()).removeView(mediaItemView);
                }

                removeMediaItemAt(itemIndex);
            } else {
                mediaItemView.setScaleRate(curValue);
                mediaItemView.requestLayout();
            }

            Loger.d(TAG, "-->value animator cur value=" + curValue + ", thread=" + Thread.currentThread().getName());
        });
        animator.start();
    }

    private void removeMediaItemAt(int itemIndex) {
        if (mOriPaths != null && 0 <= itemIndex && itemIndex < mOriPaths.size()) {
            mOriPaths.remove(itemIndex);
        }
        if (mOriPaths != null) {  //重新刷新数据
            addMultiSelectedPics(mOriPaths);
        }
    }


    public ArrayList<MediaEntity> getSelectedMediaList() {
        Loger.d(TAG, "-->getSelectedMediaList(), mOriPaths:" + mOriPaths);
        return mOriPaths;
    }

    public int getSelectedMediaCnt() {
        Loger.d(TAG, "-->getSelectedMediaCnt(), mOriPaths:" + mOriPaths);
        return mOriPaths != null ? mOriPaths.size() : 0;
    }

    public MediaEntity getSelectedVideoEntity() {
        MediaEntity videoEntity = null;
        if (mOriPaths != null) {
            for (MediaEntity mediaEntity : mOriPaths) {
                if (mediaEntity != null && mediaEntity.isVideo()) {
                    videoEntity = mediaEntity;
                    break;
                }
            }
        }
        return videoEntity;
    }

    private int getSelectedPicSize() {
        int size = mOriPaths != null ? mOriPaths.size() : 0;
        Loger.d(TAG, "-->getSelectedPicSize(), size:" + size);
        return size;
    }

    public void onPhotoSelectDone(ArrayList<MediaEntity> data) {
        Loger.d(TAG, "-->onPhotoSelectDone() ");
        addSelectedMedia(data);
    }

    @Override
    public void setEditText(ViewGroup rootLayout, EditText paramEditText) {
        super.setEditText(rootLayout, paramEditText);
        updateFinishBtnEnableStatus();
    }

    /**
     * 设置当前可选择图片个数的最大值
     */
    public void setAddPicMaxCnt(int maxCnt) {
        ADD_PIC_MAX_CNT = maxCnt;
        setNoticeText(0);
    }

    public void setSupportVideo(boolean supportVideo) {
        Loger.d(TAG, "-->setSupportVideo(), supportVideo=" + supportVideo);
        mSupportVideo = supportVideo;
    }

    /**
     * 设置当前已选择 && 未选择图片个数提示信息
     */
    public void setNoticeText(int cnt) {
        if (mNotice != null) {
            mNotice.setText(CApplication.getStringFromRes(R.string.post_select_notice, cnt, ADD_PIC_MAX_CNT - cnt));
        }
    }

    public void setPicPanelListener(IPicPanelListener picPanelListener) {
        this.mPicPanelListener = picPanelListener;
    }

    @Override
    public void onCameraClick(int showType) {
        if (getContext() instanceof Activity) {
            notifyPageJumpForPS();
            HostAppModuleMgr.startCameraActivityWithPermission((Activity) getContext(), this, showType);
        }
    }

    @Override
    public void onGalleryClick(int showType) {
        notifyPageJumpForPS();
        PhotoSelectorModuleMgr.startPhotoSelectPage(getContext(), ADD_PIC_MAX_CNT, mOriPaths, showType == ICameraGalleryGuideCallback.SHOW_TYPE_VIDEO_AND_PIC);
    }

    private void notifyPageJumpForPS() {
        if (mPicPanelListener != null) {
            mPicPanelListener.beforeJump2PSPage();
        }
    }

    private void notifySelectedPicChanged() {
        if (mPicPanelListener != null) {
            mPicPanelListener.onSelectedPicChanged(getSelectedPicSize());
        }
    }

    private void addCameraRecordPic(MediaEntity mediaEntity) {
        if (mediaEntity != null) {
            if (mOriPaths == null) {
                mOriPaths = new ArrayList<>(ADD_PIC_MAX_CNT);
            }
            if (mOriPaths.size() < ADD_PIC_MAX_CNT) {
                mOriPaths.add(mediaEntity);
                addSelectedMedia(mOriPaths);
            }
        }
    }

    @Override
    public void onCameraRecordRet(MediaEntity psMediaEntity) {
        Loger.d(TAG, "onCameraRecordRet() -> ");
        onCameraReturn(psMediaEntity);
    }

    @Override
    public void onCameraPhotoRet(MediaEntity psMediaEntity) {
        Loger.d(TAG, "onCameraPhotoRet() -> ");
        onCameraReturn(psMediaEntity);
    }

    private void onCameraReturn(MediaEntity psMediaEntity) {
        UiThreadUtil.postRunnable(() -> {
            IPC.get().setMethodProvider(null);
            if (psMediaEntity != null) {
                addCameraRecordPic(psMediaEntity);
            }
        });
    }
}