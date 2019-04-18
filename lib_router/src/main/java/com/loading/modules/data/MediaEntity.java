package com.loading.modules.data;

import android.provider.MediaStore;
import android.text.TextUtils;

import com.loading.common.utils.CommonUtils;

import java.io.Serializable;

/**
 * Created by loading on 2018/8/3.
 */
public class MediaEntity implements Serializable {
    private static final long serialVersionUID = 2664205324347468636L;
    public static final int MEDIA_TYPE_IMAGE = MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
    public static final int MEDIA_TYPE_VIDEO = MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;
    //    public static final int MEDIA_TYPE_AUDIO = MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO;
    //    public static final int MEDIA_TYPE_PLAYLIST = MediaStore.Files.FileColumns.MEDIA_TYPE_PLAYLIST;

    private String itemId;//数据库索引主键
    private int mediaType;
    private String path;
    private String thumbnailsPath;
    private String mimeType;
    private int size;

    private long durationL;
    private int width;
    private int height;
    private int videoRotation;
    private long modifyTime;

    transient private boolean isSelected = false;

    public MediaEntity() {}

    public static MediaEntity newInstance(String idx,
                                          String path,
                                          String thumbPath,
                                          String mimeType,
                                          int mediaType,
                                          int size) {
        MediaEntity mediaEntity = new MediaEntity();
        mediaEntity.setItemId(idx);
        mediaEntity.setPath(path);
        mediaEntity.setThumbnailsPath(thumbPath);
        mediaEntity.setMimeType(mimeType);
        mediaEntity.setMediaType(mediaType);
        mediaEntity.setSize(size);
        return mediaEntity;
    }

    public static MediaEntity newInstance(MediaEntity mediaEntity) {
        return mediaEntity != null ? newInstance(mediaEntity.getItemId(),
                mediaEntity.getPath(),
                mediaEntity.getThumbnailsPath(),
                mediaEntity.getMimeType(),
                mediaEntity.getMediaType(),
                mediaEntity.getSize()) : new MediaEntity();
    }

    public static MediaEntity newInstance(String idx,
                                          int mediaType,
                                          String path,
                                          String thumbPath,
                                          String mimeType,
                                          int size) {
        MediaEntity mediaEntity = new MediaEntity();
        mediaEntity.setItemId(idx);
        mediaEntity.setMediaType(mediaType);
        mediaEntity.setPath(path);
        mediaEntity.setThumbnailsPath(thumbPath);
        mediaEntity.setMimeType(mimeType);
        mediaEntity.setSize(size);
        return mediaEntity;
    }

    public MediaEntity copy() {
        MediaEntity mediaEntity = new MediaEntity();
        mediaEntity.setPath(getPath());
        mediaEntity.setMediaType(getMediaType());
        mediaEntity.setThumbnailsPath(getThumbnailsPath());
        mediaEntity.setDurationL(getDurationL());
        return mediaEntity;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public void updateField(String idx,
                            int mediaType,
                            String path,
                            String thumbPath,
                            String mimeType,
                            int size) {
        this.itemId = idx;
        this.path = path;
        this.thumbnailsPath = thumbPath;
        this.mimeType = mimeType;
        this.mediaType = mediaType;
        this.size = size;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getPath() {
        return path;
    }

    public String getImgUrl() {
        String imgUrl = !TextUtils.isEmpty(thumbnailsPath) || isVideo() ? thumbnailsPath : path;
        if (!TextUtils.isEmpty(imgUrl) && !imgUrl.startsWith("http")) {
            imgUrl = CommonUtils.FILE_SCHEME_PREFIX + imgUrl;
        }
        return imgUrl;
    }

    public String getImgPath() {
        return !TextUtils.isEmpty(thumbnailsPath) || isVideo() ? thumbnailsPath : path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getMediaType() {
        return mediaType;
    }

    public void setMediaType(int mediaType) {
        this.mediaType = mediaType;
    }

    public String getThumbnailsPath() {
        return thumbnailsPath;
    }

    public void setThumbnailsPath(String thumbnailsPath) {
        this.thumbnailsPath = thumbnailsPath;
    }

//    public String getValidPath() {
//        return !TextUtils.isEmpty(thumbnailsPath) ? thumbnailsPath : path;
//    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public long getDurationL() {
        return durationL;
    }

    public void setDurationL(long durationL) {
        this.durationL = durationL;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getVideoRotation() {
        return videoRotation;
    }

    public void setVideoRotation(int videoRotation) {
        this.videoRotation = videoRotation;
    }

    public long getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(long modifyTime) {
        this.modifyTime = modifyTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MediaEntity that = (MediaEntity) o;
        return mediaType == that.mediaType && TextUtils.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        int result = path != null ? path.hashCode() : 0;
        result = 31 * result + mediaType;
        result = 31 * result + size;
        return result;
    }

    public boolean isVideo() {
        return mediaType == MEDIA_TYPE_VIDEO;
    }

    public boolean isImage() {
        return mediaType == MEDIA_TYPE_IMAGE;
    }

    public boolean isLocalResource() {
        return TextUtils.isEmpty(path) || !CommonUtils.isUrl(path);
    }

    public boolean getThumbnailsPathLocal() {
        return TextUtils.isEmpty(thumbnailsPath) || !thumbnailsPath.startsWith("http:") && !thumbnailsPath.startsWith("https:");
    }

    /**
     * The video rotation angle may be 0, 90, 180, or 270 degrees.
     * horizontal : 0,180
     * vertical : 90,270
     * @return video aspect
     */
    public float getVideoAspect() {
        if (getHeight() != 0 && getWidth() != 0) {
            float aspect;
            if (getVideoRotation() == 0 || getVideoRotation() == 180) {
                aspect = (float)getWidth() / (float)getHeight();
            } else {
                aspect = (float)getHeight() / (float)getWidth();
            }
            return aspect;
        }
        return 0;
    }
}
