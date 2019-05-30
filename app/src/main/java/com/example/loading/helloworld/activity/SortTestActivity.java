package com.example.loading.helloworld.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.loading.helloworld.R;
import com.loading.common.component.BaseActivity;
import com.loading.common.utils.CommonUtil;
import com.loading.common.utils.Loger;

public class SortTestActivity extends BaseActivity {
    private static final String TAG = "SortTestActivity";
    private EditText mInputEditText;
    private TextView mOutputTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_order_test);

        mInputEditText = findViewById(R.id.input_value);
        mOutputTextView = findViewById(R.id.output_value);
    }

    public void onBtnClicked(View view) {
        Loger.d(TAG, "-->onBtnClicked()");
        int[] inputArray = getInputArray();
        long costTime = 0;
        if (view.getId() == R.id.btn_bubble) {
            costTime = doBubbleSort(inputArray);
        } else if (view.getId() == R.id.btn_merge) {
            costTime = doMergeSort(inputArray);
        }
        outputResult(inputArray, costTime);
    }

    private int[] getInputArray() {
        int[] inputArray;
        String inputStr = mInputEditText.getText().toString();
        if (!TextUtils.isEmpty(inputStr)) {
            String[] strList = inputStr.split(",");
            inputArray = new int[strList.length];
            for (int i = 0; i < strList.length; i++) {
                inputArray[i] = CommonUtil.optInt(strList[i]);
            }
        } else {
            inputArray = new int[100];
            for (int i = 0; i < 100; i++) {
                inputArray[i] = 100 - i;
            }
        }
        return inputArray;
    }

    private void outputResult(int[] inputArray, long costTime) {
        StringBuilder builder = new StringBuilder();
        builder.append("[\n");
        for (int i = 0; i < inputArray.length; i++) {
            if (i > 0) {
                builder.append(", ");
                if (i % 10 == 0) {
                    builder.append("\n");
                }
            }
            builder.append(inputArray[i]);
        }
        builder.append("\n] costTime: ")
                .append(costTime)
                .append(" ns");

        mOutputTextView.setText(builder);
    }

    private void swapOrder(int[] arr, int firstIndex, int secondIndex) {
        int tmp = arr[firstIndex];
        arr[firstIndex] = arr[secondIndex];
        arr[secondIndex] = tmp;
    }

    private long doBubbleSort(int[] arr) {
        long startTime = System.nanoTime();
        int len = arr.length;
        for (int i = 0; i < len - 1; i++) {
            for (int j = len - 1; j > i; j--) {
                if (arr[j] < arr[j - 1]) {
                    swapOrder(arr, j, j - 1);
                }
            }
        }
        long costTime = System.nanoTime() - startTime;
        Loger.d(TAG, "-->doBubbleSort(), cost time=" + costTime + " ns");
        return costTime;
    }

    /**
     * Merge Order 归并排序
     * https://www.cnblogs.com/chengxiao/p/6194356.html
     */
    private long doMergeSort(int[] arr) {
        long startTime = System.nanoTime();
        int[] tmp = new int[arr.length];
        mergeSort(arr, 0, arr.length - 1, tmp);

        long costTime = System.nanoTime() - startTime;
        Loger.d(TAG, "-->doMergeSort(), cost time=" + costTime + " ns");
        return costTime;
    }

    private void mergeSort(int[] arr, int left, int right, int[] tmp) {
        if (left < right) {
            int mid = (left + right) / 2;
            mergeSort(arr, left, mid, tmp);
            mergeSort(arr, mid + 1, right, tmp);
            mergeBack(arr, left, mid, right, tmp);
        }
    }

    private void mergeBack(int[] arr, int left, int mid, int right, int[] tmp) {
        int i = left;
        int j = mid + 1;
        int t = 0;
        while (i <= mid && j <= right) {
            if (arr[i] < arr[j]) {
                tmp[t++] = arr[i++];
            } else {
                tmp[t++] = arr[j++];
            }
        }
        while (i <= mid) {
            tmp[t++] = arr[i++];
        }
        while (j <= right) {
            tmp[t++] = arr[j++];
        }

        t = 0;
        while (left <= right) {
            arr[left++] = tmp[t++];
        }
    }


}
