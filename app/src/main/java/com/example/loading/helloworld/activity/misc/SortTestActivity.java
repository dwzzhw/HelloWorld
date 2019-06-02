package com.example.loading.helloworld.activity.misc;

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
        } else if (view.getId() == R.id.btn_select) {
            costTime = doSimpleSelectSort(inputArray);
        } else if (view.getId() == R.id.btn_direct_insert) {
            costTime = doDirectInsertSort(inputArray);
        } else if (view.getId() == R.id.btn_shell) {
            costTime = doShellSort(inputArray);
        } else if (view.getId() == R.id.btn_heap) {
            costTime = doHeapSort(inputArray);
        } else if (view.getId() == R.id.btn_quick) {
            costTime = doQuickSort(inputArray);
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

    private void swapOrder(int[] arr, int i, int j) {
//        int tmp = arr[i];
//        arr[i] = arr[j];
//        arr[j] = tmp;

        arr[i] = arr[i] + arr[j];
        arr[j] = arr[i] - arr[j];
        arr[i] = arr[i] - arr[j];

//        //这种方式比临时变量并没有明显更快，但是可以省空间
//        arr[i] = arr[i] ^ arr[j];
//        arr[j] = arr[i] ^ arr[j];
//        arr[i] = arr[i] ^ arr[j];
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
     * 简单选择排序
     *
     * @param arr
     * @return
     */
    private long doSimpleSelectSort(int[] arr) {
        long startTime = System.nanoTime();
        int len = arr.length;
        int min;
        for (int i = 0; i < len - 1; i++) {
            min = i;
            for (int j = i + 1; j <= len - 1; j++) {
                if (arr[min] > arr[j]) {
                    min = j;
                }
            }
            if (min != i) {
                swapOrder(arr, i, min);
            }
        }

        long costTime = System.nanoTime() - startTime;
        Loger.d(TAG, "-->doSimpleSelectSort(), cost time=" + costTime + " ns");
        return costTime;
    }

    /**
     * 直接插入排序
     *
     * @param arr
     * @return
     */
    private long doDirectInsertSort(int[] arr) {
        long startTime = System.nanoTime();
        int len = arr.length;
        int min;
        for (int i = 1; i <= len - 1; i++) {

            int j = i;
            while (j > 0 && arr[j] < arr[j - 1]) {
                swapOrder(arr, j, j - 1);
                j--;
            }
        }

        long costTime = System.nanoTime() - startTime;
        Loger.d(TAG, "-->doDirectInsertSort(), cost time=" + costTime + " ns");
        return costTime;
    }

    /**
     * 希尔排序
     * <p>
     * https://www.cnblogs.com/chengxiao/p/6104371.html
     *
     * @param arr
     * @return
     */
    private long doShellSort(int[] arr) {
        long startTime = System.nanoTime();
        int len = arr.length;

        for (int gap = len / 2; gap > 0; gap = gap / 2) {
            for (int i = gap; i < len; i++) {
                int j = i;
                while (j >= gap && arr[j] < arr[j - gap]) {
                    swapOrder(arr, j, j - gap);
                    j -= gap;
                }
            }
        }
        long costTime = System.nanoTime() - startTime;
        Loger.d(TAG, "-->doShellSort(), cost time=" + costTime + " ns");
        return costTime;
    }

    /**
     * 堆排序
     * <p>
     * https://www.cnblogs.com/chengxiao/p/6129630.html#4257851
     *
     * @param arr
     * @return
     */
    private long doHeapSort(int[] arr) {
        long startTime = System.nanoTime();
        int len = arr.length;

        //1.构建大顶堆
        for (int i = len / 2 - 1; i >= 0; i--) {
            //从第一个非叶子结点从下至上，从左至右调整结构
            adjustHeap(arr, i, len);
        }

        //2.调整堆结构+交换堆顶元素与末尾元素
        for (int j = len - 1; j > 0; j--) {
            swapOrder(arr, 0, j);
            adjustHeap(arr, 0, j);
        }
        long costTime = System.nanoTime() - startTime;
        Loger.d(TAG, "-->doHeapSort(), cost time=" + costTime + " ns");
        return costTime;
    }

    private void adjustHeap(int[] arr, int i, int len) {
        int temp = arr[i];
        for (int j = 2 * i + 1; j < len; j = j * 2 + 1) {
            if (j + 1 < len && arr[j] < arr[j + 1]) {
                j++;
            }
            if (arr[j] > temp) {
                arr[i] = arr[j];
                i = j;
            } else {
                break;
            }
        }
        arr[i] = temp;

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


    private long doQuickSort(int[] arr) {
        long startTime = System.nanoTime();
        quickSort(arr, 0, arr.length - 1);
        long costTime = System.nanoTime() - startTime;
        Loger.d(TAG, "-->doQuickSort(), cost time=" + costTime + " ns");
        return costTime;
    }

    private void quickSort(int[] arr, int l, int r) {
        if (l < r) {
            int i = l, j = r, flag = arr[l];
            while (i < j) {
                while (j > i && arr[j] >= flag) {
                    j--;
                }
                if (j > i) {
                    arr[i++] = arr[j];
                }
                while (i < j && arr[i] <= flag) {
                    i++;
                }
                if (i < j) {
                    arr[j--] = arr[i];
                }
            }
            arr[i] = flag;
            quickSort(arr, l, i - 1);
            quickSort(arr, i + 1, r);
        }
    }
}
