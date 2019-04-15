package com.tencent.qqsports.commentbar.txtprop;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.tencent.qqsports.commentbar.CommentConstants;
import com.tencent.qqsports.commentbar.R;
import com.tencent.qqsports.commentbar.anim.IAnimationPlayListener;
import com.tencent.qqsports.commentbar.anim.YoyoPlayer;
import com.tencent.qqsports.commentbar.anim.animator.BaseViewAnimator;
import com.tencent.qqsports.commentbar.txtprop.view.TxtPropItemWrapper;
import com.tencent.qqsports.commentbar.txtprop.view.TxtPropListAdapter;
import com.tencent.qqsports.commentbar.txtprop.view.TxtPropPreviewView;
import com.tencent.qqsports.commentbar.utils.CommentUtils;
import com.tencent.qqsports.common.CApplication;
import com.tencent.qqsports.logger.Loger;
import com.tencent.qqsports.common.util.CollectionUtils;
import com.tencent.qqsports.common.util.SystemUtil;
import com.tencent.qqsports.common.util.UiThreadUtil;
import com.tencent.qqsports.common.util.ViewUtils;
import com.tencent.qqsports.common.widget.PopupWindowWrapper;
import com.tencent.qqsports.config.ViewTypeConstant;
import com.tencent.qqsports.imagefetcher.ImageFetcher;
import com.tencent.qqsports.modules.jumpdata.JumpProxyManager;
import com.tencent.qqsports.recycler.beanitem.CommonBeanItem;
import com.tencent.qqsports.recycler.beanitem.IBeanItem;
import com.tencent.qqsports.recycler.layoutmanager.LinearLayoutManagerEx;
import com.tencent.qqsports.recycler.view.RecyclerViewEx;
import com.tencent.qqsports.servicepojo.comment.CommentInfo;
import com.tencent.qqsports.servicepojo.prop.PropLockInfo;
import com.tencent.qqsports.servicepojo.prop.TxtPropItem;

import java.util.ArrayList;
import java.util.List;

/**
 * 评论文字特权道具
 */
public class CommentTxtPropControlBar extends LinearLayout
        implements RecyclerViewEx.OnChildClickListener,
        TxtPropItemWrapper.ITxtPropInfoSupplier,
        IAnimationPlayListener {
    private static final int ANIMATE_RELOCATE_TIME = 200;
    private static final String TAG = "CommentPropControlBar";

    private RecyclerViewEx mPropListView;
    private TxtPropPreviewView mPreviewArea;
    private TxtPropListAdapter mPropListAdapter;

    private TxtPropItem mSelectedTxtProp;
    private CommentInfo mCommentInfo;
    private boolean isFullScreenMode;

    private PopupWindow mLockTipsPopupWindow;
    private PropLockInfo mCurrentLockInfo;
    private int mPropItemWidth = 0;
    private int mPropItemCnt = 0;

    private ITxtPropControlBarListener mTxtPropControlBarListener;

    public CommentTxtPropControlBar(Context context) {
        this(context, null);
    }

    public CommentTxtPropControlBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CommentTxtPropControlBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        Loger.d(TAG, "-->initView()");
        if (attrs != null) {
            TypedArray typeArray = context.obtainStyledAttributes(attrs, R.styleable.CommentTxtPropControlBar);
            if (typeArray != null) {
                try {
                    isFullScreenMode = typeArray.getBoolean(R.styleable.CommentTxtPropControlBar_fullScreenMode, false);
                } catch (Exception e) {
                    Loger.e(TAG, "exception: " + e);
                } finally {
                    typeArray.recycle();
                }
            }
        }

        LayoutInflater.from(context).inflate(R.layout.comment_prop_control_bar_layout, this, true);
        setOrientation(VERTICAL);
        mPropListView = (RecyclerViewEx) findViewById(R.id.comment_txt_prop_list);
        LinearLayoutManagerEx layoutManager = new LinearLayoutManagerEx(context);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mPropListView.setLayoutManager(layoutManager);
        mPropListAdapter = new TxtPropListAdapter(context, this);
        mPropListView.setAdapter(mPropListAdapter);
        mPropListView.setOnChildClickListener(this);

        mPreviewArea = (TxtPropPreviewView) findViewById(R.id.txt_preview_area);
        mPreviewArea.addAnimationListener(this);
        mPreviewArea.enableAutoSuffix(true);

        mPropItemWidth = CApplication.getDimensionPixelSize(R.dimen.comment_txt_prop_bar_item_width);
        initCommentContent();
    }

    private void initCommentContent() {
        mCommentInfo = CommentUtils.generateMyCommentInfo(null);
    }

    public void updateCommentContent(String commentContent) {
        mCommentInfo.setContent(commentContent);
        if (!isFullScreenMode) {
            mPreviewArea.updateContentData(mCommentInfo);
        }
    }

    public void updateTxtPropListData(List<IBeanItem> dataList) {
        List<IBeanItem> filterDataList = filterAndPreloadPropItemBgRes(dataList);

        if (!CollectionUtils.isEmpty(filterDataList)) {
            mPropListView.setVisibility(View.VISIBLE);
            //第一次加载列表时才可能预览草稿动效
            boolean needPreviewEE = mPropListAdapter.getCount() <= 0;
            addDataToPropList(filterDataList);

            if (mSelectedTxtProp != null && !CollectionUtils.isEmpty(filterDataList)) {
                int selectedItemIndex = -1;
                for (int i = 0; i < filterDataList.size(); i++) {
                    IBeanItem item = filterDataList.get(i);
                    if (item != null && item.getItemData() instanceof TxtPropItem) {
                        TxtPropItem propItem = (TxtPropItem) item.getItemData();
                        if (propItem.isTheSameItem(mSelectedTxtProp)) {
                            if (propItem.isLocked() || propItem.isRunningOut()) {
                                //草稿项已失效
                                mSelectedTxtProp = null;
                            } else {
                                selectedItemIndex = i;
                            }
                            break;
                        }
                    }
                }
                Loger.d(TAG, "-->updateTxtPropListData(), selectedItemIndex=" + selectedItemIndex);
                if (selectedItemIndex >= 0) {
                    if (needPreviewEE) {
                        loadDraftPropItem();
                    }
                    mPropListView.smoothScrollToPosition(selectedItemIndex);
                }
            }
        } else if (mPropListAdapter.getCount() <= 0) {
            mPropListView.setVisibility(View.GONE);
        }
    }

    private List<IBeanItem> filterAndPreloadPropItemBgRes(List<IBeanItem> dataList) {
        List<IBeanItem> resultList = null;
        mPropItemCnt = 0;
        if (!CollectionUtils.isEmpty(dataList)) {
            resultList = new ArrayList<>(CollectionUtils.sizeOf(dataList));
            for (IBeanItem beanItem : dataList) {
                Object itemDataObj = beanItem.getItemData();
                if (itemDataObj instanceof TxtPropItem) {
                    TxtPropItem itemData = (TxtPropItem) itemDataObj;
                    if (isSupportedPropItem(itemData)) {
                        if (itemData.isEnterEffectType() && !TextUtils.isEmpty(itemData.getPreviewBgUrl())) {
                            ImageFetcher.preloadImage(
                                    itemData.getPreviewBgUrl(),
                                    SystemUtil.getScreenWidthIntPx(),
                                    CApplication.getDimensionPixelSize(R.dimen.comment_txt_prop_preview_area_height),
                                    null);
                        }
                        resultList.add(beanItem);
                        mPropItemCnt++;
                    }
                } else {
                    //未知类型，如分割线
                    resultList.add(beanItem);
                }
            }
        }
        return resultList;
    }

    private void addDataToPropList(List<IBeanItem> dataList) {
        if (!CollectionUtils.isEmpty(dataList)) {
            mPropListAdapter.onDataSetChanged(dataList);
        }
    }

    private boolean isSupportedPropItem(TxtPropItem propItemData) {
        return propItemData != null && (propItemData.isColorType() || (!isFullScreenMode && (propItemData.isBGType() || propItemData.isEnterEffectType())));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (isFullScreenMode && mPropItemWidth > 0) {
            int targetWidth = (int) ((mPropItemCnt <= 4 ? mPropItemCnt : (4.5)) * mPropItemWidth);
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(targetWidth, MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void previewTextEffect() {
        Loger.d(TAG, "-->previewTextEffect(), isFullScreenMode=" + isFullScreenMode);
        if (!isFullScreenMode) {
            ImageFetcher.loadBitmap(mCommentInfo.getTxtPropItemBgResUrl(), 0, 0, new ImageFetcher.IBitmapLoadListener() {
                @Override
                public void onBitmapLoaded(String bitmapUrl, Bitmap resultBitmap) {
                    if (!TextUtils.isEmpty(bitmapUrl) && mCommentInfo != null && bitmapUrl.equals(mCommentInfo.getTxtPropItemBgResUrl())) {
                        mPreviewArea.updateContentData(mCommentInfo, resultBitmap);
                        mPreviewArea.playAnimation(YoyoPlayer.ANIM_TYPE_SCALE_IN_SHAKE_STAY);
                    }
                }

                @Override
                public void onBitmapLoadFailed(String bitmapUrl) {

                }
            });
        }
    }

    private void hidePreviewArea() {
        Loger.d(TAG, "-->hidePreviewArea(), isFullScreenMode=" + isFullScreenMode);
        if (mPreviewArea.getVisibility() == View.VISIBLE && !isFullScreenMode) {
            mPreviewArea.playAnimation(YoyoPlayer.ANIM_TYPE_FADE_OUT);
        }
    }

    @Override
    public boolean onChildClick(RecyclerViewEx parent, RecyclerViewEx.ViewHolderEx viewHolder) {
        if (ViewUtils.isFastDoubleClick()) {
            return false;
        }
        Object itemDataObj = viewHolder.getChildData();
        Loger.d(TAG, "-->onChildClick(), itemData=" + itemDataObj);
        if (itemDataObj instanceof TxtPropItem) {
            TxtPropItem tItem = (TxtPropItem) itemDataObj;
            mCurrentLockInfo = null;
            if (tItem.isLocked() || tItem.isRunningOut()) {
                showLockedItemTipsView(tItem, viewHolder.itemView);
            } else {
                if (tItem.isTheSameItem(mSelectedTxtProp)) {
                    mSelectedTxtProp = null;
                    hidePreviewArea();
                } else {
                    mSelectedTxtProp = tItem;
                    if (mSelectedTxtProp.isEnterEffectType()) {
                        mCommentInfo.setTxtPropInfo(tItem);
                        previewTextEffect();
                    } else {
                        hidePreviewArea();
                    }
                }
                refreshData();
            }

        }
        return false;
    }

    private void showLockedItemTipsView(TxtPropItem tItem, View anchorView) {
        dismissLockTipsPopupWindow();
        mCurrentLockInfo = tItem != null ? tItem.getLockInfo() : null;
        if (mCurrentLockInfo != null) {
            boolean needRelocate = relocateListIfNeeded(anchorView, mPropListView);
            UiThreadUtil.postDelay(() -> {
                        if (mCurrentLockInfo != null) {
                            Runnable mLockTipsClickListener = null;
                            if (mCurrentLockInfo.jumpData != null) {
                                mLockTipsClickListener = () -> {
                                    dismissLockTipsPopupWindow();
                                    if (mCurrentLockInfo != null && mCurrentLockInfo.jumpData != null) {
                                        if (JumpProxyManager.getInstance().jumpToActivity(getContext(), mCurrentLockInfo.jumpData) && mTxtPropControlBarListener != null) {
                                            mTxtPropControlBarListener.onLockTipsClicked(tItem);
                                        }
                                    }
                                    mCurrentLockInfo = null;
                                };
                            }
                            mLockTipsPopupWindow = PopupWindowWrapper.showPropPopupWindow(getContext(), anchorView,
                                    mCurrentLockInfo.title, mCurrentLockInfo.summary, mLockTipsClickListener, 3000);
                            if (mTxtPropControlBarListener != null) {
                                mTxtPropControlBarListener.onLockTipsShown(tItem);
                            }
                        }
                    },
                    needRelocate ? ANIMATE_RELOCATE_TIME : 0);
        }
    }

    private boolean relocateListIfNeeded(View anchorView, RecyclerViewEx recyclerView) {
        boolean animated = false;
        if (anchorView != null && recyclerView != null) {
            Rect anchorRect = new Rect();
            Rect recyclerViewRect = new Rect();
            recyclerView.getGlobalVisibleRect(recyclerViewRect);
            anchorView.getGlobalVisibleRect(anchorRect);
            if (Math.abs(anchorRect.left - anchorRect.right) < anchorView.getWidth()) {
                if (anchorRect.left <= recyclerViewRect.left) {
                    recyclerView.smoothScrollBy(-anchorView.getWidth(), 0);
                    animated = true;
                } else if (anchorRect.right >=
                        (SystemUtil.isLandscapeOrientation() ? SystemUtil.getScreenHeightIntPx() : SystemUtil.getScreenWidthIntPx())) {
                    recyclerView.smoothScrollBy(anchorView.getWidth(), 0);
                    animated = true;
                }
            }
        }
        return animated;
    }

    private void dismissLockTipsPopupWindow() {
        if (mLockTipsPopupWindow != null) {
            mLockTipsPopupWindow.dismiss();
            mLockTipsPopupWindow = null;
        }
    }

    private void refreshData() {
        mPropListAdapter.notifyDataSetChanged();
    }

    public TxtPropItem getSelectedTxtProp() {
        return mSelectedTxtProp;
    }

    public String getAutoSuffixStr() {
        return mSelectedTxtProp != null ? mSelectedTxtProp.getContentSuffix() : null;
    }

    @Override
    public boolean isSelectedItem(TxtPropItem propItem) {
        return mSelectedTxtProp != null && mSelectedTxtProp.isTheSameItem(propItem);
    }

    public void setInitTxtPropInfo(TxtPropItem propItem) {
        if (propItem != null && !propItem.isLocked() && !propItem.isRunningOut() && mSelectedTxtProp == null) {
            mSelectedTxtProp = propItem;
            loadDraftPropItem();
        }
    }

    /**
     * 待数据加载完毕，存在此前选定的道具项时，展示该道具项
     */
    private void loadDraftPropItem() {
        if (mSelectedTxtProp != null && mPropListAdapter.getCount() > 0) {
            mCommentInfo.setTxtPropInfo(mSelectedTxtProp);
            refreshData();

            if (mSelectedTxtProp.isEnterEffectType()) {
                previewTextEffect();
            }
        }
    }

    @Override
    public void onAnimationStart(BaseViewAnimator animation) {

    }

    @Override
    public void onAnimationStageChanged(BaseViewAnimator animation, int stage) {

    }

    @Override
    public void onAnimationEnd(BaseViewAnimator animation) {
        Loger.d(TAG, "-->onAnimationEnd(), animation=" + animation);
    }

    @Override
    public void onAnimationCancel(BaseViewAnimator animation) {

    }

    public void onDestroy() {
        dismissLockTipsPopupWindow();
    }

    public void applyTheme(int theme) {
        boolean darkMode = (CommentConstants.THEME_NIGHT == theme);
        mPropListView.setBackgroundColor(CApplication.getColorFromRes(darkMode ? R.color.comment_bar_night_background : R.color.comment_bar_background));
    }

    public void setTxtPropControlBarListener(ITxtPropControlBarListener txtPropControlBarListener) {
        this.mTxtPropControlBarListener = txtPropControlBarListener;
    }

    public interface ITxtPropControlBarListener {
        void onLockTipsClicked(TxtPropItem propItem);

        void onLockTipsShown(TxtPropItem propItem);
    }

    //Used for debug begin
    private void addTxtPropData() {
        List<IBeanItem> dataList = new ArrayList<>();
        dataList.add(CommonBeanItem.newInstance(TxtPropListAdapter.TYPE_ENTER_EFFECT, TxtPropItem.newInstance(TxtPropItem.TXT_PROP_TYPE_EE, "EE_1", "http://puui.qpic.cn/qqvideo_ori/0/p07403vb6dv_496_280/0")));
        dataList.add(CommonBeanItem.newInstance(TxtPropListAdapter.TYPE_ENTER_EFFECT, TxtPropItem.newInstance(TxtPropItem.TXT_PROP_TYPE_EE, "EE_2", "http://mat1.gtimg.com/sports/benchang/aoyun/team/yuan/NED.png")));
        dataList.add(CommonBeanItem.newInstance(TxtPropListAdapter.TYPE_ENTER_EFFECT, TxtPropItem.newInstance(TxtPropItem.TXT_PROP_TYPE_EE, "EE_3", "http://puui.qpic.cn/qqvideo_ori/0/i0740m1y61b_496_280/0")));
        dataList.add(CommonBeanItem.newInstance(TxtPropListAdapter.TYPE_ENTER_EFFECT, TxtPropItem.newInstance(TxtPropItem.TXT_PROP_TYPE_EE, "EE_4", "http://inews.gtimg.com/newsapp_bt/0/5715457957/1000")));
        dataList.add(CommonBeanItem.newInstance(ViewTypeConstant.COMMON_VERTICAL_LINE_SEP_TYPE, null));
        dataList.add(CommonBeanItem.newInstance(TxtPropListAdapter.TYPE_TXT_BG, TxtPropItem.newInstance(TxtPropItem.TXT_PROP_TYPE_BG, "BG_1")));
        dataList.add(CommonBeanItem.newInstance(TxtPropListAdapter.TYPE_TXT_BG, TxtPropItem.newInstance(TxtPropItem.TXT_PROP_TYPE_BG, "BG_2")));
        dataList.add(CommonBeanItem.newInstance(TxtPropListAdapter.TYPE_TXT_BG, TxtPropItem.newInstance(TxtPropItem.TXT_PROP_TYPE_BG, "BG_3")));
        dataList.add(CommonBeanItem.newInstance(ViewTypeConstant.COMMON_VERTICAL_LINE_SEP_TYPE, null));
        dataList.add(CommonBeanItem.newInstance(TxtPropListAdapter.TYPE_TXT_COLOR, TxtPropItem.newInstance(TxtPropItem.TXT_PROP_TYPE_COLOR, "Color_1")));
        dataList.add(CommonBeanItem.newInstance(TxtPropListAdapter.TYPE_TXT_COLOR, TxtPropItem.newInstance(TxtPropItem.TXT_PROP_TYPE_COLOR, "Color_2")));
        dataList.add(CommonBeanItem.newInstance(TxtPropListAdapter.TYPE_TXT_COLOR, TxtPropItem.newInstance(TxtPropItem.TXT_PROP_TYPE_COLOR, "Color_3")));

        addDataToPropList(filterAndPreloadPropItemBgRes(dataList));
    }
    //Used for debug end
}
