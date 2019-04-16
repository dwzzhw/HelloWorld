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
}
