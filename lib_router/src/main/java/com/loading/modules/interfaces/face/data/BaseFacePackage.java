package com.loading.modules.interfaces.face.data;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.widget.TextView;

import com.loading.common.component.CApplication;
import com.loading.common.utils.CommonUtils;
import com.loading.common.utils.Loger;
import com.loading.common.utils.SystemUtil;
import com.loading.common.widget.ImageSpanEx;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 表情包类，包含一组表情，可能预置在本地，也可能来源于网络
 * Created by loading in 2019.02.19
 */
public abstract class BaseFacePackage {
    private static final String TAG = "BaseFacePackage";

    private Pattern mFaceRegexPattern = null;
    private static HashMap<String, SoftReference<Bitmap>> FACE_CACHE = new HashMap<>();

    protected Properties mFaceName2FaceFileMappingProperties = null;
    protected ArrayList<String> mFaceNameList;   //需要展示在表情面板中的表情名字列表，按顺序排列

    private int mRowCnt;
    private int mColumnCnt;

    private int mGridItemPaddingLR;
    private int mGridItemVerticalSpacing;

    protected abstract void init();

    protected abstract Bitmap createFaceBitmapForFaceFile(String faceFileName);

    public abstract Object getPackageIndicatorRes();

    public Bitmap getFaceBitmapAtPosition(int pos) {
        Bitmap faceBitmap = null;
        if (pos >= 0 && mFaceNameList != null && mFaceNameList.size() > pos) {
            faceBitmap = getFaceBitmap(mFaceNameList.get(pos));
        }
        return faceBitmap;
    }

    public String getFaceStringAtPosition(int pos) {
        String faceString = null;
        if (pos >= 0 && mFaceNameList != null && mFaceNameList.size() > pos) {
            faceString = mFaceNameList.get(pos);
        }
        return faceString;
    }

    public List<FacePageItems> getFacePageList() {
        List<FacePageItems> pageList = new ArrayList<>();
        int itemCntPerPage = mRowCnt * mColumnCnt - 1;
        if (mFaceNameList != null && mFaceNameList.size() > 0 && itemCntPerPage > 0) {
            for (int i = 0; i * itemCntPerPage < mFaceNameList.size(); i++) {
                FacePageItems pageItems = new FacePageItems(this, i, itemCntPerPage);
                pageList.add(pageItems);
            }
        }
        return pageList;
    }

    public int getFacePageCnt() {
        int pageCnt = 0;
        int itemCntPerPage = mRowCnt * mColumnCnt - 1;
        if (mFaceNameList != null && mFaceNameList.size() > 0 && itemCntPerPage > 0) {
            pageCnt = mFaceNameList.size() / itemCntPerPage;
            if (mFaceNameList.size() % itemCntPerPage != 0) {
                pageCnt++;
            }
        }
        return pageCnt;
    }

    private String createFaceRegexStr() {
        StringBuilder regexStrBuilder = new StringBuilder();
        if (mFaceNameList != null && mFaceNameList.size() > 0) {
            for (int i = 0; i < mFaceNameList.size(); i++) {
                String faceNameItem = mFaceNameList.get(i);
                if (!TextUtils.isEmpty(faceNameItem)) {
                    regexStrBuilder.append("(");
                    regexStrBuilder.append(faceNameItem.replace("[", "\\[").replace("]", "\\]"));
                    regexStrBuilder.append(")");
                    if (i < mFaceNameList.size() - 1) {
                        regexStrBuilder.append("|");
                    }
                }
            }
        }
        return regexStrBuilder.toString();
    }

    private Pattern getPattern() {
        if (mFaceRegexPattern == null) {
            mFaceRegexPattern = Pattern.compile(createFaceRegexStr());
        }
        return mFaceRegexPattern;
    }

    public SpannableStringBuilder convertToSpannableStr(CharSequence initSequence, float textSize, TextView txtView) {
        SpannableStringBuilder builder = null;
        if (initSequence != null && initSequence.length() > 0 && (txtView != null || textSize > 0.0001f)) {
            float imageSize = textSize;
            if (txtView != null) {
                imageSize = getImageSizeByTextView(txtView) * 0.8f;
            }
            if (imageSize > 0.0001f) {
                ImageSpanEx imageSpan;
                Bitmap bitmap;
                Drawable tDrawable;
                Matcher mat = getPattern().matcher(initSequence);
                int lastEnd = 0;
                while (mat.find()) {
                    if (builder == null) {
                        builder = new SpannableStringBuilder();
                    }

                    if (mat.start() >= lastEnd) {
                        builder.append(initSequence, lastEnd, mat.start());
                        builder.append(initSequence, mat.start(), mat.end());
                    } else {
                        builder.append(initSequence, mat.start(), mat.end());
                    }
                    lastEnd = mat.end();
                    bitmap = getFaceBitmap(mat.group());
                    if (bitmap != null) {
                        tDrawable = new BitmapDrawable(CApplication.getAppContext().getResources(), bitmap);
                        tDrawable.setBounds(0, 0, (int) imageSize, (int) imageSize);
                        imageSpan = new ImageSpanEx(tDrawable, ImageSpanEx.ALIGN_CENTER);
                        int startPos = builder.length() - mat.group().length();
                        if (startPos >= 0) {
                            builder.setSpan(imageSpan, startPos, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                    }
                }
                if (builder == null) {
                    if (initSequence instanceof SpannableStringBuilder) {
                        builder = (SpannableStringBuilder) initSequence;
                    } else {
                        builder = new SpannableStringBuilder(initSequence);
                    }
                } else if (lastEnd > 0 && initSequence.length() > lastEnd) { // 表情+文字，以文字结束
                    builder.append(initSequence, lastEnd, initSequence.length());
                }
            }
        }
        return builder;
    }

//    public SpannableStringBuilder convertToSpannableStr(String iText, float textSize, TextView txtView) {
//        SpannableStringBuilder builder = null;
//        if (iText != null && iText.length() > 0 && (txtView != null || textSize > 0.0001f)) {
//            float imageSize = textSize;
//            if (txtView != null) {
//                imageSize = getImageSizeByTextView(txtView) * 0.7f;
//            }
//            if (imageSize > 0.0001f) {
//                ImageSpan imageSpan;
//                Bitmap bitmap;
//                Drawable tDrawable;
//                Matcher mat = getPattern().matcher(iText);
//                int lastEnd = 0;
//                while (mat.find()) {
//                    if (builder == null) {
//                        if (txtView != null) {
//                            Object tObj = txtView.getTag();
//                            if (tObj != null && tObj instanceof SpannableStringBuilder) {
//                                builder = (SpannableStringBuilder) tObj;
//                                builder.clear();
//                            } else {
//                                builder = new SpannableStringBuilder();
//                            }
//                            txtView.setTag(builder);
//                        } else {
//                            builder = new SpannableStringBuilder();
//                        }
//                    }
//
//                    if (mat.start() >= lastEnd) {
//                        builder.append(iText, lastEnd, mat.start());
//                        builder.append(iText, mat.start(), mat.end());
//                    } else {
//                        builder.append(iText, mat.start(), mat.end());
//                    }
//                    lastEnd = mat.end();
//                    bitmap = getFaceBitmap(mat.group());
//                    if (bitmap != null) {
//                        tDrawable = new BitmapDrawable(CApplication.getAppContext().getResources(), bitmap);
//                        tDrawable.setBounds(0, 0, (int) imageSize, (int) imageSize);
//                        imageSpan = new ImageSpanEx(tDrawable, ImageSpanEx.ALIGN_CENTER);
//                        int startPos = builder.length() - mat.group().length();
//                        if (startPos >= 0) {
//                            builder.setSpan(imageSpan, startPos, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                        }
//                    }
//                }
//                if (builder == null) {
//                    builder = new SpannableStringBuilder(iText);
//                }
//                // 表情+文字，以文字结束
//                if (lastEnd > 0 && iText.length() > lastEnd) {
//                    builder.append(iText, lastEnd, iText.length());
//                }
//            }
//        }
//        return builder;
//    }

    private Bitmap getFaceBitmap(final String readableFaceName) {
        if (FACE_CACHE.containsKey(readableFaceName)) {
            SoftReference<Bitmap> ref = FACE_CACHE.get(readableFaceName);
            if (ref.get() != null) {
                return ref.get();
            } else {
                Loger.d("FaceImage", "For key:" + readableFaceName + ", cache expired.");
                FACE_CACHE.remove(readableFaceName);
            }
        } else {
            Loger.d("FaceImage", "For key:" + readableFaceName + ", not cache yet");
        }

        return createFaceBitmapForFaceFile(mFaceName2FaceFileMappingProperties != null ? mFaceName2FaceFileMappingProperties.getProperty(readableFaceName) : null);
    }

    public static float getImageSizeByTextView(TextView textView) {
        float imageSize = SystemUtil.dpToPx(16);
        if (textView != null && textView.getPaint() != null) {
            TextPaint paint = textView.getPaint();
            imageSize = paint.descent() - paint.ascent();
            imageSize += imageSize / 5;
        }
        return imageSize;
    }

    public void initRowColumnLayout(int rowCnt, int columnCnt, int itemPaddingLR, int itemVerticalSpacing) {
        mRowCnt = rowCnt;
        mColumnCnt = columnCnt;
        mGridItemPaddingLR = itemPaddingLR;
        mGridItemVerticalSpacing = itemVerticalSpacing;
    }

    public int getRowCnt() {
        return mRowCnt;
    }

    public int getColumnCnt() {
        return mColumnCnt;
    }

    public int getGridItemPaddingLR() {
        return mGridItemPaddingLR;
    }

    public int getGridItemVerticalSpacing() {
        return mGridItemVerticalSpacing;
    }

    /**
     * 表情包是否仍然有效，兼容表情图片缓存被清理的情况
     *
     * @return
     */
    public boolean isPackageValid() {
        //对第一与最后一张表情进行抽样检查
        return !CommonUtils.isEmpty(mFaceNameList) && getFaceBitmap(mFaceNameList.get(0)) != null && getFaceBitmap(mFaceNameList.get(mFaceNameList.size() - 1)) != null;
    }
}
