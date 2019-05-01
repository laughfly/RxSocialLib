package com.laughfly.rxsociallib.downloader;

import android.text.TextUtils;

import com.laughfly.rxsociallib.SocialLogger;
import com.laughfly.rxsociallib.SocialModel;
import com.laughfly.rxsociallib.SocialUriUtils;
import com.laughfly.rxsociallib.SocialUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;


/**
 * Created by caowy on 2019/4/17.
 * email:cwy.fly2@gmail.com
 */

public class DefaultFileDownloader extends FileDownloader {

    public DefaultFileDownloader() {
    }

    @Override
    public File download(String url) throws Exception {
        SocialLogger.d("Downloader", "url:" + url);
        long time = System.currentTimeMillis();
        File downloadDirectory = SocialModel.getDownloadDirectory();

        String fileName = SocialUriUtils.getFileName(url);
        if(TextUtils.isEmpty(fileName)) {
            fileName = String.valueOf(url.hashCode());
        }

        File downloadFile = new File(downloadDirectory, fileName);
        if(downloadFile.exists()) {
            downloadFile.delete();
        }
        InputStream is = null;
        BufferedOutputStream bos = null;
        try {
            URLConnection urlConnection = new URL(url).openConnection();
            is = urlConnection.getInputStream();
            bos = new BufferedOutputStream(new FileOutputStream(downloadFile), 8192);
            int readLen;
            int bufferSize = 8192;
            byte[] buff = new byte[bufferSize];
            while ((readLen = is.read(buff, 0, bufferSize)) > 0) {
                bos.write(buff, 0, readLen);
            }
            bos.flush();
        } finally {
            SocialUtils.closeStream(is);
            SocialUtils.closeStream(bos);
        }

        SocialLogger.d("DefaultFileDownloader", "TimeCost=" + (System.currentTimeMillis() - time));
        return downloadFile;
    }
}
