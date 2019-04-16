package com.loading.common.utils;

import android.content.res.AssetManager;
import android.media.MediaMetadataRetriever;
import android.text.TextUtils;
import android.util.Base64;

import com.loading.common.component.CApplication;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;

@SuppressWarnings({"UnusedReturnValue", "WeakerAccess", "unused"})
public class FileHandler {
    private static final String TAG = "FileHandler";

    /**
     * 从文件中直接读一个对象
     *
     * @param srcStream file input stream
     */
    public synchronized static Object readSerObjectFromInStream(InputStream srcStream) {
        Object b = null;
        ObjectInputStream in = null;
        try {
            in = new ObjectInputStream(srcStream);
            b = in.readObject();
        } catch (Exception e) {
            Loger.e(TAG, "从文件中直接读一个对象错误: " + e.toString());
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    Loger.e(TAG, "从文件中直接读一个对象，关闭io错误: " + e.toString());
                }
            }
        }
        return b;
    }

    //NOTE: the user should be responsible for close the outputstream
    public synchronized static boolean writeSerObjectToStream(OutputStream desStream, Object serObj) {
        boolean isSuccess = false;
        if (desStream != null && serObj != null) {
            try {
                ObjectOutputStream objOutputStream = new ObjectOutputStream(desStream);
                objOutputStream.writeObject(serObj);
                objOutputStream.flush();
                isSuccess = true;
            } catch (IOException e) {
                Loger.e(TAG, "writeSerObjectToStream exception: " + e);
            }
        }
        return isSuccess;
    }

    public static boolean isFileExistInAssets(String folder, String fileName) {
        boolean isExist = false;
        AssetManager am = CApplication.getAppContext().getAssets();
        InputStream is = null;
        try {
            is = am.open(folder + File.separator + fileName);
            if (is != null) {
                isExist = true;
            }

        } catch (IOException e) {
            Loger.e(TAG, "isFileExistInAssets: " + e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ignore) {
                }
            }
        }
        return isExist;
    }

    /**
     * 读取asset文件
     *
     * @param fileName to read file
     * @return the seriziabl obj stored in file
     */
    public static synchronized Object getObjectFileFromAssets(String fileName) {
        if (fileName == null || fileName.equals("")) {
            return null;
        }
        InputStream stream = null;
        Object object = null;
        try {
            stream = CApplication.getAppContext().getAssets().open(fileName);
            object = FileHandler.readSerObjectFromInStream(stream);
        } catch (IOException e) {
            Loger.e(TAG, "exception 1: " + e.toString());
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    Loger.e(TAG, "从文件中直接读一个对象，关闭io错误" + e.toString());
                }
            }
        }
        return object;
    }


    /**
     * 删除指定文件
     *
     * @param sPath the file or path to del
     * @return true for success, else false
     */
    public static synchronized boolean removeFileAtPath(String sPath) {
        boolean flag = false;
        try {
            File file = new File(sPath);
            // 判断目录或文件是否存在
            if (file.exists()) { // 不存在返回 false
                // 判断是否为文件
                if (file.isFile()) { // 为文件时调用删除文件方法
                    flag = deleteFile(sPath);
                } else { // 为目录时调用删除目录方法
                    flag = deleteDirectory(sPath);
                }
            }
        } catch (Exception ignore) {
            // Log.e("removeFileAtPath", e.getMessage());
        }
        return flag;
    }

    /**
     * 判断文件/文件夹是否存在
     *
     * @param path the file path
     * @return true for exist, else false
     */
    public static boolean isDirFileExist(String path) {
        if (path == null || path.length() == 0) {
            return false;
        }
        File dirFile = new File(path);
        return dirFile.exists();
    }


    /**
     * 取得文件Size
     *
     * @param path the file path
     * @return the sise of the file
     */
    public static long getFileSize(String path) {
        long size = 0;
        if (!TextUtils.isEmpty(path)) {
            File file = new File(path);
            if (file.exists()) {
                size = file.length();
            }
        }
        return size;
    }

    /**
     * 获取时间间隔，video太小的时候是空
     *
     * @param path the video file path
     * @return the duration of this video
     */
    public static long getFileDurationTime(String path) {
        long time = 0;
        if (!TextUtils.isEmpty(path)) {
            MediaMetadataRetriever mediaMetadataRetriever = null;
            try {
                mediaMetadataRetriever = new MediaMetadataRetriever();
                mediaMetadataRetriever.setDataSource(path);
                String timeStr = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                mediaMetadataRetriever.release();
                time = Long.parseLong(timeStr);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (mediaMetadataRetriever != null) {
                        mediaMetadataRetriever.release();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return time;
    }

    /**
     * 删除指定文件夹下面的扩展名为ext的所有文件
     *
     * @param path the path to check
     * @param ext  files extension
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void removeFilesAtDirPath(String path, String ext) {
        if (!TextUtils.isEmpty(path)) {
            File file = new File(path);
            if (file.exists()) {
                String[] list = file.list();
                if (list != null) {
                    int count = list.length;
                    for (String aList : list) {
                        if (aList.endsWith(ext)) {
                            File delFile = new File(aList);
                            delFile.delete();
                        }
                    }
                }
            }
        }
    }


    /**
     * 取得指定文件的modification日期
     *
     * @param path the file path
     * @return the modified time
     */
    public static long getFileModificationDate(String path) {
        long modTime = 0;
        if (!TextUtils.isEmpty(path)) {
            File file = new File(path);
            if (file.exists()) {
                modTime = file.lastModified();
            }
        }
        return modTime;
    }

    /**
     * 重命名
     *
     * @param srcFilePath  src file path
     * @param destFilePath dest file path
     * @return true for success, else false
     */
    public static boolean rename(String srcFilePath, String destFilePath) {
        boolean isSuccess = false;
        if (!TextUtils.isEmpty(srcFilePath) && !TextUtils.isEmpty(destFilePath)) {
            File srcfile = new File(srcFilePath);
            if (srcfile.exists()) {
                File desfile = new File(destFilePath);
                isSuccess = srcfile.renameTo(desfile);
            }
        }
        return isSuccess;
    }

    /**
     * 将data完全写入文件，如果文件已存在，将会追加写入
     *
     * @param data data to write
     * @param path the file path write to
     * @return true for success
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static synchronized boolean writeDataToPath(byte[] data, String path) {
        FileOutputStream os = null;
        try {
            File file = new File(path);
            if (!file.exists()) {
                file.createNewFile();
            }
            os = new FileOutputStream(path);
            os.write(data, 0, data.length);
            os.flush();
        } catch (Exception e) {
            return false;
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    Loger.e(TAG, "从文件中直接读一个对象，关闭io错误: " + e.toString());
                }
            }
        }
        return true;
    }

    /**
     * 将data完全写入文件，如果同名文件已存在，则先删除文件
     *
     * @param data
     * @param path
     * @return
     */
    public static synchronized void writeByteToFilePath(byte[] data, String path, boolean append) {
        FileOutputStream os = null;
        try {
            File file = new File(path);
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            os = new FileOutputStream(path, append);
            os.write(data, 0, data.length);
            os.flush();
        } catch (Exception e) {
            Loger.e(TAG, e.toString());
        } finally {
            if (os != null) {
                try {
                    os.close();
                    os = null;
                } catch (IOException e) {
//					Loger.e("从文件中直接读一个对象，关闭io错误", e.toString());
                }
            }
        }
    }

    /**
     * 将对象写入文件，如果文件已存在，将会追加写入
     *
     * @param obj
     * @param path
     * @return
     */
    public static synchronized boolean writeObjectToPath(Object obj, String path) {
        FileOutputStream os = null;
        ObjectOutputStream oos = null;
        try {
            File file = new File(path);
            if (!file.exists()) {
                file.createNewFile();
            }
            os = new FileOutputStream(path);
            oos = new ObjectOutputStream(os);
            oos.writeObject(obj);
            oos.flush();
        } catch (Exception e) {
            Loger.e(TAG, "写文件错误" + e);
            return false;
        } finally {
            if (os != null) {
                try {
                    os.close();
                    os = null;
                } catch (IOException e) {
                    Loger.e(TAG, "从文件中直接读一个对象，关闭io错误: " + e.toString());
                }
            }
            if (oos != null) {
                try {
                    oos.close();
                    oos = null;
                } catch (IOException e) {
                    Loger.e(TAG, "从文件中直接读一个对象，关闭io错误" + e.toString());
                }
            }
        }
        return true;
    }

    /**
     * 将对象写入文件，如果同名文件已存在，则先删除文件
     *
     * @param obj
     * @param path
     * @return
     */
    public static synchronized boolean writeObjectToFilePath(Object obj, String path) {
        FileOutputStream os = null;
        ObjectOutputStream oos = null;
        try {
            File file = new File(path);
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            os = new FileOutputStream(path);
            oos = new ObjectOutputStream(os);
            oos.writeObject(obj);
            oos.flush();
        } catch (Exception e) {
            // Log.e("writeObjectToPath", e.getMessage());
            return false;
        } finally {
            if (os != null) {
                try {
                    os.close();
                    os = null;
                } catch (IOException e) {
                    Loger.e(TAG, "从文件中直接读一个对象，关闭io错误: " + e.toString());
                }
            }
            if (oos != null) {
                try {
                    oos.close();
                    oos = null;
                } catch (IOException e) {
                    Loger.e(TAG, "从文件中直接读一个对象，关闭io错误: " + e.toString());
                }
            }
        }
        return true;
    }

    /**
     * 将文件中的内容完全读出
     *
     * @param path
     * @return
     */
    public static synchronized byte[] readDataFromPath(String path) {
        File file = new File(path);
        int filesize = (int) file.length();

        if (filesize == 0) {
            return null;
        }

        byte[] buffer = new byte[filesize + 1];
        FileInputStream is = null;
        try {
            is = new FileInputStream(path); // 读入原文件
            is.read(buffer, 0, filesize);
            is.close();
        } catch (Exception e) {
            return null;
        } finally {
            if (is != null) {
                try {
                    is.close();
                    is = null;
                } catch (IOException e) {
//					Loger.e("从文件中直接读一个对象，关闭io错误", e.toString());
                }
            }
        }

        return buffer;
    }

    /**
     * 将文件内容读取为Object
     *
     * @param path
     * @return
     */
    public static synchronized Object readObjectFromPath(String path) {
        Object obj = null;
        FileInputStream is = null;
        ObjectInputStream ois = null;
        if (!TextUtils.isEmpty(path)) {
            try {
                File file = new File(path);
                if (file.exists()) {
                    is = new FileInputStream(file); // 读入原文件
                    ois = new ObjectInputStream(is);
                    obj = ois.readObject();
                }
            } catch (Exception e) {
                Loger.e(TAG, "从文件中直接读一个对象错误: " + e);
            } finally {
                if (is != null) {
                    try {
                        is.close();
                        is = null;
                    } catch (IOException e) {
                        Loger.e(TAG, "从文件中直接读一个对象，关闭io错误: " + e.toString());
                    }
                }
                if (ois != null) {
                    try {
                        ois.close();
                        ois = null;
                    } catch (IOException e) {
                        Loger.e(TAG, "从文件中直接读一个对象，关闭io错误: " + e.toString());
                    }
                }
            }
        }
        return obj;
    }

    /**
     * 同步化读/写文件，如果传入object为空，则为读，如果非空，则为写
     *
     * @param path
     * @param writeObj
     * @return
     */
    public static synchronized Object synchronized_RW_Object(String path, Object writeObj) {
        Object obj = null;

        if (writeObj == null) {
            // read
            obj = FileHandler.readObjectFromPath(path);
        } else {
            // write
            FileHandler.writeObjectToFilePath(writeObj, path);
        }

        return obj;
    }

    public static synchronized boolean deleteFile(String sPath) {
        boolean flag = false;
        if (!TextUtils.isEmpty(sPath)) {
            File file = new File(sPath);
            // 路径为文件且不为空则进行删除
            if (file.isFile() && file.exists()) {
                file.delete();
                flag = true;
            }
        }
        return flag;
    }

    public static synchronized boolean deleteDirectory(String sPath) {
        boolean isSuccess = false;
        if (!TextUtils.isEmpty(sPath)) {
            // 如果sPath不以文件分隔符结尾，自动添加文件分隔符
            if (!sPath.endsWith(File.separator)) {
                sPath = sPath + File.separator;
            }
            File dirFile = new File(sPath);
            if (dirFile.exists() && dirFile.isDirectory()) {
                boolean flag = true;
                // 删除文件夹下的所有文件(包括子目录)
                File[] files = dirFile.listFiles();
                if (files != null) {
                    for (int i = 0; i < files.length; i++) {
                        if (files[i].isFile()) {
                            // 删除子文件
                            flag = deleteFile(files[i].getAbsolutePath());
                            if (!flag) {
                                break;
                            }

                        } else {
                            // 删除子目录
                            flag = deleteDirectory(files[i].getAbsolutePath());
                            if (!flag) {
                                break;
                            }
                        }
                    }
                    if (flag) {
                        // 删除当前目录
                        if (dirFile.delete()) {
                            isSuccess = true;
                        }
                    }
                }
            }
        }
        return isSuccess;
    }

    /**
     * 序列化一个对象
     *
     * @param object
     * @return
     */
    public static synchronized String getSeriString(Object object) {
        String productBase64 = null;
        if (object != null) {
            ObjectOutputStream oos = null;
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                oos = new ObjectOutputStream(baos);
                oos.writeObject(object);
                productBase64 = new String(Base64.encode(baos.toByteArray(), Base64.DEFAULT));
            } catch (IOException e) {
                Loger.e("", e.toString());
            } catch (OutOfMemoryError e) {
                Loger.e(TAG, "内存溢出" + e);
            } finally {
                if (oos != null) {
                    try {
                        oos.close();
                    } catch (IOException e) {
                        Loger.e(TAG, "IO异常" + e);
                    }
                }
            }
        }
        return productBase64;
    }

    /**
     * 反序列化一个对象
     *
     * @param data
     * @return
     * @throws Exception
     */
    public static synchronized Object getObjectFromBytes(String data) throws Exception {
        Object object = null;
        if (!TextUtils.isEmpty(data)) {
            byte[] objBytes = Base64.decode(data, Base64.DEFAULT);
            if (objBytes != null && objBytes.length > 0) {
                ByteArrayInputStream bi = null;
                ObjectInputStream oi = null;

                try {
                    bi = new ByteArrayInputStream(objBytes);
                    oi = new ObjectInputStream(bi);
                    object = oi.readObject();
                } finally {
                    if (oi != null) {
                        oi.close();
                    }
                    if (bi != null) {
                        bi.close();
                    }
                }
            }
        }
        return object;
    }


    public static synchronized String readString(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            StringBuilder text = new StringBuilder();
            BufferedReader br = null;
            try {
                br = new BufferedReader(new FileReader(file));
                String line;
                while ((line = br.readLine()) != null) {
                    text.append(line);
                    text.append('\n');
                }
            } catch (IOException e) {
                Loger.e(TAG, "" + e.toString());
            } finally {
                try {
                    if (br != null) {
                        br.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return text.toString();
        } else {
            return null;
        }
    }

    /**
     * 把str写入文件
     *
     * @param filePath 文件路径
     * @param str      json字符串
     * @return 写入成功与否
     * @author
     * @since 2011-10-24
     */
    public static synchronized boolean writeString(String filePath, String str, boolean isAppend) {
        FileOutputStream out = null;
        try {
            File file = makeDIRAndCreateFile(filePath);
            out = new FileOutputStream(file, isAppend);
            out.write(str.getBytes());
            out.flush();
        } catch (Exception e) {
            Loger.e(TAG, "" + e.toString());
            return false;
        } finally {
            if (out != null) {
                try {
                    out.close();
                    out = null;
                } catch (Exception e) {
                    Loger.e(TAG, "" + e.toString());
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 创建目录和文件， 如果目录或文件不存在，则创建出来
     *
     * @param filePath 文件路径
     * @return 创建后的文件
     * @throws IOException the exception
     * @since 2011-10-24
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static synchronized File makeDIRAndCreateFile(String filePath) throws IOException {
        File file = new File(filePath);
        String parent = file.getParent();
        File parentFile = parent != null ? new File(parent) : null;
        if (parentFile != null && !parentFile.exists()) {
            if (parentFile.mkdirs()) {
                file.createNewFile();
            } else {
                throw new IOException("创建目录失败！");
            }
        } else {
            if (!file.exists()) {
                file.createNewFile();
            }
        }
        return file;
    }

    /**
     * @param filePath, the path to the file or dir
     * @return the size of file or total size of dir
     */
    public static synchronized long getFolderSize(String filePath) {
        long resultSize = 0L;
        if (!TextUtils.isEmpty(filePath)) {
            File tFile = new File(filePath);
            resultSize = getFileTotalSize(tFile);
        }
        return resultSize;
    }

    public static synchronized long getFileTotalSize(File file) {
        long length = 0L;
        if (file != null) {
            if (file.isDirectory()) {
                File[] tFileArray = file.listFiles();
                if (tFileArray == null) {
                    return length;
                }
                for (File tFileItem : tFileArray) {
                    length += getFileTotalSize(tFileItem);
                }
            } else {
                length += file.length();
            }
        }
        return length;
    }

    public static byte[] readFileByRandomAccess(String fileName, long seekIndex, long length) {
        RandomAccessFile randomFile = null;
        byte[] bytes = null;
        try {
            // 打开一个随机访问文件流，按只读方式
            randomFile = new RandomAccessFile(fileName, "rw");
            if (randomFile.length() > seekIndex) {
                randomFile.seek(seekIndex);
                bytes = new byte[(int) length];
                int byteRead = randomFile.read(bytes);
                if (byteRead != length) {
                    bytes = null;  // there is no more data
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            Loger.e(TAG, e.toString());
            bytes = null;
        } finally {
            if (randomFile != null) {
                try {
                    randomFile.close();
                } catch (IOException e1) {
                    Loger.e(TAG, e1.toString());
                    bytes = null;
                }
            }
        }

        return bytes;
    }

    public static void setFilelengthByRandomAccess(String fileName, long index) {
        RandomAccessFile randomFile = null;
        try {
            randomFile = new RandomAccessFile(fileName, "rw");
            randomFile.setLength(index);
        } catch (IOException e) {
            e.printStackTrace();
            Loger.e(TAG, e.toString());
        } finally {
            if (randomFile != null) {
                try {
                    randomFile.close();
                } catch (IOException e1) {
                    Loger.e(TAG, e1.toString());
                }
            }
        }
    }

    public static byte[] readFile(String path) {
        BufferedInputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(path));
        } catch (FileNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (Exception e) {
            Loger.e(TAG, "error, exceptin: " + e);
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
        byte[] temp = new byte[1024];
        int size;
        try {
            if (in != null) {
                while ((size = in.read(temp)) != -1) {
                    out.write(temp, 0, size);
                }
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        try {
            if (in != null) {
                in.close();
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return out.toByteArray();
    }

    public static String[] byte2hex(byte[] b) {
        try {
            if (b != null && b.length > 0) {
                String[] hexStrArray = new String[b.length];
                for (int n = 0; n < b.length; n++) {
                    String stmp = Integer.toHexString(b[n] & 0XFF).toLowerCase();

                    if (stmp.length() == 1) {
                        stmp = "0" + stmp;
                    }

                    hexStrArray[n] = stmp;
                }

                return hexStrArray;
            }
        } catch (Exception e) {
            Loger.e(TAG, e.toString());
        }
        return null;
    }

    public static boolean saveStreamToFile(InputStream inputStream, String fFilePath) {
        boolean isSuccess = false;
        if (inputStream != null && !TextUtils.isEmpty(fFilePath)) {
            BufferedInputStream tBufferInputStream = null;
            BufferedOutputStream ostream = null;
            try {
                File file = new File(fFilePath);
                if (!file.exists()) {
                    //noinspection ResultOfMethodCallIgnored
                    file.createNewFile();
                }
                ostream = new BufferedOutputStream(new FileOutputStream(file));
                tBufferInputStream = new BufferedInputStream(inputStream);
                int n;
                byte[] aByte = new byte[1024];
                while ((n = tBufferInputStream.read(aByte, 0, 1024)) >= 0) {
                    ostream.write(aByte, 0, n);
                }
                ostream.flush();
                isSuccess = true;
            } catch (Exception e) {
                Loger.e(TAG, "save img to path exception: " + e);
            } finally {
                if (tBufferInputStream != null) {
                    try {
                        tBufferInputStream.close();
                    } catch (Exception e) {
                        Loger.e(TAG, "exception: " + e);
                    }
                }
                if (ostream != null) {
                    try {
                        ostream.close();
                    } catch (Exception e) {
                        Loger.e(TAG, "exception: " + e);
                    }
                }
            }
        }
        return isSuccess;
    }

    public static String convertStreamToString(InputStream inputStream) {
        String resultStr = null;
        if (inputStream != null) {
            ByteArrayOutputStream resultStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            try {
                while ((length = inputStream.read(buffer)) != -1) {
                    resultStream.write(buffer, 0, length);
                    resultStr = resultStream.toString("UTF-8");
                }
            } catch (Exception e) {
                Loger.e(TAG, "-->convertStreamToString()", e);
            } finally {
                try {
                    resultStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return resultStr;
    }
}