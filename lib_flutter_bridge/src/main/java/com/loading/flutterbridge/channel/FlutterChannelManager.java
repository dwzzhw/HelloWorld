package com.loading.flutterbridge.channel;

import android.support.annotation.Nullable;

import com.loading.common.component.BaseActivity;
import com.loading.common.utils.Loger;
import com.loading.common.utils.UiThreadUtil;
import com.loading.common.widget.TipsToast;
import com.loading.modules.interfaces.commentpanel.CommentPanelModuleMgr;
import com.loading.modules.interfaces.commentpanel.data.CommentConstants;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.view.FlutterView;

public class FlutterChannelManager {
    public static final String TAG = "FlutterChannelManager_dwz";

    public static final String CHANNEL_NAME = "sports_native_flutter_channel";
    public static final String METHOD_N2F_SAY_HELLO = "native_to_flutter_say_hello";
    public static final String METHOD_F2N_SAY_HELLO = "flutter_to_native_say_hello";

    public static final String METHOD_F2N_SHOW_COMMENT_PANEL = "flutter_to_native_show_comment_panel";
    public static final String METHOD_N2F_SEND_COMMENT = "native_to_flutter_send_comment";

    private BaseActivity mContainerActivity;
    private FlutterView mFlutterView;
    private MethodChannel methodChannel;
    private MethodChannel.MethodCallHandler methodCallHandler;
    private MethodChannel.Result methodChannelResult;

    public FlutterChannelManager(BaseActivity containerActivity, FlutterView flutterView) {
        this.mFlutterView = flutterView;
        this.mContainerActivity = containerActivity;
        initMethodChannel();
    }

    private void initMethodChannel() {
        initFlutterMethodCallHandler();

        methodChannel = new MethodChannel(mFlutterView, CHANNEL_NAME);
        methodChannel.setMethodCallHandler(methodCallHandler);
    }

    private void initFlutterMethodCallHandler() {
        if (methodCallHandler == null) {
            methodCallHandler = (call, result) -> {
                Loger.d(TAG, "-->receive method from flutter, method=" + call.method);
                CallNativeMethodResp respObj = handleMethodCallFromFlutter(call);

                if (respObj != null) {
                    if (respObj.success) {
                        result.success(respObj.combineMethodAndResp());
                    } else {
                        result.error(respObj.combineMethodAndResp(), "arg02", "obj03");
                    }
                } else {
                    result.notImplemented();
                }
            };
        }
    }

    private CallNativeMethodResp handleMethodCallFromFlutter(MethodCall methodCall) {
        CallNativeMethodResp respObj = null;
        if (methodCall != null) {
            Loger.d(TAG, "-->handleMethodCallFromFlutter(), methodName=" + methodCall.method + ", arguments=" + methodCall.arguments);
            boolean result = false;
            String respMsg = null;
            boolean methodMatch = false;
            if (METHOD_F2N_SHOW_COMMENT_PANEL.equals(methodCall.method)) {
                result = true;
                methodMatch = true;
                respMsg = "Android will show comment panel";

                CommentPanelModuleMgr.showCommentPanel(mContainerActivity,
                        CommentConstants.MODE_EMOJO | CommentConstants.MODE_SINGLE_PIC, null);
            } else if (METHOD_F2N_SAY_HELLO.equals(methodCall.method)) {
                result = true;
                methodMatch = true;
                respMsg = "Nice to meed you, flutter!";

            } else {
                result = true;
                methodMatch = true;
                respMsg = "No match method handler, just say hello.";

                //dwz test
                UiThreadUtil.postDelay(() -> {
                    callFlutterMethod(METHOD_N2F_SAY_HELLO, "A2FParams");
                }, 3000);
            }
            if (methodMatch) {
                respObj = new CallNativeMethodResp(methodCall.method, respMsg, result);
            }
        }
        Loger.d(TAG, "-->handleMethodCallFromFlutter(): resp=" + respObj);
        return respObj;
    }

    static class CallNativeMethodResp {
        String methodName;
        String respStr;
        boolean success;

        public CallNativeMethodResp(String methodName, String respStr, boolean success) {
            this.methodName = methodName;
            this.respStr = respStr;
            this.success = success;
        }

        String combineMethodAndResp() {
            return methodName + "/" + respStr;
        }

        @Override
        public String toString() {
            return "CallNativeMethodResp{" +
                    "methodName='" + methodName + '\'' +
                    ", respStr='" + respStr + '\'' +
                    ", success=" + success +
                    '}';
        }
    }

    public void callFlutterMethod(String methodName, Object argsObj) {
        Loger.d(TAG, "-->callFlutterMethod(): methodName=" + methodName + ", argsObj=" + argsObj);
        initMethodChannelResultHandler();
        methodChannel.invokeMethod(methodName, argsObj, methodChannelResult);
    }

    private void initMethodChannelResultHandler() {
        if (methodChannelResult == null) {
            methodChannelResult = new MethodChannel.Result() {
                @Override
                public void success(@Nullable Object result) {
                    Loger.d(TAG, "-->method channel result() success, result=" + result);
                    TipsToast.getInstance().showTipsText("Receive resp from flutter: " + result);
                }

                @Override
                public void error(String errorCode, @Nullable String errorMessage, @Nullable Object errorDetails) {

                    Loger.d(TAG, "-->method channel result() error, result=" + errorCode + ", errorMessage=" + errorMessage + ", errorDetails=" + errorDetails);
                    TipsToast.getInstance().showTipsText("method channel result() error, result=" + errorCode);
                }

                @Override
                public void notImplemented() {
                    Loger.d(TAG, "-->method channel result() notImplemented.");
                    TipsToast.getInstance().showTipsText("method channel result() notImplemented");
                }
            };
        }
    }

}
