package com.loading.modules.interfaces.face;

import android.text.SpannableStringBuilder;
import android.widget.TextView;

import com.loading.modules.IModuleInterface;
import com.loading.modules.annotation.Repeater;
import com.loading.modules.interfaces.face.data.RemoteFacePackageInfo;

import java.util.List;

@Repeater
public interface IFaceService extends IModuleInterface {
    SpannableStringBuilder convertToSpannableStr(String textContent, float textSize);

    SpannableStringBuilder convertToSpannableStr(String textContent, TextView containerTextView);

    //更新远端表情包配置
    void updateRemoteFacePackageInfo(List<RemoteFacePackageInfo> remoteFacePackageInfoList, String version);
}
