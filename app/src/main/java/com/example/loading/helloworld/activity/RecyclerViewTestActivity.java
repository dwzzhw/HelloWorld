package com.example.loading.helloworld.activity;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.loading.helloworld.R;
import com.loading.common.component.BaseActivity;
import com.loading.common.utils.CommonUtil;
import com.loading.common.utils.Loger;
import com.loading.common.utils.SystemUtil;

public class RecyclerViewTestActivity extends BaseActivity {
    private static final String TAG = "RecyclerViewTestActivity";

    private RecyclerView mRecyclerView;
    private EditText mStyleValueET;
    private MyAdapter mAdapter;
    private static int mLineOrColumnCnt = 3;
    private static int mItemMargin = SystemUtil.dpToPx(4);
//    private static int mSeparatorWidth = SystemUtil.dpToPx(10);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_recycler_view_test);
        mStyleValueET = findViewById(R.id.style_value);
        mStyleValueET.setHint("1: LV 2:LH 3:GV 4:GH 5:SGV 6:SGH");

        initRecyclerView();
    }

    private void initRecyclerView() {
        Loger.d(TAG, "-->initRecyclerView()");
        mRecyclerView = findViewById(R.id.recycler_view);
        mAdapter = new MyAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
//                outRect.set(0, 0, isHorizontalLayout() ? mItemMargin : 0, isHorizontalLayout() ? 0 : mItemMargin);
//                outRect.set(mItemMargin, mItemMargin, mItemMargin, mItemMargin);
                outRect.set(mItemMargin, mItemMargin, mItemMargin, mItemMargin);
            }
        });
    }

    public void onBtnClicked(View view) {
        Loger.d(TAG, "-->onBtnClicked()");
        if (view.getId() == R.id.btn_change_style) {
            changeLayoutStyle();
        }
    }

    private boolean isHorizontalLayout() {
        return mCurrentOrientation == RecyclerView.HORIZONTAL;
    }

    private void updateLayoutManager() {
        RecyclerView.LayoutManager lm = null;
        int orientation = RecyclerView.HORIZONTAL;
        switch (mCurrentStyle) {
            case 1:
                lm = new LinearLayoutManager(this);
                ((LinearLayoutManager) lm).setOrientation(LinearLayoutManager.VERTICAL);
                orientation = RecyclerView.VERTICAL;
                mMinusSizeBeforeDivider = mItemMargin * mLineOrColumnCnt * 2;
                break;
            case 2:
                lm = new LinearLayoutManager(this);
                ((LinearLayoutManager) lm).setOrientation(LinearLayoutManager.HORIZONTAL);
                mMinusSizeBeforeDivider = mItemMargin * mLineOrColumnCnt * 2;
                break;
            case 3:
                lm = new GridLayoutManager(this, mLineOrColumnCnt);
                ((GridLayoutManager) lm).setOrientation(LinearLayoutManager.VERTICAL);
                orientation = RecyclerView.VERTICAL;
                mMinusSizeBeforeDivider = mItemMargin * mLineOrColumnCnt * 2;
                break;
            case 4:
                lm = new GridLayoutManager(this, mLineOrColumnCnt);
                ((GridLayoutManager) lm).setOrientation(LinearLayoutManager.HORIZONTAL);
                mMinusSizeBeforeDivider = mItemMargin * mLineOrColumnCnt * 2;
                break;
            case 5:
                lm = new StaggeredGridLayoutManager(mLineOrColumnCnt, RecyclerView.VERTICAL);
                orientation = RecyclerView.VERTICAL;
                mMinusSizeBeforeDivider = 0;
                break;
            case 6:
                lm = new StaggeredGridLayoutManager(mLineOrColumnCnt, RecyclerView.HORIZONTAL);
                mMinusSizeBeforeDivider = 0;
                break;

        }
        mCurrentOrientation = orientation;
        mAdapter.updateOrientation(orientation, mContainerWidth, mContainerHeight);
        mRecyclerView.setLayoutManager(lm);
        mAdapter.notifyDataSetChanged();
    }

    private int mCurrentStyle;
    private int mCurrentOrientation;
    private int mContainerWidth;
    private int mContainerHeight;
    private static int mMinusSizeBeforeDivider;

    private void changeLayoutStyle() {
        int newValueStr = CommonUtil.optInt(mStyleValueET.getText().toString());
        if (newValueStr != mCurrentStyle) {
            mCurrentStyle = newValueStr;
            mContainerWidth = mRecyclerView.getWidth();
            mContainerHeight = mRecyclerView.getHeight();
            updateLayoutManager();
        }
    }

    private static class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
        private Context mContext;
        private int mOrientation;
        private int mContainerWidth;
        private int mContainerHeight;

        public MyAdapter(Context context) {
            mContext = context;
        }

        public void updateOrientation(int orientation, int containerWidth, int containerHeight) {
            Loger.d(TAG, "-->updateOrientation(), orientation=" + orientation + ", width=" + containerWidth + ", height=" + containerHeight);
            mOrientation = orientation;
            mContainerWidth = containerWidth;
            mContainerHeight = containerHeight;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(mContext).inflate(R.layout.recycler_view_item, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            holder.fillData(position, mOrientation, mContainerWidth, mContainerHeight);
        }

        @Override
        public int getItemCount() {
            return 200;
        }
    }

    private static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView mTitleView;
        private TextView mSummaryView;

        public MyViewHolder(View itemView) {
            super(itemView);
            mTitleView = itemView.findViewById(R.id.title);
            mSummaryView = itemView.findViewById(R.id.summary);
        }

        private void fillData(int position, int orientation, int containerWidth, int containerHeight) {
            mTitleView.setText("Title_" + (position + 1));
            mSummaryView.setText("Summary_" + (position + 1));

            ViewGroup.LayoutParams lp = itemView.getLayoutParams();
            if (lp != null) {
                if (orientation == RecyclerView.HORIZONTAL) {
                    lp.width = SystemUtil.dpToPx(120 + (int) (Math.random() * 200));
                    lp.height = (containerHeight - mMinusSizeBeforeDivider) / mLineOrColumnCnt;
                } else {
                    lp.height = SystemUtil.dpToPx(80 + (int) (Math.random() * 200));
                    lp.width = (containerWidth - mMinusSizeBeforeDivider) / mLineOrColumnCnt;
//                    lp.width = (containerWidth) / mLineOrColumnCnt;
                }
                itemView.setLayoutParams(lp);
            }

            itemView.setBackgroundColor(Color.argb(50, (int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255)));
        }
    }
}
