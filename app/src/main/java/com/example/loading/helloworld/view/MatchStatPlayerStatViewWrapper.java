package com.example.loading.helloworld.view;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.loading.helloworld.R;

public class MatchStatPlayerStatViewWrapper {
    private static final String TAG = MatchStatPlayerStatViewWrapper.class.getSimpleName() + "_dwz";
    protected Context mContext;
    protected View convertView;

    private TextView mTitleView = null;
    private LayoutInflater mLayoutInflater = null;
    private LinearLayoutManager mLayoutManager = null;
    private LinearLayout playerNameColumn = null;
    private RecyclerView playerDataView = null;
    private PlayerDataColumnViewAdapter playerDataAdapter = null;

    // 包括第一行的标签，所以此值实为球员数+1
    private int lineCount = 0;
    private int dataRowHeight = 0;
    private int labelRowHeight = 0;
    private static LinearLayout.LayoutParams dataItemLp = null;

    public MatchStatPlayerStatViewWrapper(Context context) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);

        dataRowHeight = mContext.getResources().getDimensionPixelSize(R.dimen.match_detail_data_player_stat_value_row_height);
        labelRowHeight = mContext.getResources().getDimensionPixelSize(R.dimen.match_detail_data_player_stat_label_row_height);
        Log.d(TAG, "MatchStatPlayerStatViewWrapper<>, dataRowHeight=" + dataRowHeight + ", labelRowHeight=" + labelRowHeight);
        dataItemLp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dataRowHeight);
    }

    public View inflateConvertView(ViewGroup parent) {
        convertView = mLayoutInflater.inflate(R.layout.sport_detail_data_basketball_player_stat, parent, false);
        playerNameColumn = (LinearLayout) convertView.findViewById(R.id.sport_deatail_player_stat_name_column);
        playerDataView = (RecyclerView) convertView.findViewById(R.id.recyler_view);
        mLayoutManager = new LinearLayoutManager(mContext);
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        playerDataView.setLayoutManager(mLayoutManager);
        mTitleView = (TextView) convertView.findViewById(R.id.table_title);
        return convertView;
    }

    public void fillDataToView(String tableTitle, String[][] playerDatas) {
        Log.d(TAG, "-->fillDataToView(), data size=" + (playerDatas == null ? "Null" : playerDatas.length));
        lineCount = (playerDatas != null && playerDatas.length > 0 ? playerDatas[0].length : 0);
        if (lineCount > 0) {
            if (!TextUtils.isEmpty(tableTitle)) {
                mTitleView.setVisibility(View.VISIBLE);
                mTitleView.setText(tableTitle);
            } else {
                mTitleView.setVisibility(View.GONE);
            }
//            fillDataToView(playerNames, playerNameColumn, true, false);

            // Add 1px seperator line height
            int viewTotalHeight = labelRowHeight + dataRowHeight * (lineCount - 1) + 1;
            RelativeLayout.LayoutParams tipsLayerLP = new RelativeLayout.LayoutParams(40, viewTotalHeight);
            tipsLayerLP.addRule(RelativeLayout.RIGHT_OF, R.id.sport_deatail_player_stat_name_column);

            Log.d(TAG, "-->fillDataToView(), statData.playerDatas=" + playerDatas + ", viewTotalHeight=" + viewTotalHeight);
            if (playerDatas != null) {
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, viewTotalHeight);
                lp.addRule(RelativeLayout.RIGHT_OF, R.id.sport_deatail_player_stat_name_column);
                lp.addRule(RelativeLayout.BELOW, R.id.table_title);
                playerDataView.setLayoutParams(lp);

                if (playerDataAdapter == null) {
                    playerDataAdapter = new PlayerDataColumnViewAdapter(playerDatas);
                    playerDataView.setAdapter(playerDataAdapter);
                } else {
                    playerDataAdapter.updateData(playerDatas);
                    playerDataAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    private void fillDataToView(String[] dataStr, LinearLayout containerView, boolean isNameColumn, boolean needLeftBorder) {
        if (dataStr != null && containerView != null && dataStr.length > 0) {
            int i = 0;
            for (; i < dataStr.length; i++) {
                View itemView = containerView.getChildAt(i);
                View textViewContainer = null;
                if (itemView != null) {
                    textViewContainer = itemView;
                } else {
                    textViewContainer = createItemView(isNameColumn);
                    containerView.addView(textViewContainer, i, dataItemLp);
                }
                View contentView = textViewContainer.findViewById(R.id.txt_content);
                if (contentView != null && contentView instanceof TextView) {
                    ((TextView) contentView).setText(dataStr[i]);
                }

                View leftBorder = textViewContainer.findViewById(R.id.left_border);
                if (leftBorder != null) {
                    leftBorder.setVisibility(needLeftBorder ? View.VISIBLE : View.GONE);
                }

                Log.d(TAG, "-->fillDataToView(), data=" + dataStr[i]);
            }
            int childViewCount = containerView.getChildCount();
            for (int j = childViewCount - 1; j >= i + 1; j--) {
                containerView.removeViewAt(j);
            }
        }
    }

    private View createItemView(boolean isNameColumn) {
        View itemView = null;
        if (isNameColumn) {
            itemView = mLayoutInflater.inflate(R.layout.sport_detail_data_player_stat_name_item, null);
        } else {
            itemView = mLayoutInflater.inflate(R.layout.sport_detail_data_player_stat_value_item, null);
        }
        return itemView;
    }

    private class PlayerDataColumnViewAdapter extends RecyclerView.Adapter<ViewHolder> {
        private String[][] statData = null;

        public PlayerDataColumnViewAdapter(String[][] statData) {
            this.statData = statData;
        }

        @Override
        public int getItemCount() {
            int itemCount = statData != null ? statData.length : 0;
            Log.d(TAG, "getItemCount(), itemCount=" + itemCount);
            return itemCount;
        }

        public void updateData(String[][] statData) {
            this.statData = statData;
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int pos) {
            View itemView = viewHolder.itemView;
            if (itemView != null && itemView instanceof LinearLayout) {
                fillDataToView(statData[pos], (LinearLayout) itemView, false, pos == 0);
            } else {
                Log.e(TAG, "container view is empty or non LinearLayout");
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int pos) {
            View rootView = mLayoutInflater.inflate(R.layout.sport_detail_data_player_stat_value_column, parent, false);
            ViewHolder viewHolder = new ViewHolder(rootView) {
            };
            return viewHolder;
        }
    }
}
