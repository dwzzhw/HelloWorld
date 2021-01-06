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
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.loading.helloworld.R;
import com.example.loading.helloworld.activity.misc.SecurityTestActivity;
import com.loading.common.component.BaseActivity;
import com.loading.common.utils.Loger;
import com.loading.common.utils.SecurityUtil;
import com.loading.common.widget.TipsToast;
import com.tencent.mtt.QQBrowserTestActivity;

import java.net.URISyntaxException;

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
        } else if (viewId == R.id.btn_mibrowser) {
            startMiBrowser();
        } else if (viewId == R.id.btn_check_intent) {
            checkDeepLink();
        } else if (viewId == R.id.btn_qqbrowser) {
            startQQBrowser();
        } else if (viewId == R.id.btn_check_sign_md5) {
            checkPackageSignature();
        } else if (viewId == R.id.btn_search) {
            triggerSearch();
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
//
//        String url20 = "content://com.ss.android.lark.kami.common.fileprovider/external_files/Lark/download/2020.06.xlsm";
        String url22 = "mibrowser://infoflow?web_url=mailto:nobody@google.com";
//        String url21 = "mibrowser://infoflow?web_url\u003dhttps%3A%2F%2Fhot.browser.miui.com%2Frec%2Fcontent%2Fbjnews_160449023915284%3FcontentId%3Dbjnews_160449023915284%26version%3D2%26mibusinessId%3Dmiuibrowser%26env%3Dproduction%26miref%3Dnewsin_push_model%26infotype%3D1%26_miui_fullscreen%3D1%26utm_source%3Dxmpush%26mifloat%3Dnewscat%26docid%3Dbjnews_160449023915284%26cp%3Dcn-bjnews%26itemtype%3Dnews\u0026first_launch_web\u003dtrue\u0026channel\u003drec#miui_back_info\u003d0\u0026_miui_fullscreen\u003d1";
        String url21 = "mibrowser://infoflow?web_url\u003dhttps%3A%2F%2Fpartners.sina.cn%2Fhtml%2Fxiaomi%2Fbrowser%2Farticle%3Fwm%3D3993%26docUrl%3Dhttp%253A%252F%252Fk.sina.cn%252Farticle_1686546714_6486a91a020018xd6.html%26en_dataid%3D2c01ed59eef9db710ce9e92d62cd790722770cb2282e7f343f27cae5a724f64d%26version%3D2%26mibusinessId%3Dmiuibrowser%26env%3Dproduction%26miref%3Dnewsin_push_model%26infotype%3D1%26_miui_fullscreen%3D1%26utm_source%3Dxmpush%26mifloat%3Dnewscat%26docid%3Dsina_dae0eddbaec93625bba4beb2e54f1929%26cp%3Dcn-sina%26itemtype%3Dnews\u0026first_launch_web\u003dtrue\u0026channel\u003drec#miui_back_info\u003d0\u0026_miui_fullscreen\u003d1";

        Intent intent = new Intent(Intent.ACTION_VIEW);
        String targetUrl = url;
        if (TextUtils.isEmpty(targetUrl)) {
            targetUrl = url15;
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
        Intent intent = null;
//        try {
//            intent = Intent.parseUri(deepLinkUrl, Intent.URI_INTENT_SCHEME);

//        String novelUrl = "https://reader.browser.duokan.com/hs/market/shelf?_miui_fullscreen=1&_miui_orientation=portrait&source=newhome";
        String novelUrl = "https://reader.browser.duokan.com/v2/#page=user&source=newhome&miback=true";
//        String url31 = "https://app.myzaker.com/news/article.php?f=xiaomi&pk=5e708d11b15ec06db90baa52&cp=123&docid=123&itemtype=news";
        if (TextUtils.isEmpty(deepLinkUrl)) {
            deepLinkUrl = "https://app.myzaker.com/news/article.php?f=xiaomi&pk=5e708d11b15ec06db90baa52&cp=123&docid=123&itemtype=news";
            deepLinkUrl = "https://reader.browser.duokan.com/v2/#page=user&source=newhome&miback=true";
            deepLinkUrl = "intent:#Intent;action=android.intent.action.WEB_SEARCH;package=com.android.browser;S.query=hello;end";
        }
//        String novelUrl = "https://www.baidu.com";
        if (deepLinkUrl.contains("#Intent")) {
            try {
                intent = Intent.parseUri(deepLinkUrl, 0);
            } catch (URISyntaxException e) {
            }
        }
        if (intent == null) {
            intent = new Intent(Intent.ACTION_VIEW);
//        intent.setPackage("com.android.browser");
//            intent.setAction("com.duokan.shop.mibrowser.actions.SHOW_STORE");
            intent.setData(Uri.parse(deepLinkUrl));
        }
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

    private void startQQBrowser() {
        String url = "";
        Intent intent = new Intent(this, QQBrowserTestActivity.class);


//        startActivity(intent);


//        Uri data = Uri.parse("mibrowser://infoflow?web_url=https%3A%2F%2Fhot.browser.miui.com%2Fv7%2F%23page%3Dinline-video-detail%26id%3Dnetease_VI00RANDQVMRC79%26cp%3Dcn-netease-youliao-browser%26docid%3Dnetease_VI00RANDQVMRC79%26itemtype%3Dinline_video%26video_url%3Dhttps%253A%252F%252Fvideo-nos.yiyouliao.com%252Ficvtd-shd-20201103-b74f9ee7bda6fcd5c6646d44bcf9fb55.mp4%253Ftime%253D1604478533%2526signature%253DED2EA17868E4992A22EFEE3C0E279339%2526yiyouliao_channel%253Dxiaomisv_video%26originCpId%3DVI00RANDQVMRC79%26_miui_bottom_bar%3Dcomment%26infotype%3D2%26_miui_fullscreen%3D1%26a%3Da%3Fmiref%3Dnewsin_push_model%26utm_source%3Dxmpush%26mifloat%3Dnewscat%26docid%3Dnetease_VI00RANDQVMRC79%26cp%3Dcn-netease-youliao%26itemtype%3Dnews&first_launch_web=true&channel=rec#miui_back_info=0&_miui_fullscreen=1");
//        Uri data = Uri.parse("mibrowser://infoflow?web_url=https%3A%2F%2Fpartners.sina.cn%2Fhtml%2Fxiaomi%2Fbrowser%2Farticle%3Fwm%3D3993%26docUrl%3Dhttp%253A%252F%252Fk.sina.cn%252Farticle_1686546714_6486a91a020018xd6.html%26en_dataid%3D2c01ed59eef9db710ce9e92d62cd790722770cb2282e7f343f27cae5a724f64d%26version%3D2%26mibusinessId%3Dmiuibrowser%26env%3Dproduction%26miref%3Dnewsin_push_model%26infotype%3D1%26_miui_fullscreen%3D1%26utm_source%3Dxmpush%26mifloat%3Dnewscat%26docid%3Dsina_dae0eddbaec93625bba4beb2e54f1929%26cp%3Dcn-sina%26itemtype%3Dnews&first_launch_web=true&channel=rec#miui_back_info=0&_miui_fullscreen=1\\");
//        Uri data = Uri.parse("mibrowser://infoflow?web_url=https%3A%2F%2Fhot.browser.miui.com%2Frec%2Fcontent%2Frenminwang_ZXB-ORIGIN-5213513%3FcontentId%3Drenminwang_ZXB-ORIGIN-5213513%26version%3D2%26mibusinessId%3Dmiuibrowser%26env%3Dproduction%26miref%3Dnewsin_push_model%26infotype%3D1%26_miui_fullscreen%3D1%26utm_source%3Dxmpush%26mifloat%3Dnewscat%26docid%3Drenminwang_ZXB-ORIGIN-5213513%26cp%3Dcn-renminwang%26itemtype%3Dnews&first_launch_web=true&channel=rec#miui_back_info=0&_miui_fullscreen=1");
//        Uri data = Uri.parse("mibrowser://infoflow?web_url=https%3A%2F%2Fpartners.sina.cn%2Fhtml%2Fxiaomi%2Fbrowser%2Farticle%3Fwm%3D3993%26docUrl%3Dhttp%253A%252F%252Fk.sina.cn%252Farticle_1664221137_6331ffd102000rklz.html%26en_dataid%3Dbb63b46aab5ebf19e6ee85dfd02707b6fbe32c4009c855c06c466408901550c4%26version%3D2%26mibusinessId%3Dmiuibrowser%26env%3Dproduction%26miref%3Dnewsin_push_model%26infotype%3D1%26_miui_fullscreen%3D1%26utm_source%3Dxmpush%26mifloat%3Dnewscat%26docid%3Dsina_34b0c89710c835cba2d1ff94d2d067f0%26cp%3Dcn-sina%26itemtype%3Dnews&first_launch_web=true&channel=rec#miui_back_info=0&_miui_fullscreen=1");
//        Uri data = Uri.parse("mibrowser://infoflow?web_url=https%3A%2F%2Fpartners.sina.cn%2Fhtml%2Fxiaomi%2Fbrowser%2Farticle%3Fwm%3D3993%26docUrl%3Dhttp%253A%252F%252Fk.sina.cn%252Farticle_2803301701_a716fd45020017ft0.html%26en_dataid%3Dde92bb9670c4cd51ad5ff43db405e2b85aa39c814b1ffc77fa6a3e50cf003399%26version%3D2%26mibusinessId%3Dmiuibrowser%26env%3Dproduction%26miref%3Dnewsin_push_model%26infotype%3D1%26_miui_fullscreen%3D1%26utm_source%3Dxmpush%26mifloat%3Dnewscat%26docid%3Dsina_73b752784e4c3894a2990e925e15a11b%26cp%3Dcn-sina%26itemtype%3Dnews&first_launch_web=true&channel=rec#miui_back_info=0&_miui_fullscreen=1");
//        Uri data = Uri.parse("mibrowser://infoflow?web_url=https%3A%2F%2Fpartners.sina.cn%2Fhtml%2Fxiaomi%2Fbrowser%2Farticle%3Fwm%3D3993%26docUrl%3Dhttp%253A%252F%252Fnews.sina.cn%252F2020-11-03%252Fdetail-iiznctkc9306402.d.html%26en_dataid%3Dd7881228aff8a80e325dc14ba596c26555e5c59bdb1eb2f13a64d0da2de62ea1%26version%3D2%26mibusinessId%3Dmiuibrowser%26env%3Dproduction%26miref%3Dnewsin_push_model%26infotype%3D1%26_miui_fullscreen%3D1%26utm_source%3Dxmpush%26mifloat%3Dnewscat%26docid%3Dsina_5c9c8ccb2ca735f6ac75d4d8d0a74616%26cp%3Dcn-sina%26itemtype%3Dnews&first_launch_web=true&channel=rec#miui_back_info=0&_miui_fullscreen=1");
//        Uri data = Uri.parse("mibrowser://infoflow?web_url=https%3A%2F%2Frss-api.yiyouliao.com%2Fapi-server%2Frss%2Fxiaomi%2Fitem%2FII001HO9D5PH6MI.html%3Fversion%3D2%26mibusinessId%3Dmiuibrowser%26env%3Dproduction%26miref%3Dnewsin_push_model%26infotype%3D1%26_miui_fullscreen%3D1%26utm_source%3Dxmpush%26mifloat%3Dnewscat%26docid%3Dnetease_II001HO9D5PH6MI%26cp%3Dcn-netease-youliao%26itemtype%3Dnews&first_launch_web=true&channel=rec#miui_back_info=0&_miui_fullscreen=1");
//        Uri data = Uri.parse("mibrowser://infoflow?web_url=https%3A%2F%2Fhot.browser.miui.com%2Frec%2Fcontent%2Fbjnews_160449023915284%3FcontentId%3Dbjnews_160449023915284%26version%3D2%26mibusinessId%3Dmiuibrowser%26env%3Dproduction%26miref%3Dnewsin_push_model%26infotype%3D1%26_miui_fullscreen%3D1%26utm_source%3Dxmpush%26mifloat%3Dnewscat%26docid%3Dbjnews_160449023915284%26cp%3Dcn-bjnews%26itemtype%3Dnews&first_launch_web=true&channel=rec#miui_back_info=0&_miui_fullscreen=1");

        Uri data = Uri.parse("mibrowser://infoflow?web_url=mailto:nobody@google.com");
//        Uri data = Uri.parse("mibrowser://infoflow?web_url=https%3A%2F%2Frss-api.yiyouliao.com%2Fapi-server%2Frss%2Fxiaomi%2Fitem%2FII001HO9D5PH6MI.html%3Fversion%3D2%26mibusinessId%3Dmiuibrowser%26env%3Dproduction%26miref%3Dnewsin_push_model%26infotype%3D1%26_miui_fullscreen%3D1%26utm_source%3Dxmpush%26mifloat%3Dnewscat%26docid%3Dnetease_II001HO9D5PH6MI%26cp%3Dcn-netease-youliao%26itemtype%3Dnews&first_launch_web=true&channel=rec#miui_back_info=0&_miui_fullscreen=1");
//        Uri data = Uri.parse("mibrowser://infoflow?web_url=https%3A%2F%2Fpartners.sina.cn%2Fhtml%2Fxiaomi%2Fbrowser%2Farticle%3Fwm%3D3993%26docUrl%3Dhttp%253A%252F%252Fnews.sina.cn%252F2020-11-03%252Fdetail-iiznctkc9306402.d.html%26en_dataid%3Dd7881228aff8a80e325dc14ba596c26555e5c59bdb1eb2f13a64d0da2de62ea1%26version%3D2%26mibusinessId%3Dmiuibrowser%26env%3Dproduction%26miref%3Dnewsin_push_model%26infotype%3D1%26_miui_fullscreen%3D1%26utm_source%3Dxmpush%26mifloat%3Dnewscat%26docid%3Dsina_5c9c8ccb2ca735f6ac75d4d8d0a74616%26cp%3Dcn-sina%26itemtype%3Dnews&first_launch_web=true&channel=rec#miui_back_info=0&_miui_fullscreen=1");
//        Uri data = Uri.parse("mibrowser://infoflow?web_url=https%3A%2F%2Fhot.browser.miui.com%2Frec%2Fcontent%2Frenminwang_85f867ff5e79675a%3FcontentId%3Drenminwang_85f867ff5e79675a%26version%3D2%26mibusinessId%3Dmiuibrowser%26env%3Dproduction%26miref%3Dnewsin_push_model%26infotype%3D1%26_miui_fullscreen%3D1%26utm_source%3Dlocalpush%26docid%3Dae630fa7d6bd4233a0716348bb5a7898%26cp%3Dcn-browser-push%26itemtype%3Dnews&miui_back_info=0&_miui_fullscreen=1&utm_source=localpush&first_launch_web=true");
//        Uri data = Uri.parse("mibrowser://infoflow?web_url\u003dhttps%3A%2F%2Fhot.browser.miui.com%2Fv7%2F%23page%3Dinline-video-detail%26id%3Dnetease_VI00RANDQVMRC79%26cp%3Dcn-netease-youliao-browser%26docid%3Dnetease_VI00RANDQVMRC79%26itemtype%3Dinline_video%26video_url%3Dhttps%253A%252F%252Fvideo-nos.yiyouliao.com%252Ficvtd-shd-20201103-b74f9ee7bda6fcd5c6646d44bcf9fb55.mp4%253Ftime%253D1604478533%2526signature%253DED2EA17868E4992A22EFEE3C0E279339%2526yiyouliao_channel%253Dxiaomisv_video%26originCpId%3DVI00RANDQVMRC79%26_miui_bottom_bar%3Dcomment%26infotype%3D2%26_miui_fullscreen%3D1%26a%3Da%3Fmiref%3Dnewsin_push_model%26utm_source%3Dxmpush%26mifloat%3Dnewscat%26docid%3Dnetease_VI00RANDQVMRC79%26cp%3Dcn-netease-youliao%26itemtype%3Dnews\u0026first_launch_web\u003dtrue\u0026channel\u003drec#miui_back_info\u003d0\u0026_miui_fullscreen\u003d1");
//        Uri data = Uri.parse("mibrowser://infoflow?web_url\u003dhttps%3A%2F%2Fpartners.sina.cn%2Fhtml%2Fxiaomi%2Fbrowser%2Farticle%3Fwm%3D3993%26docUrl%3Dhttp%253A%252F%252Fk.sina.cn%252Farticle_1686546714_6486a91a020018xd6.html%26en_dataid%3D2c01ed59eef9db710ce9e92d62cd790722770cb2282e7f343f27cae5a724f64d%26version%3D2%26mibusinessId%3Dmiuibrowser%26env%3Dproduction%26miref%3Dnewsin_push_model%26infotype%3D1%26_miui_fullscreen%3D1%26utm_source%3Dxmpush%26mifloat%3Dnewscat%26docid%3Dsina_dae0eddbaec93625bba4beb2e54f1929%26cp%3Dcn-sina%26itemtype%3Dnews\u0026first_launch_web\u003dtrue\u0026channel\u003drec#miui_back_info\u003d0\u0026_miui_fullscreen\u003d1");
        boolean isOpaque = data.isOpaque();
        String web_url = isOpaque ? "" : data.getQueryParameter("web_url");
        String miRef = null;
        if (!TextUtils.isEmpty(web_url)) {
            Uri url2 = Uri.parse(web_url);
            miRef = url2.getQueryParameter("miref");
        }
        Loger.d(TAG, "-->startQQBrowser: miRef=" + miRef + ", web_url=" + web_url);
    }

    private void checkPackageSignature() {
        EditText inputView = findViewById(R.id.target_package_name);
        TextView resultView = findViewById(R.id.md5_board);

        String targetPackageName = inputView.getText().toString();
        if (TextUtils.isEmpty(targetPackageName)) {
            targetPackageName = "com.android.browser";
        }

        String signMd5 = SecurityUtil.getPackageSignMd5Str(this, targetPackageName);
        Loger.d(TAG, "-->checkPackageSignature(), packageName=" + targetPackageName + ",signature md5=" + signMd5);
        resultView.setText(targetPackageName + ": " + signMd5);
    }

    private void triggerSearch() {
        EditText inputView = findViewById(R.id.query_content);
        CheckBox debugFlagView = findViewById(R.id.mibrowser_debug_search);
        String queryContent = inputView.getText().toString();
        boolean useDebugPackage = debugFlagView.isChecked();

        Loger.d(TAG, "-->triggerSearch: content=" + queryContent);
        if (!TextUtils.isEmpty(queryContent)) {
            Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
            intent.putExtra("query", queryContent);
            if (useDebugPackage) {
                intent.setPackage("com.android.browser.debug");
            }

            PackageManager packageManager = getPackageManager();
            ResolveInfo resolveInfo = packageManager.resolveActivity(intent, 0);
            Loger.d(TAG, "-->triggerSearch(): resolveInfo=" + resolveInfo);

            startActivity(intent);
        } else {
            TipsToast.getInstance().showTipsText("搜索内容为空");
        }

    }
}
