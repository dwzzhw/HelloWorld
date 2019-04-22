package com.example.loading.helloworld.download;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.loading.helloworld.R;
import com.loading.common.utils.Loger;
import com.loading.modules.interfaces.download.DownloadListener;
import com.loading.modules.interfaces.download.DownloadModuleMgr;
import com.loading.modules.interfaces.download.DownloadRequest;

import java.util.List;

public class SocketClientActivity extends FragmentActivity {
    private static final String TAG = "SocketClientActivity";
    // 定义界面上的两个文本框
    EditText input;
    TextView show;
    EditText mHostIp;
    // 定义界面上的一个按钮
    Handler handler;
    // 定义与服务器通信的子线程
    SocketClientThread clientThread;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_socket_client);
        input = (EditText) findViewById(R.id.input);
        mHostIp = (EditText) findViewById(R.id.host_ip);
        show = (TextView) findViewById(R.id.show);
        handler = new Handler() //①
        {
            @Override
            public void handleMessage(Message msg) {
                // 如果消息来自于子线程
                if (msg.what == 0x123) {
                    // 将读取的内容追加显示在文本框中
                    show.append("\n" + msg.obj.toString());
                }
            }
        };
    }

    public void onBtnClicked(View view) {
        int viewId = view.getId();
        if (viewId == R.id.btn_connect) {
            onConnectBtnClicked();
        } else if (viewId == R.id.bnt_send) {
            onSendBtnClicked();
        } else if (viewId == R.id.btn_query) {
            onQueryBtnClicked();
        } else if (viewId == R.id.btn_download) {
            onDownloadBtnClicked();
        }
    }

    private void onSendBtnClicked() {
        Loger.d(TAG, "-->onSendBtnClicked()");
        try {
            // 当用户按下发送按钮后，将用户输入的数据封装成Message，
            // 然后发送给子线程的Handler
            Message msg = new Message();
            msg.what = SocketClientThread.MSG_SEND_INFO_TO_SERVER;
            msg.obj = input.getText().toString();
            clientThread.revHandler.sendMessage(msg);
            // 清空input文本框
            input.setText("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onConnectBtnClicked() {
        Loger.d(TAG, "-->onConnectBtnClicked()");
        String hostIp = mHostIp.getText().toString();
        if (TextUtils.isEmpty(hostIp)) {
//            hostIp = "192.168.199.121";
            hostIp = "10.72.159.117";
        }
        clientThread = new SocketClientThread(hostIp, handler);
        // 客户端启动ClientThread线程创建网络连接、读取来自服务器的数据
        new Thread(clientThread).start(); //①
    }

    private void onQueryBtnClicked() {
        String targetUrl = input.getText().toString();
        // 清空input文本框
        input.setText("");

        if (TextUtils.isEmpty(targetUrl)) {
            targetUrl = "http://puep.qpic.cn/coral/Q3auHgzwzM4fgQ41VTF2rC5GOhteBjmYVBcmEo9tcIY1ic7XYxAyk6w/0";
        }
        DownloadModuleMgr.asyncQueryFileInfo(targetUrl, null, (url, success, headerFieldsMap) -> {
            Loger.d(TAG, "dwz-->onQueryFileInfoDone(), url=" + url + ", success=" + success + ", paramMap=" + headerFieldsMap);

            StringBuilder builder = new StringBuilder();
            builder.append("Query result from module: ").append(success).append("\n");
            if (headerFieldsMap != null) {
                for (String key : headerFieldsMap.keySet()) {
                    builder.append(key).append(" = ");
                    List<String> valueList = headerFieldsMap.get(key);
                    if (valueList != null && valueList.size() > 0) {
                        builder.append(valueList.get(0));
                    }
                    builder.append("\n");
                }
            }
            show.setText(builder.toString());
        });
    }

    private void onDownloadBtnClicked() {
        String targetUrl = input.getText().toString();
        // 清空input文本框
        input.setText("");

        if (TextUtils.isEmpty(targetUrl)) {
            targetUrl = "http://puep.qpic.cn/coral/Q3auHgzwzM4fgQ41VTF2rC5GOhteBjmYVBcmEo9tcIY1ic7XYxAyk6w/0";
        }

        DownloadModuleMgr.startDownload(targetUrl, null, new DownloadListener() {
            @Override
            public void onDownloadProgress(String taskId, String downloadUrl, String tempFilePath, long completeSize, long totalSize, int nProgress, DownloadRequest downloadRequest) {
                show.setText("-->onDownloadProgress(), rate=" + nProgress + "(" + completeSize + "/" + totalSize + ")");
            }

            @Override
            public void onDownloadPaused(String taskId, String downloadUrl, String tempFilePath, long completeSize, long totalSize, int nProgress, DownloadRequest downloadRequest) {
                show.setText("-->onDownloadPaused(), rate=" + nProgress + "(" + completeSize + "/" + totalSize + ")");
            }

            @Override
            public void onDownloadError(String taskId, String downloadUrl, String tempFilePath, long completeSize, long totalSize, int nProgress, DownloadRequest downloadRequest) {
                show.setText("-->onDownloadError(), rate=" + nProgress + "(" + completeSize + "/" + totalSize + ")");
            }

            @Override
            public void onDownloadComplete(String taskId, String downloadUrl, String finalFilePath, long completeSize, long totalSize, DownloadRequest downloadRequest) {
                show.setText("-->onDownloadComplete(), completeSize=" + completeSize + ", final path=" + finalFilePath);
            }
        });
    }
}
