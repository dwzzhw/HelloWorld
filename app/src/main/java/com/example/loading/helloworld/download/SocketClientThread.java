package com.example.loading.helloworld.download;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.example.loading.helloworld.utils.Loger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class SocketClientThread implements Runnable {
    private static final String TAG = "SocketClientThread";
    public static final int MSG_SEND_INFO_TO_SERVER = 1;
    private Socket s;
    // 定义向UI线程发送消息的Handler对象
    private Handler handler;
    // 定义接收UI线程的消息的Handler对象
    public Handler revHandler;
    // 该线程所处理的Socket所对应的输入流
    BufferedReader br = null;
    InputStream in;
    OutputStream os = null;
    private String mHostIp;
    private boolean mSleepNow = false;
    private boolean mNeedEcho = true;

    public SocketClientThread(String hostIp, Handler handler) {
        this.handler = handler;
        mHostIp = hostIp;
    }

    public void run() {
        try {
            Loger.d(TAG, "-->try connect to host, id=" + mHostIp);
            s = new Socket(mHostIp, 9999);

            s.setReceiveBufferSize(200 * 1024);

            s.setSoTimeout(0);
            in = s.getInputStream();
            br = new BufferedReader(new InputStreamReader(
                    s.getInputStream()));
            os = s.getOutputStream();
            // 启动一条子线程来读取服务器响应的数据
            new Thread() {
                @Override
                public void run() {
                    String content = null;
                    byte[] buffer = new byte[4096];
                    int length = 0;
                    int totalReadLength = 0;
                    // 不断读取Socket输入流中的内容。
                    try {
                        while ((length = in.read(buffer)) != -1) {
                            // 每当读到来自服务器的数据之后，发送消息通知程序界面显示该数据
                            totalReadLength += length;
                            if (totalReadLength > 100 * 1024) {
                                Thread.sleep(100);
                                totalReadLength = 0;
                            }
                            if (mNeedEcho || mSleepNow) {
                                content = new String(buffer);
                                Message msg = new Message();
                                msg.what = 0x123;
                                msg.obj = content;
                                handler.sendMessage(msg);
                                Loger.d(TAG, "-->receive msg from server, socket recv buffer size=" + s.getReceiveBufferSize() + ", content=" + content);
                            }
                            if (mSleepNow) {
                                Loger.d(TAG, "-->sleep 2s to read");
                                Thread.sleep(500);
                                mSleepNow = false;
                            }
                        }
//                        while ((content = br.readLine()) != null) {
//                            // 每当读到来自服务器的数据之后，发送消息通知程序界面显示该数据
//                            if (mNeedEcho || mSleepNow) {
//                                Message msg = new Message();
//                                msg.what = 0x123;
//                                msg.obj = content;
//                                handler.sendMessage(msg);
//                            }
//                            Loger.d(TAG, "-->receive msg from server, content=" + content+", socket recv buffer size="+s.getReceiveBufferSize());
//                            if (mSleepNow) {
//                                Loger.d(TAG, "-->sleep 10s to read");
//                                Thread.sleep(10000);
//                                mSleepNow = false;
//                            }
//                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
            // 为当前线程初始化Looper
            Looper.prepare();
            // 创建revHandler对象
            revHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    // 接收到UI线程中用户输入的数据
                    switch (msg.what) {
                        case MSG_SEND_INFO_TO_SERVER:
                            // 将用户在文本框内输入的内容写入网络
                            try {
                                Loger.d(TAG, "-->send msg to server, content=" + msg.obj);
                                String msgContent = msg.obj.toString();
                                if ("mp4".equals(msgContent)) {
                                    mSleepNow = true;
                                    mNeedEcho = false;
                                } else {
                                    mNeedEcho = true;
                                }
                                os.write((msgContent + "\r\n")
                                        .getBytes("utf-8"));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                    }
                }
            };
            // 启动Looper
            Looper.loop();
        } catch (SocketTimeoutException e1) {
            Loger.w(TAG, "网络连接超时！！");
            Loger.e(TAG, "SocketTimeoutException, e=" + e1, e1);
        } catch (Exception e) {
            Loger.e(TAG, "Exception happen, e=" + e, e);
            e.printStackTrace();
        }
    }
}
