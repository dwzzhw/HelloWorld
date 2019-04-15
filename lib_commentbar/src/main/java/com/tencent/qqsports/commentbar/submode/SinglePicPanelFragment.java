package com.tencent.qqsports.commentbar.submode;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tencent.qqmusic.module.ipcframework.toolbox.IPC;
import com.tencent.qqsports.commentbar.R;
import com.tencent.qqsports.commentbar.videorecord.IPCCameraCallBack;
import com.tencent.qqsports.commentbar.view.AddMediaItemView;
import com.tencent.qqsports.common.CApplication;
import com.tencent.qqsports.common.interfaces.ICameraGalleryGuideCallback;
import com.tencent.qqsports.common.pojo.MediaEntity;
import com.tencent.qqsports.common.util.CollectionUtils;
import com.tencent.qqsports.common.util.UiThreadUtil;
import com.tencent.qqsports.components.BaseFragment;
import com.tencent.qqsports.logger.Loger;
import com.tencent.qqsports.modules.interfaces.hostapp.HostAppModuleMgr;
import com.tencent.qqsports.modules.interfaces.photoselector.PhotoSelectorModuleMgr;

import java.util.ArrayList;

public class SinglePicPanelFragment extends BaseFragment implements AddMediaItemView.IMediaItemClickListener, ICameraGalleryGuideCallback, IPCCameraCallBack {
    private static final String TAG = "SinglePicPanelFragment";
    public static final String KEY_SUPPORT_VIDEO = "support_video";
    private AddMediaItemView mAddMediaItemView;
    private ArrayList<MediaEntity> mOriPaths;
    private IPicPanelListener mPicPanelListener;
    private boolean mSupportVideo;

    public static SinglePicPanelFragment newInstance(boolean supportVideo) {
        Loger.d(TAG, "-->newInstance(), supportVideo=" + supportVideo);
        SinglePicPanelFragment facePanel = new SinglePicPanelFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(KEY_SUPPORT_VIDEO, supportVideo);
        facePanel.setArguments(bundle);
        return facePanel;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            mSupportVideo = bundle.getBoolean(KEY_SUPPORT_VIDEO, false);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.comment_sub_panel_single_pic_layout, container, false);
        mAddMediaItemView = rootView.findViewById(R.id.item_content);
        mAddMediaItemView.setMediaItemClickListener(this);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updateMediaContent();
    }

    @Override
    public void onAddMediaBtnClick() {
        Loger.d(TAG, "-->onAddMediaBtnClick()");
        showCameraGalleryGuideDialog();
    }

    @Override
    public void onMediaContentClick(View itemView, int mediaItemIndex) {
        Loger.d(TAG, "-->onMediaContentClick(), media item index=" + mediaItemIndex);
        if (!CollectionUtils.isEmpty(mOriPaths)) {
            MediaEntity selectedMediaEntry = mOriPaths.get(0);
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
        Loger.d(TAG, "-->onDelMediaBtnClick()");
        if (mOriPaths != null && mOriPaths.size() > 0) {
            mOriPaths.remove(0);
        }
        updateMediaContent();
        notifySelectedPicChanged();
    }

    public void addSelectedMediaItem(MediaEntity mediaEntity) {
        if (mOriPaths == null) {
            mOriPaths = new ArrayList<>(1);
        } else {
            mOriPaths.clear();
        }
        if (mediaEntity != null) {
            mOriPaths.add(mediaEntity);
        }
        updateMediaContent();
        notifySelectedPicChanged();
    }

    private void updateMediaContent() {
        if (mAddMediaItemView != null) {
            MediaEntity mediaEntity = mOriPaths != null && mOriPaths.size() > 0 ? mOriPaths.get(0) : null;
            mAddMediaItemView.setMediaContent(mediaEntity, 0);
        }
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
        PhotoSelectorModuleMgr.startPhotoSelectPage(CApplication.getAppContext(), 1, mOriPaths, showType == ICameraGalleryGuideCallback.SHOW_TYPE_VIDEO_AND_PIC);
    }

    @Override
    public void onCameraGalleryDialogCancel() {
        if (mPicPanelListener != null) {
            mPicPanelListener.onPicSelectDialogCancel();
        }
    }

    public void setPicPanelListener(IPicPanelListener picPanelListener) {
        this.mPicPanelListener = picPanelListener;
    }

    public void clearContent() {
        mOriPaths.clear();
        updateMediaContent();
    }

    private void notifyPageJumpForPS() {
        if (mPicPanelListener != null) {
            mPicPanelListener.beforeJump2PSPage();
        }
    }

    private void notifySelectedPicChanged() {
        if (mPicPanelListener != null) {
            mPicPanelListener.onSelectedPicChanged(getSelectedMediaCnt());
        }
    }

    public int getSelectedMediaCnt() {
        return mOriPaths != null ? mOriPaths.size() : 0;
    }

    public ArrayList<MediaEntity> getSelectedMediaList() {
        return mOriPaths;
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
                addSelectedMediaItem(psMediaEntity);
            }
        });
    }
}
