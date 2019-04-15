package com.tencent.qqsports.commentbar.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.tencent.qqsports.commentbar.R;
import com.tencent.qqsports.common.pojo.MediaEntity;
import com.tencent.qqsports.common.util.CommonUtil;
import com.tencent.qqsports.imagefetcher.ImageFetcher;
import com.tencent.qqsports.imagefetcher.view.RecyclingImageView;

public class AddMediaItemView extends RelativeLayout implements View.OnClickListener {
    private ImageView mAddPicBtn = null;
    private RecyclingImageView mPicContent = null;
    private View mPicContainer = null;
    private View mDelBtnContainer = null;
    private ImageView mVideoLogo = null;

    private IMediaItemClickListener mediaItemClickListener;
    private MediaEntity mediaEntity;
    private int mediaIndex = -1;
    private float mScaleRate = 0;

    private int mTargetInitWidth = 0;
    private int mTargetInitHeight = 0;

    public AddMediaItemView(Context context) {
        this(context, null);
    }

    public AddMediaItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AddMediaItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        inflate(context, R.layout.add_media_item_view, this);
        mAddPicBtn = findViewById(R.id.addpic);
        mPicContainer = findViewById(R.id.selected_media_container);
        mPicContent = findViewById(R.id.pic_content);
        mDelBtnContainer = findViewById(R.id.del_btn_container);
        mVideoLogo = findViewById(R.id.video_logo);
        mPicContainer.setOnClickListener(this);
        mAddPicBtn.setOnClickListener(this);
        mDelBtnContainer.setOnClickListener(this);

        updateMediaContent();
    }

    public void setMediaItemClickListener(IMediaItemClickListener mediaItemClickListener) {
        this.mediaItemClickListener = mediaItemClickListener;
    }

    public void setMediaContent(MediaEntity mediaEntity, int mediaIndex) {
        this.mediaEntity = mediaEntity;
        this.mediaIndex = mediaIndex;
        updateMediaContent();
    }

    private void updateMediaContent() {
        if (mAddPicBtn != null && mDelBtnContainer != null && mVideoLogo != null && mPicContainer != null) {
            if (mediaEntity != null) {
                String filePathUrl = CommonUtil.FILE_SCHEME_PREFIX + mediaEntity.getPath();
                ImageFetcher.loadImage(mPicContent, filePathUrl);
                mAddPicBtn.setVisibility(View.GONE);
                mDelBtnContainer.setVisibility(View.VISIBLE);
                mPicContainer.setVisibility(View.VISIBLE);
                mVideoLogo.setVisibility(mediaEntity.isVideo() ? View.VISIBLE : View.GONE);
            } else {
                mAddPicBtn.setVisibility(View.VISIBLE);
                mDelBtnContainer.setVisibility(View.GONE);
                mPicContainer.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.addpic) {
            if (mediaItemClickListener != null) {
                mediaItemClickListener.onAddMediaBtnClick();
            }
        } else if (v.getId() == R.id.selected_media_container) {
            if (mediaItemClickListener != null) {
                mediaItemClickListener.onMediaContentClick(this, mediaIndex);
            }
        } else if (v.getId() == R.id.del_btn_container) {
            if (mediaItemClickListener != null) {
                mediaItemClickListener.onDelMediaBtnClick(this, mediaIndex);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mScaleRate > 0 && mTargetInitWidth > 0 && mTargetInitHeight > 0) {
            widthMeasureSpec = MeasureSpec.makeMeasureSpec((int) (mTargetInitWidth * mScaleRate), MeasureSpec.EXACTLY);
            heightMeasureSpec = MeasureSpec.makeMeasureSpec((int) (mTargetInitHeight * mScaleRate), MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setScaleRate(float scaleRate) {
        mScaleRate = scaleRate;
    }

    public void setTargetInitWH(int width, int height) {
        mTargetInitWidth = width;
        mTargetInitHeight = height;
    }

    public float getScaleRate() {
        return mScaleRate;
    }

    public interface IMediaItemClickListener {
        void onAddMediaBtnClick();

        void onMediaContentClick(View itemView, int mediaItemIndex);

        void onDelMediaBtnClick(View itemView, int mediaItemIndex);
    }
}
