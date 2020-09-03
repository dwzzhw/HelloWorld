package com.example.loading.helloworld.activity.ui;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.annotation.Nullable;

import com.example.loading.helloworld.R;
import com.example.loading.helloworld.activity.misc.SecurityTestActivity;
import com.loading.common.component.BaseActivity;
import com.loading.common.utils.Loger;

public class BrowserTestActivity extends BaseActivity {
    private static final String TAG = "BrowserTestActivity";
    private CheckBox mMibrowserDebugSwitch;
    private EditText mMibrowserUrl;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser_test);

        mMibrowserDebugSwitch = findViewById(R.id.mibrowser_debug);
        mMibrowserUrl = findViewById(R.id.mibrowser_url);
    }

    public void onBtnClicked(View view) {
        int viewId = view.getId();
        if (viewId == R.id.encrypt_page) {
            startEncryptPage();
        } else if (view.getId() == R.id.btn_mibrowser) {
            startMiBrowser();
        } else if (view.getId() == R.id.btn_check_intent) {
            checkDeepLink();
        }
    }

    private void startEncryptPage() {
        Intent intent = new Intent(this, SecurityTestActivity.class);
        startActivity(intent);
    }

    private void startMiBrowser() {
        Loger.d(TAG, "-->startMiBrowser: ");
        boolean useDebugPackage = mMibrowserDebugSwitch.isChecked();
        String url = mMibrowserUrl.getText().toString();

//        String url1 = "mibrowser://home?web_url=https%3A%2F%2Fm.ifeng.com%2FmiArticle%3Fch%3Dref_xmllq_hz1%26version%3D2%26aid%3Ducms_7uCj51705PY%26mibusinessId%3Dnewhome%26mibusinessId%3Dxiangkan%26env%3Dproduction%26docid%3Dfenghuang_ucms_7uCj51705PY%26cp%3Dcn-fenghuang%26itemtype%3Dnews%26miref%3Dnewsout_quicksearchbox_news%26_miui_bottom_bar%3Dcomment%26_miui_fullscreen%3D1%26s%3Dmb";
//        String url2 = "mibrowser://infoflow?first_launch_web=true&web_url=https%3A%2F%2Fm.ifeng.com%2FmiArticle%3Fch%3Dref_xmllq_hz1%26version%3D2%26aid%3Ducms_7uCj51705PY%26mibusinessId%3Dnewhome%26mibusinessId%3Dxiangkan%26env%3Dproduction%26docid%3Dfenghuang_ucms_7uCj51705PY%26cp%3Dcn-fenghuang%26itemtype%3Dnews%26miref%3Dnewsout_quicksearchbox_news%26_miui_bottom_bar%3Dcomment%26_miui_fullscreen%3D1%26s%3Dmb%26infotype%3D1";
//        String url3 = "https://m.ifeng.com/miArticle?ch=ref_xmllq_hz1&version=2&aid=ucms_7uCj51705PY&mibusinessId=newhome&mibusinessId=xiangkan&env=production&docid=fenghuang_ucms_7uCj51705PY&cp=cn-fenghuang&itemtype=news&miref=newsout_quicksearchbox_news&_miui_bottom_bar=comment&_miui_fullscreen=1&s=mb";
//        String url4 = "https://m.ifeng.com/miArticle?ch=ref_xmllq_hz1&version=2&aid=ucms_7uCj51705PY"
//                + "&docid=fenghuang_ucms_7uCj51705PY&cp=cn-fenghuang&itemtype=news"
////                +"&mibusinessId=newhome&mibusinessId=xiangkan&env=production"
////                +"&miref=newsout_quicksearchbox_news&_miui_bottom_bar=comment&_miui_fullscreen=1&s=mb"

        String url4 = "mibrowser://infoflow?channel=game&miref=12345";  //游戏频道
        String url5 = "mibrowser://infoflow?channel=shortVideo";  //小视频频道
        String url51 = "mibrowser://infoflow?channel=rec&miref=xiaoai";  //视频频道
        String url6 = "mibrowser://video";   //底部视频tab
        String url7 = "mibrowser://video/videoHome_boba";   //底部视频tab，  播吧页卡
        String url8 = "mibrowser://video/videoHome_recommend";//底部视频tab， 推荐页卡
        String url9 = "mibrowser://video/videoHome_small";   //底部视频tab, 小视频页卡

        String url10 = "mibrowser://infoflow?mibr_page_type=111&channel=game";   //外部拉起游戏频道
        String url11 = "mibrowser://infoflow?mibr_page_type=202";   //外部拉起小说页
        String url12 = "mibrowser://infoflow?mibr_page_type=110&url=http%3a%2f%2fwww.baidu.com";   //外部拉起制定网址页面
        String url13 = "http://www.baidu.com";
        String url14 = "mibrowser://infoflow?channel=science&miref=xiaoai";
        String url15 = "mibrowser://infoflow?web_url=https%3a%2f%2fwww.yidianzixun.com%2farticle%2f0OuIthnD%3fs%3dmb%26appid%3dmibrowser%26version%3d2%26mibusinessId%3dmiuibrowser%26env%3dtest&first_launch_web=true&channel=sport";
        String url16 = "http://www.baidu.com/?&mibrowser_back=1";
        String url17 = "https://m.ifeng.com/miArticle?ref=browser_news&mibusinessId=miuibrowser&s=mb&itemtype=news&ch=ref_xmllq_hz1&dod=fenghuang_ucms_7vIHHpWWyqO&env=production&version=2&aid=ucms_7vIHHpWWyqO&cp=cn-fenghuang-browser&category=%E7%A4%BE%E4%BC%9A&cateCode=%E6%8E%A8%E8%8D%90&authorId=2f6c561aea56001fb359fa5565a9f1b2&mibrowser_back=1";
        String url18 = "mibrowser://infoflow?web_url=https%3A%2F%2Fyiyouliao.com%2Fapi-server%2Frss%2Fxiaomi%2Fitem%2Fuser%2FIU01G7L6BCX4XEC.html%3Fversion%3D2%26mibusinessId%3Dxiangkan%26env%3Dtest%26miref%3Dnewsin_push_model%26infotype%3D1%26_miui_fullscreen%3D1%26utm_source%3Dlocalpush%26docid%3Dnetease_IU01G7L6BCX4XEC%26cp%3Dcn-netease-youliao%26itemtype%3Dnews&miui_back_info=0&_miui_fullscreen=1&utm_source=localpush&first_launch_web=true";
        String url19 = "mibrowser://infoflow?web_url=https%3A%2F%2Fapp.myzaker.com%2Fnews%2Farticle.php%3Ff%3Dxiaomi%26pk%3D5e9eabec1bc8e02849000182%26version%3D2%26mibusinessId%3Dxiangkan%26env%3Dproduction%26miref%3Dnewsin_push_model%26infotype%3D1%26_miui_fullscreen%3D1%26utm_source%3Dxmpush%26mifloat%3Dnewscat%26docid%3Dzaker_5e9eabec1bc8e02849000182%26cp%3Dcn-zaker-browser%26itemtype%3Dnews&first_launch_web=true&channel=%E6%8E%A8%E8%8D%90#miui_back_info=0&_miui_fullscreen=1&utm_source=xmpush&utm_campaign=2020-04-22 18:55#4YYb1qIN#%E6%9C%80%E6%96%B0%E6%94%BE%E5%81%87%E9%80%9A%E7%9F%A5%EF%BC%81%E4%B8%8A%E7%8F%AD%E6%97%B6%E9%97%B4%E6%9C%89%E5%8F%98%E5%8C%96%EF%BC%81";
//        String url20 = "content://com.ss.android.lark.kami.common.fileprovider/external_files/Lark/download/2020.06一线运营报表 - 手机 .xlsm";
//        String url20 = "content://com.ss.android.lark.kami.common.fileprovider/external_files/Lark/download/2020.06.xlsm";

        Intent intent = new Intent(Intent.ACTION_VIEW);
        String targetUrl = url;
        if (TextUtils.isEmpty(targetUrl)) {
            targetUrl = url19;
        }
        intent.setData(Uri.parse(targetUrl));
//        intent.setDataAndType(Uri.parse(targetUrl), "text/plain");
//        intent.setDataAndType(Uri.parse(targetUrl), "*/*");
        if (useDebugPackage) {
            intent.setPackage("com.android.browser.debug");
        }

        PackageManager packageManager = getPackageManager();
        ResolveInfo resolveInfo = packageManager.resolveActivity(intent, 0);
        Loger.d(TAG, "-->startMiBrowser(): resolveInfo=" + resolveInfo);

        startActivity(intent);


//        String url31 = "https://app.myzaker.com/news/article.php?f=xiaomi&pk=5e708d11b15ec06db90baa52&version=2&mibusinessId=miuibrowser&env=production&miref=newsin_push_model&infotype=1&_miui_fullscreen=1&utm_source=xmpush&channel=sport&cp=123&docid=123&itemtype=news";
//        String url31 = "https://app.myzaker.com/news/article.php?f=xiaomi&pk=5e708d11b15ec06db90baa52&cp=123&docid=123&itemtype=news";
//        String url32 = "mibrowser://infoflow?web_url=https%3a%2f%2fapp.myzaker.com%2fnews%2farticle.php%3ff%3dxiaomi%26pk%3d5e708d11b15ec06db90baa52%26version%3d2%26mibusinessId%3dmiuibrowser%26env%3dproduction%26miref%3dnewsin_push_model%26infotype%3d1%26_miui_fullscreen%3d1%26utm_source%3dxmpush%26cp%3d123%26docid%3d123%26itemtype%3dnews&channel=sport&first_launch_web=true&miref=newsin_push_model&infotype=1&_miui_fullscreen=1&utm_source=xmpush";
//        String url31 = "https://mb.yidianzixun.com/article/0OuXPu1J?s=mb&appid=mibrowser&version=2&mibusinessId=miuibrowser&env=test&miref=newsin_push_model&infotype=1&_miui_fullscreen=1&utm_source=xmpush";
//        String url33 = "mibrowser://infoflow?web_url=https%3a%2f%2fpartners.sina.cn%2fhtml%2fxiaomi%2fbrowser%2farticle%3fwm%3d3993%26docUrl%3dhttp%253A%252F%252Fsports.sina.cn%252Fnba%252F2020-03-20%252Fdetail-iimxxsth0436581.d.html%26version%3d2%26mibusinessId%3dxiangkan%26env%3dtest&channel=sport";
//        Intent clickIntent = new Intent();
//        clickIntent.setPackage("com.android.browser.debug");
////        clickIntent.setPackage("com.android.browser");
//        clickIntent.putExtra(Browser.EXTRA_APPLICATION_ID, "push");
//        clickIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        clickIntent.setAction(Intent.ACTION_VIEW);
//        clickIntent.setData(Uri.parse(url33));
//
//        startActivity(clickIntent);
    }


    private void checkDeepLink() {
        String deepLinkUrl = mMibrowserUrl.getText().toString();
//        String novelUrl = "https://reader.browser.duokan.com/hs/market/shelf?_miui_fullscreen=1&_miui_orientation=portrait&source=newhome";


//        deepLinkUrl = "intent:#Intent;action=miui.intent.action.GARBAGE_CLEANUP;end";
//        deepLinkUrl = "intent:#Intent;package=com.android.browser;end";

        boolean exist = false;
        final Intent intent;
//        try {
//            intent = Intent.parseUri(deepLinkUrl, Intent.URI_INTENT_SCHEME);

//        String novelUrl = "https://reader.browser.duokan.com/hs/market/shelf?_miui_fullscreen=1&_miui_orientation=portrait&source=newhome";
        String novelUrl = "https://reader.browser.duokan.com/v2/#page=user&source=newhome&miback=true";
//        String url31 = "https://app.myzaker.com/news/article.php?f=xiaomi&pk=5e708d11b15ec06db90baa52&cp=123&docid=123&itemtype=news";
        if (TextUtils.isEmpty(deepLinkUrl)) {
            deepLinkUrl = "https://app.myzaker.com/news/article.php?f=xiaomi&pk=5e708d11b15ec06db90baa52&cp=123&docid=123&itemtype=news";
            deepLinkUrl = "https://reader.browser.duokan.com/v2/#page=user&source=newhome&miback=true";
        }
//        String novelUrl = "https://www.baidu.com";
        intent = new Intent(Intent.ACTION_VIEW);
        intent.setPackage("com.android.browser");
//            intent.setAction("com.duokan.shop.mibrowser.actions.SHOW_STORE");
        intent.setData(Uri.parse(deepLinkUrl));
        startActivity(intent);

//        } catch (URISyntaxException ex) {
//            Loger.w(TAG, "Bad URI " + deepLinkUrl + ": " + ex.getMessage());
//            return;
//        }

//        intent.addCategory(Intent.CATEGORY_BROWSABLE);
//        intent.addCategory(Intent.CATEGORY_DEFAULT);
////        intent.setComponent(null);
//        Intent selector = intent.getSelector();
//        if (selector != null) {
//            selector.addCategory(Intent.CATEGORY_BROWSABLE);
//            selector.setComponent(null);
//        }
//
//        PackageManager packageManager = getPackageManager();
//        ResolveInfo info = packageManager.resolveActivity(intent, 0);
//        Loger.d(TAG, "-->checkDeepLink(), url=" + deepLinkUrl + ", info=" + info);
//        TipsToast.getInstance().showTipsText("result=" + (info != null));
//
//        if (info != null) {
//            startActivity(intent);
//        }
    }

    private void startDeepLink() {
        String url = "";
        Intent intent = new Intent();
        intent.setData(Uri.parse(url));


        startActivity(intent);
    }

}
