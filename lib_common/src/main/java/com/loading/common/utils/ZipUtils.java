package com.loading.common.utils;

import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipUtils {
    private static final String TAG = "ZipUtils";
    /*
    BUFFER is used for limiting the buffer memory size
    while reading and writing data it to the zip stream
    */
    private static final int BUFFER = 1024;

    /**
     * @param files       holds all the file paths that you want to zip
     * @param zipFileName the name of the zip file.
     */
    public static boolean zip(List<String> files, String zipFileName) {
        Loger.d(TAG, "--> zip(String[] files=" + files + ", String zipFileName=" + zipFileName + ")");
        boolean isSuccess = false;
        ZipOutputStream out = null;
        BufferedInputStream origin = null;
        if (files != null && files.size() > 0 && !TextUtils.isEmpty(zipFileName)) {
            try {
                out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFileName)));
                byte data[] = new byte[BUFFER];
                for (String file : files) {
                    Loger.d(TAG, "Adding: " + file);
                    ZipEntry entry = new ZipEntry(file.substring(file.lastIndexOf("/") + 1));
                    out.putNextEntry(entry);
                    origin = new BufferedInputStream(new FileInputStream(file));
                    int count;
                    while ((count = origin.read(data, 0, BUFFER)) != -1) {
                        out.write(data, 0, count);
                    }
                    origin.close();
                }
                isSuccess = true;
            } catch (Exception e) {
                Loger.e(TAG, "zip exception: " + e);
            } finally {
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (origin != null) {
                    try {
                        origin.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return isSuccess;
    }

    public static boolean unZip(String zipFileName, String destPath) {
        boolean result;
        if (!destPath.endsWith("/")) {
            destPath = destPath + "/";
        }
        FileOutputStream fileOut = null;
        ZipInputStream zipIn = null;
        ZipEntry zipEntry;
        File file;
        int readedBytes;
        byte buf[] = new byte[4096];
        try {
            zipIn = new ZipInputStream(new BufferedInputStream(new FileInputStream(zipFileName)));
            while ((zipEntry = zipIn.getNextEntry()) != null) {
                String zipName = zipEntry.getName();
                if (zipName != null && zipName.contains("../")) {
                    continue;
                }
                file = new File(destPath + zipName);
                if (zipEntry.isDirectory()) {
                    //noinspection ResultOfMethodCallIgnored
                    file.mkdirs();
                } else {
                    File parent = file.getParentFile();
                    if (parent != null && !parent.exists()) {
                        //noinspection ResultOfMethodCallIgnored
                        parent.mkdirs();
                    }
                    fileOut = new FileOutputStream(file);
                    while ((readedBytes = zipIn.read(buf)) > 0) {
                        fileOut.write(buf, 0, readedBytes);
                    }
                }
                if (fileOut != null) {
                    fileOut.close();
                    fileOut = null;
                }
            }
            result = true;
        } catch (IOException ioe) {
            Loger.e(TAG, "exception: " + ioe);
            result = false;
        } finally {
            if (fileOut != null) {
                try {
                    fileOut.close();
                } catch (IOException e) {
                    Loger.e(TAG, "" + e);
                }
            }
            if (zipIn != null) {
                try {
                    zipIn.closeEntry();
                    zipIn.close();
                } catch (IOException e) {
                    Loger.e(TAG, "" + e);
                }
            }
        }
        return result;
    }

    /*public void unzip(String zipFile, String targetLocation) {
        //create target location folder if not exist
        dirChecker(targetLocation);
        try {
            FileInputStream fin = new FileInputStream(zipFile);
            ZipInputStream zin = new ZipInputStream(fin);
            ZipEntry ze = null;
            while ((ze = zin.getNextEntry()) != null) {

                //create dir if required while unzipping
                if (ze.isDirectory()) {
                    dirChecker(ze.getName());
                } else {
                    FileOutputStream fout = new FileOutputStream(targetLocation + ze.getName());
                    for (int c = zin.read(); c != -1; c = zin.read()) {
                        fout.write(c);
                    }

                    zin.closeEntry();
                    fout.close();
                }

            }
            zin.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }*/
}