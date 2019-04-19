package com.tencent.qqsports.commentbar.submode;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.loading.common.component.CApplication;
import com.loading.common.utils.CommonUtils;
import com.loading.common.utils.Loger;
import com.loading.common.utils.SystemUtils;
import com.loading.common.widget.CirclePageIndicator;
import com.loading.common.widget.ViewPagerEX;
import com.loading.common.widget.ViewPagerProxy;
import com.tencent.qqsports.commentbar.R;
import com.tencent.qqsports.commentbar.adapter.FacePagerAdapter;
import com.tencent.qqsports.commentbar.view.FacePackageIndicatorView;
import com.tencent.qqsports.face.BaseFacePackage;
import com.tencent.qqsports.face.FaceManager;
import com.tencent.qqsports.face.FacePageItems;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("ResourceAsColor")
public class FacePanelFragment extends PanelModeBaseFragment implements ViewPager.OnPageChangeListener, View.OnClickListener, IFacePanelListener {
    private static final String TAG = FacePanelFragment.class.getSimpleName();
    private ViewPagerEX mPanelViewPager;
    private CirclePageIndicator mIndicator;
    private LinearLayout mPackageIndicatorContainer;
    private ViewPagerProxy mViewPagerProxy; //用于ViewPage的页码转换
    private FacePagerAdapter mViewPagerAdapter;

    private List<BaseFacePackage> mFacePackageList;
    private IFaceItemLongPressListener mFaceItemLongPressListener;
    private boolean isEditTextViewNotEmpty = false; //监听输入框内容是否发生了变化，以此更改退格按钮的状态

    public static FacePanelFragment newInstance(int panelTargetHeight) {
        FacePanelFragment facePanel = new FacePanelFragment();
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
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.facepanel_view_layout;
    }

    @Override
    protected void initView(Context context) {
        mPanelViewPager = mRootView.findViewById(R.id.pager);
        mIndicator = mRootView.findViewById(R.id.indicator);
        mPackageIndicatorContainer = mRootView.findViewById(R.id.package_indicator_container);
        mFinishBtn = mRootView.findViewById(R.id.btn_finish);
        initFaceView();
        updateFinishBtnEnableStatus();
    }

    private void initFaceView() {
        mFacePackageList = FaceManager.getInstance().getAvailablePackageList();

        int faceItemSize = CApplication.getDimensionPixelSize(R.dimen.comment_face_panel_content_item_size);
        int bottomBarHeight = CApplication.getDimensionPixelSize(R.dimen.face_cb_height);
        int gridContentItemPaddingLR = CApplication.getDimensionPixelSize(R.dimen.comment_face_panel_content_item_padding_LR);  //GridView 中每一项的左右padding
        int gridContentContainerPaddingLR = CApplication.getDimensionPixelSize(R.dimen.comment_face_panel_content_padding_LR);  //GridView 左右padding
        int pageIndicatorMaintainHeight = SystemUtils.dpToPx(20);
        int gridViewVerticalSpacing = (mPanelTargetHeight - bottomBarHeight - pageIndicatorMaintainHeight - 3 * faceItemSize) / 5;
        int faceItemContainerWidth = faceItemSize + gridContentItemPaddingLR * 2;
        int colNum = (SystemUtils.getRealTimeScreenWidthIntPx(getContext()) - 2 * gridContentContainerPaddingLR) / faceItemContainerWidth;

        List<FacePageItems> facePageList = new ArrayList<>(5);
        mPackageIndicatorContainer.removeAllViews();
        for (int i = 0; i < mFacePackageList.size(); i++) {
            BaseFacePackage facePackage = mFacePackageList.get(i);
            if (facePackage != null && facePackage.isPackageValid()) {
                facePackage.initRowColumnLayout(3, colNum, gridContentItemPaddingLR, gridViewVerticalSpacing);
                List<FacePageItems> tFacePageList = facePackage.getFacePageList();
                if (!CommonUtils.isEmpty(tFacePageList)) {
                    int packageStartIndex = facePageList.size();
                    FacePackageIndicatorView indicatorView = new FacePackageIndicatorView(getContext());
                    indicatorView.setPackageStartIndexInViewPager(packageStartIndex);
                    indicatorView.setPackageIndicatorRes(facePackage.getPackageIndicatorRes());
                    indicatorView.setOnClickListener(this);
                    if (i == 0) {
                        indicatorView.setSelected(true);
                    }
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(CApplication.getDimensionPixelSize(R.dimen.face_switch_btn_width),
                            CApplication.getDimensionPixelSize(R.dimen.face_switch_btn_height));
                    mPackageIndicatorContainer.addView(indicatorView, lp);

                    facePageList.addAll(facePackage.getFacePageList());
                }
            }
        }

        mViewPagerAdapter = new FacePagerAdapter(getContext(), facePageList, mGridViewItemClickListener, mFaceItemLongPressListener, this);
        mPanelViewPager.setAdapter(mViewPagerAdapter);
        initViewPagerProxy();
        mPanelViewPager.addOnPageChangeListener(mViewPagerProxy);
        mIndicator.setViewPager(mViewPagerProxy);
        mPanelViewPager.addOnPageChangeListener(this);
        mViewPagerProxy.onPageSelected(0);
    }

    private void initViewPagerProxy() {
        mViewPagerProxy = new ViewPagerProxy();
        mViewPagerProxy.setViewPagerCallback(new ViewPagerProxy.ViewPagerCallback() {
            @Override
            public int getGroupIndex(int position) {
                int groupIndex = 0;
                int totalCnt = 0;
                for (int i = 0; i < mFacePackageList.size(); i++) {
                    totalCnt += mFacePackageList.get(i).getFacePageCnt();
                    if (totalCnt > position) {
                        groupIndex = i;
                        break;
                    }
                }
//                Loger.d(TAG, "-->getGroupIndex(), position=" + position + ", groupIndex=" + groupIndex);
                return groupIndex;
            }

            @Override
            public int getChildCount(int index) {
                int cnt = 0;
                if (index >= 0 && mFacePackageList.size() > index) {
                    cnt = mFacePackageList.get(index).getFacePageCnt();
                }
//                Loger.d(TAG, "-->getChildCount(), index=" + index + ", cnt=" + cnt);
                return cnt;
            }

            @Override
            public int getChildPosition(int position) {
                int newPos = position;
                int totalCnt = 0;
                for (int i = 0; i < mFacePackageList.size(); i++) {
                    int curPageCnt = mFacePackageList.get(i).getFacePageCnt();
                    if (totalCnt + curPageCnt > position) {
                        newPos = position - totalCnt;
                        break;
                    }
                    totalCnt += curPageCnt;
                }
//                Loger.d(TAG, "-->getChildPosition(), position=" + position + ", newPos=" + newPos);
                return newPos;
            }
        });
    }

    @Override
    public void updateFinishBtnEnableStatus() {
        super.updateFinishBtnEnableStatus();
        boolean existTxtContent = isExistTxtContent();
        Loger.d(TAG, "-->updateFinishBtnEnableStatus(), existTxtContent=" + existTxtContent + ", former not empty status=" + isEditTextViewNotEmpty);
        if (isEditTextViewNotEmpty != existTxtContent) {
            mViewPagerAdapter.notifyDataSetChanged();
            isEditTextViewNotEmpty = existTxtContent;
        }
    }

    @Override
    public void hide() {
        super.hide();
        if (mFaceItemLongPressListener != null) {
            mFaceItemLongPressListener.exitLongPressState();
        }
    }

    @Override
    public void onClick(View v) {
        if (v instanceof FacePackageIndicatorView) {
            FacePackageIndicatorView packageIndicatorView = (FacePackageIndicatorView) v;
            boolean checked = packageIndicatorView.isSelected();
            if (!checked) {
                packageIndicatorView.setSelected(true);
                mPanelViewPager.setCurrentItem(packageIndicatorView.getPackageStartIndexInViewPager());
            }
        }
    }

    public void setFaceItemLongPressListener(IFaceItemLongPressListener faceItemLongPressListener) {
        this.mFaceItemLongPressListener = faceItemLongPressListener;
    }

    private void insertContentAtCursor(SpannableStringBuilder spannableStringBuilder, EditText editText) {
        if (editText != null) {
            int index = editText.getSelectionStart();
            Editable editable = editText.getText();
            editable.insert(index, spannableStringBuilder);
            Loger.d(TAG, "----->insertContentAtCursor()----index:" + index + "， content: " + spannableStringBuilder);
        }
    }

    private AdapterView.OnItemClickListener mGridViewItemClickListener = (parent, view, position, id) -> {
        if (parent != null && parent.getTag() != null && parent.getTag() instanceof FacePageItems) {
            FacePageItems facePageItems = (FacePageItems) parent.getTag();

            if (position == facePageItems.getFaceCntPerPage()) {
                KeyEvent event = new
                        KeyEvent(System.currentTimeMillis(),
                        System.currentTimeMillis(),
                        KeyEvent.ACTION_DOWN,
                        KeyEvent.KEYCODE_DEL, 0, 0, -1, 0, 0x6);
                if (mEditText != null) {
                    mEditText.dispatchKeyEvent(event);
                }
                return;
            }

            String faceStr = facePageItems.getFaceStringAtGroupPosition(position);
            SpannableStringBuilder spanStr = FaceManager.getInstance().convertToSpannableStr(faceStr, mEditText);
            Loger.d(TAG, "clicked item page index=" + facePageItems.getPageIndexInFacePackage() + ", pos in page=" + position + "， spanStr=" + spanStr);
            if (spanStr != null) {
                insertContentAtCursor(spanStr, mEditText);
            }
        }
    };

    private void updatePackageIndicator(int curSelectedPageIndex) {
        Loger.d(TAG, "-->updatePackageIndicator(), curSelectedPageIndex=" + curSelectedPageIndex);
        boolean found = false;
        for (int i = mPackageIndicatorContainer.getChildCount() - 1; i >= 0; i--) {
            View childView = mPackageIndicatorContainer.getChildAt(i);
            if (childView instanceof FacePackageIndicatorView) {
                FacePackageIndicatorView indicatorView = (FacePackageIndicatorView) childView;
                if (!found && indicatorView.getPackageStartIndexInViewPager() <= curSelectedPageIndex) {
                    found = true;
                    indicatorView.setSelected(true);
                } else {
                    indicatorView.setSelected(false);
                }
            }
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        updatePackageIndicator(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public boolean needEnableDeleteBtn() {
        return isExistTxtContent();
    }
}
