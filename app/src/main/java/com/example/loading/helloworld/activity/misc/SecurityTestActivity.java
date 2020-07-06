package com.example.loading.helloworld.activity.misc;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.loading.helloworld.R;
import com.example.loading.libjava.SecurityHelper;
import com.example.loading.libjava.utils.AESUtil;
import com.loading.common.component.BaseActivity;
import com.loading.common.utils.Loger;

public class SecurityTestActivity extends BaseActivity {
    private static final String TAG = "SecurityTestActivity";
    private EditText mInputArea;
    private TextView mOutputArea;
    private EditText mKeyArea;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_test);

        mInputArea = findViewById(R.id.input_content);
        mOutputArea = findViewById(R.id.output_content);
        mKeyArea = findViewById(R.id.key_content);
    }

    public void onBtnClicked(View view) {
        int viewId = view.getId();
        if (viewId == R.id.aes_encrypt) {
            startAESEncrypt();
        } else if (viewId == R.id.aes_decrypt) {
            startAESDecrypt();
        } else if (viewId == R.id.base64_encrypt) {
            startBase64Encode();
        } else if (viewId == R.id.base64_decrypt) {
            startBase64Decode();
        } else if (viewId == R.id.clear) {
            clear();
        }
    }

    private void startAESEncrypt() {

        String pwd = mKeyArea.getText().toString();
        if (TextUtils.isEmpty(pwd)) {
            pwd = AESUtil.getRandomAESKey();
            mKeyArea.setText(pwd);
        }

        String inputContent = mInputArea.getText().toString();
        Loger.d(TAG, "-->startAESEncrypt(): pwd=" + pwd, ", inputContent=", inputContent);
        String outContent = AESUtil.encrypt(inputContent, pwd);
        mOutputArea.setText(outContent);
    }

    private void startAESDecrypt() {
        String inputContent = mInputArea.getText().toString();
        String pwd = mKeyArea.getText().toString();
        int length = inputContent.length();
        if (TextUtils.isEmpty(pwd)) {
            pwd = SecurityHelper.guessKey(inputContent.substring(length - 32));
            inputContent = inputContent.substring(0, length - 32);
        }
        Loger.d(TAG, "-->startAESDecrypt(): pwd=", pwd, ", inputContent=" + inputContent);

        String outContent = AESUtil.decrypt(inputContent, pwd);
        mOutputArea.setText(outContent);
    }

    private void startBase64Encode() {
        String inputContent = mInputArea.getText().toString();
        String outPut = Base64.encodeToString(inputContent.getBytes(), Base64.NO_WRAP);
        mOutputArea.setText(outPut);
    }

    private void startBase64Decode() {
        String inputContent = mInputArea.getText().toString();
        try {
            String outPut = new String(Base64.decode(inputContent.getBytes(), Base64.NO_WRAP));
            mOutputArea.setText(outPut);
        } catch (Exception e) {
            mOutputArea.setText(e.toString());
        }

    }

    private void clear() {
        mInputArea.setText(null);
        mKeyArea.setText(null);
    }
}
