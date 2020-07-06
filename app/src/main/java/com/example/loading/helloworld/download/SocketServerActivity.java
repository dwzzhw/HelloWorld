package com.example.loading.helloworld.download;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.fragment.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.example.loading.helloworld.R;
import com.loading.common.utils.Loger;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SocketServerActivity extends FragmentActivity {
    private static final String TAG = "SocketServerActivity";
    private static final int PORT = 9999;
    private static final int MSG_RECEIVE_CLIENT_MSG = 1;
    private List<Socket> mList = new ArrayList<Socket>();
    private volatile ServerSocket server = null;
    private ExecutorService mExecutorService = null; //线程池
    private String mHostIp;//本机IP
    private TextView mText1;
    private TextView mText2;
    private Button mBut1 = null;
    private Handler myHandler = null;
    private volatile boolean flag = true;//线程标志位
                           //3670016
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_socket_server);
        mHostIp = getLocalIpAddress();  //获取本机IP
        mText1 = (TextView) findViewById(R.id.textView1);
        mText1.setText(mHostIp);
        mText1.setEnabled(false);

        mText2 = (TextView) findViewById(R.id.textView2);

        mBut1 = (Button) findViewById(R.id.but1);
        mBut1.setOnClickListener(new Button1ClickListener());
        //取得非UI线程传来的msg，以改变界面
        myHandler = new Handler() {
            @SuppressLint("HandlerLeak")
            public void handleMessage(Message msg) {
                if (msg.what == MSG_RECEIVE_CLIENT_MSG) {
                    mText2.append("\n" + msg.obj.toString());
                }
            }
        };

    }

    //对button1的监听事件
    private final class Button1ClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            //如果是“启动”，证明服务器是关闭状态，可以开启服务器
            if (mBut1.getText().toString().equals("启动")) {
                Loger.d(TAG, "Start server, flag:" + flag);
                ServerThread serverThread = new ServerThread();
                flag = true;
                serverThread.start();
                mBut1.setText("关闭");
            } else {
                try {
                    flag = false;
                    server.close();
                    for (int p = 0; p < mList.size(); p++) {
                        Socket s = mList.get(p);
                        s.close();
                    }
                    mExecutorService.shutdownNow();
                    mBut1.setText("启动");
                    Loger.d(TAG, "服务器已关闭");
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

        }
    }

    //Server端的主线程
    class ServerThread extends Thread {

        public void stopServer() {
            try {
                if (server != null) {
                    server.close();
                    Loger.i(TAG, "close task success");
                }
            } catch (IOException e) {
                Loger.e(TAG, "close task failed", e);
            }
        }

        public void run() {

            try {
                server = new ServerSocket(PORT);
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                Loger.w(TAG, "S2: Error");
                e1.printStackTrace();
            }
            mExecutorService = Executors.newCachedThreadPool();  //创建一个线程池
            Loger.i(TAG, "服务器已启动...");
            Socket client = null;
            while (flag) {
                try {
                    client = server.accept();
//                    client.setSendBufferSize(20000);
                    //把客户端放入客户端集合中
                    mList.add(client);
                    mExecutorService.execute(new Service(client)); //启动一个新的线程来处理连接
                } catch (IOException e) {
                    Loger.e(TAG, "S1: Error", e);
                    e.printStackTrace();
                }
            }


        }
    }

    //获取IPv6的IP地址
	/*public String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("WifiPreference IpAddress", ex.toString());
        }
        return null;
    }  */
    //获取本地IP
    public static String getLocalIpAddress() {
        try {
            Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
            if (en != null) {
                for (; en.hasMoreElements(); ) {
                    NetworkInterface intf = en.nextElement();
                    for (Enumeration<InetAddress> enumIpAddr = intf
                            .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress()) {
                            String hostId = inetAddress.getHostAddress();
                            if (!TextUtils.isEmpty(hostId) && !hostId.contains(":")) {
                                return hostId;
                            }
                        }
                    }
                }
            } else {
                Loger.w(TAG, "fail to get network interfaces");
            }
        } catch (SocketException ex) {
            Loger.e(TAG, "-->getLocalIpAddress() crash, info=" + ex.toString(), ex);
        }
        return null;
    }

    //处理与client对话的线程
    class Service implements Runnable {
        private volatile boolean kk = true;
        private Socket socket;
        private BufferedReader in = null;
        private String msg = "";

        public Service(Socket socket) {
            this.socket = socket;
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                msg = "OK";
                this.sendmsg(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        public void run() {

            while (kk) {
                try {
                    if ((msg = in.readLine()) != null) {
                        //当客户端发送的信息为：exit时，关闭连接
                        if (msg.equals("exit")) {
                            mList.remove(socket);
                            //in.close();
                            //socket.close();
                            break;
                            //接收客户端发过来的信息msg，然后发送给客户端。
                        } else if (msg.equals("mp4")) {
                            sendLargeFile();
                        } else {
                            Message msgLocal = new Message();
                            msgLocal.what = MSG_RECEIVE_CLIENT_MSG;
                            msgLocal.obj = msg + " （客户端发送）";
                            Loger.d(TAG, "receive local msg, msg=" + msgLocal.obj);
                            myHandler.sendMessage(msgLocal);
                            msg = socket.getInetAddress() + ":" + msg + "（服务器发送）";
                            this.sendmsg(msg);
                        }

                    }
                } catch (IOException e) {
                    Loger.e(TAG, "Service crash, close. e=" + e, e);
                    kk = false;
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }

        //向客户端发送信息
        public void sendmsg(String msg) {
            Loger.d(TAG, "-->sendmsg(), msg=" + msg);
            PrintWriter pout = null;
            try {
                pout = new PrintWriter(new BufferedWriter(
                        new OutputStreamWriter(socket.getOutputStream())), true);
                pout.println(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void sendLargeFile() {
            Message msgLocal = new Message();
            msgLocal.what = MSG_RECEIVE_CLIENT_MSG;
            msgLocal.obj = msg + " （客户端发送）请求大文件";
            Loger.d(TAG, "receive local msg, msg=" + msgLocal.obj);
            myHandler.sendMessage(msgLocal);

            OutputStream outputStream = null;
            InputStream inputStream = null;
            try {
                outputStream = new BufferedOutputStream(socket.getOutputStream());
//                inputStream = getAssets().open("champion.mp4");
//                inputStream = getAssets().open("large_file_40M.pdf");
                inputStream = getAssets().open("large_file_10M.rar");
                byte[] buffer = new byte[4096];
                int readLen = -1;
                int totalWriteLength = 0;
                while ((readLen = inputStream.read(buffer)) != -1) {
                    totalWriteLength += readLen;
                    outputStream.write(buffer, 0, readLen);
                    Loger.d(TAG, "-->sendLargeFile to client, totalWriteLength=" + totalWriteLength);
                }

            } catch (IOException e) {
                Loger.e(TAG, "sendLargeFile IOException, e=" + e, e);
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
