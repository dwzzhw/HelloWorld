package com.example.loading.helloworld;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.loading.helloworld.view.MatchStatPlayerStatViewWrapper;

/**
 * Created by loading on 3/17/16.
 */
public class ReportTableListAdapter extends BaseAdapter {
    private Context mContext = null;
    private static final int LINE_CNT = 10;
    private static final int COLUMN_CNT = 20;

    public ReportTableListAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        return 10;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MatchStatPlayerStatViewWrapper wrapper = null;
        if (convertView == null) {
            wrapper = new MatchStatPlayerStatViewWrapper(mContext);
            convertView = wrapper.inflateConvertView(parent);
            convertView.setTag(wrapper);
        } else {
            wrapper = (MatchStatPlayerStatViewWrapper) convertView.getTag();
        }

        if (wrapper != null) {
            wrapper.fillDataToView("Table_" + (position + 1), getMockData(position));
        }
        return convertView;
    }

    /**
     * 所提供数据需要有个行列转换， 第一维度代表列数，第二维度代表行数
     * @param position
     * @return
     */
    private String[][] getMockData(int position) {
        String[][] dataArray = new String[COLUMN_CNT][position+2];
        for (int i = 0; i < position + 2; i++) {
            for (int j = 0; j < COLUMN_CNT; j++) {
                dataArray[j][i] = position + "_" + (i + 1) + "_" + (j + 1);
            }
        }
        return dataArray;
    }
}
