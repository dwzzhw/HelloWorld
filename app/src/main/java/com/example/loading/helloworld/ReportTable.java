package com.example.loading.helloworld;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.example.loading.helloworld.view.MatchStatPlayerStatViewWrapper;

/**
 * Created by loading on 3/17/16.
 */
public class ReportTable extends Activity {
    private static final String TAG = "ReportTable";
    private static final int LINE_CNT = 10;
    private static final int COLUMN_CNT = 20;

    MatchStatPlayerStatViewWrapper mViewWrapper = null;
    private ListView mTableList = null;
    private ListAdapter mTableAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.report_table);
        mTableList = (ListView)findViewById(R.id.table_list);
        mTableAdapter = new ReportTableListAdapter(this);
        mTableList.setAdapter(mTableAdapter);


        mViewWrapper = new MatchStatPlayerStatViewWrapper(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

}
