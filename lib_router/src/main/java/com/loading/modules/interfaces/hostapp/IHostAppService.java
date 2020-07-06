package com.loading.modules.interfaces.hostapp;

import android.app.Activity;
import android.content.Context;
import androidx.fragment.app.FragmentManager;

import com.loading.modules.IModuleInterface;
import com.loading.modules.annotation.Repeater;
import com.loading.modules.data.MediaEntity;
import com.loading.modules.data.jumpdata.AppJumpParam;
import com.loading.modules.interfaces.photoselector.ICameraGalleryGuideCallback;

import java.util.List;

/**
 * Created by huazhang on 2018/1/24.
 * the app module provides service
 */
@Repeater
public interface IHostAppService extends IModuleInterface {
    //生命周期、配置相关
    void onActivityResume(Activity activity, AppJumpParam appJumpParam);

    void disableInnerSpreadOnce();

    void startCameraActivityWithPermission(Activity activity, Object ipcCameraProvider, int showType);

    int activateUnicomCard(int status);

    void refreshUnicomCardStatus();

    int getKingCardStatusForBoss();

    //关注相关
    boolean isTagAttended(String tagId);

    void bossFollowViewClickEvent(Context context, String scene, String tagId, String url);

    void bossFollowViewShowEvent(Context context, String scene, String tagId, String url);

    //其他
    void checkAndSetServiceHandler();

    Object createNewsDetailTextItemWrapper(Context context);

    Object createNewsDetailImageItemWrapper(Context context);

    void startPhotoGlancePage(Context context, String imageUrl);

    void startPhotoGlancePage(Context context, List<MediaEntity> imageInfo, int initImgIndex, boolean needControlLayer);

    void onClearLoginInfo();

    void showCameraGalleryGuideDialog(FragmentManager fragmentManager,
                                      ICameraGalleryGuideCallback callback,
                                      int showType,
                                      String fragmentTag);

    void initGuidRelated();

    //fullscreen h5 popup

    //展示正在静默加载的H5浮层，如果没加载完成展示loading，如果加载完成直接展示页面
    boolean showPreloadingH5layerIfExist(Activity activity, String notifyId);

    String onH5layerNotify(final String url, boolean showLoading, boolean showIfComplete);

    String onH5layerNotify(final AppJumpParam jumpParam);

    String onH5layerNotify(final AppJumpParam jumpParam, final AppJumpParam showOnPage);


    /**
     * 用来处理全局通知和播放器自动转屏的逻辑
     * @param activity activity
     * @return true for 正在展示 h5 相关的全局弹窗，这个时候播放器不能转屏
     */
    boolean isShowH5Notify(Activity activity);

    void hidePostTopicBar();

    void startVideoPreview(Activity context,
                           MediaEntity mediaEntity,
                           int requestCode);

    void startImmersePlay(Context context,
                          String vid,
                          String cid,
                          String playerReportPageName);

}
