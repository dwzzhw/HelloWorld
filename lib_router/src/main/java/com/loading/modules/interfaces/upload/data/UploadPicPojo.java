package com.loading.modules.interfaces.upload.data;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.util.List;

public class UploadPicPojo implements Serializable {
    private static final long serialVersionUID = 714744355045364053L;
    private int code = -1;
    private UpPicRespData data;

    public UpPicInfo getPicInfo(int idx) {
        UpPicInfo result = null;
        if (data != null) {
            List<UpPicInfo> picUrlList = data.getPicture();
            if (picUrlList != null) {
                int picUrlSize = picUrlList.size();
                if (picUrlSize > idx) {
                    result = picUrlList.get(idx);
                }
            }
        }
        return result;
    }

    /*public void fakeUrls() {
        int size = getPicUrlSize();
        UpPicInfo tPicInfo = null;
        for (int i = 0; i < size; i++) {
            tPicInfo = getPicInfo(i);
            if (tPicInfo != null) {
                tPicInfo.fakeUrls();
            }
        }
    }*/

    public List<UpPicInfo> getPicture() {

        return data != null ? data.getPicture() : null;
    }


    public boolean isEffectPictures() {
        boolean isRet = true;
        List<UpPicInfo> upPicInfos = getPicture();
        if (upPicInfos != null) {
            UpPicInfo tmp;
            for (int i = 0; i < upPicInfos.size(); i++) {
                tmp = upPicInfos.get(i);
                if (tmp == null || TextUtils.isEmpty(tmp.getUrl())) {
                    isRet = false;
                    break;
                }
            }
        } else {
            isRet = false;
        }
        return isRet;
    }

    public String getPicUrl(int idx) {
        String picUrl = null;
        UpPicInfo tPicInfo = getPicInfo(idx);
        if (tPicInfo != null) {
            picUrl = tPicInfo.getUrl();
        }
        return picUrl;
    }

    /*public int getPicUrlSize() {
        int picUrlSize = 0;
        if (data != null) {
            List<UpPicInfo> picUrlList = data.getPicture();
            if (picUrlList != null) {
                picUrlSize = picUrlList.size();
            }
        }
        return picUrlSize;
    }*/

    public boolean isRetSuccess() {
        return code == 0;
    }


    public static class UpPicRespData implements Serializable {
        private static final long serialVersionUID = 2009995892812856361L;
        private List<UpPicInfo> picture;

        public List<UpPicInfo> getPicture() {
            return picture;
        }

        public void setPicture(List<UpPicInfo> picture) {
            this.picture = picture;
        }

        /**
         * 返回上传图片结果的URL
         *
         * @return
         */
        public String getUploadUrls() {
            String uploadUrl = null;
            if (picture != null && picture.size() > 0) {
                Gson g = new Gson();
                uploadUrl = g.toJson(picture);
            }
            return uploadUrl;
        }

        public static UpPicInfo parseFirstPicInfoFromStr(String picInfoListStr) {
            List<UpPicInfo> picList = parsePicInfoFromStr(picInfoListStr);
            return picList != null && picList.size() > 0 ? picList.get(0) : null;
        }

        public static List<UpPicInfo> parsePicInfoFromStr(String picInfoListStr) {
            List<UpPicInfo> picList = null;
            if (!TextUtils.isEmpty(picInfoListStr)) {
                Gson g = new Gson();
                picList = g.fromJson(picInfoListStr, new TypeToken<List<UpPicInfo>>() {
                }.getType());
            }
            return picList;
        }

        /**
         * 图片上传成功后，需要假写到对应的整条评论中，此处返回的即为假写图片内容
         *
         * @return
         */
        public UpPicInfo getSendingPicInfo() {
            UpPicInfo sendingPicInfo = null;
            if (picture != null && picture.size() > 0) {
                sendingPicInfo = picture.get(0);
            }
            return sendingPicInfo;
        }

        @Override
        public String toString() {
            return "UpPicRespData{" +
                    "picture=" + picture +
                    '}';
        }
    }


    public static class UpPicInfo implements Serializable {
        private static final long serialVersionUID = 8121232986972365429L;
        private String url;
        private String oriUrl;
        private int width;
        private int height;
        private int size;
        private String type;

        public UpPicInfo cloneLocalPicInfo(String fileLocator) {
            UpPicInfo result = new UpPicInfo();
            result.setWidth(width);
            result.setHeight(height);
            result.setSize(size);
            result.setUrl(url);
            result.setOriUrl(oriUrl);
//            if ( !TextUtils.isEmpty(fileLocator) ) {
//                result.setUrl(fileLocator);
//            } else {
//                result.setUrl(url);
//            }
            return result;
        }

        /*public void fakeUrls() {
            if (!TextUtils.isEmpty(url) && !url.contains("/100x100")) {
                oriUrl = url + "/0";
                url = url + "/100x100";
            }
        }*/

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public String getOriUrl() {
            return oriUrl;
        }

        public String getHDUrl() {
            return TextUtils.isEmpty(oriUrl) ? url : oriUrl;
        }

        public void setOriUrl(String oriUrl) {
            this.oriUrl = oriUrl;
        }

        public void setType(String mimeType) {
            this.type = mimeType;
        }

        public String getType() {
            return type;
        }

        @Override
        public String toString() {
            return "UpPicInfo{" +
                    "url='" + url + '\'' +
                    ", oriUrl='" + oriUrl + '\'' +
                    ", width=" + width +
                    ", height=" + height +
                    ", size=" + size +
                    ", type='" + type + '\'' +
                    '}';
        }
    }

    public int getCode() {
        return code;
    }


    public UpPicRespData getData() {
        return data;
    }


    public void setData(UpPicRespData data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "UploadPicPojo{" +
                "code=" + code +
                ", data=" + data +
                '}';
    }
}
