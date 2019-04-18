package com.laughfly.rxsociallib.downloader;

import android.text.TextUtils;

import com.laughfly.rxsociallib.SocialModel;
import com.laughfly.rxsociallib.SocialUriUtils;
import com.laughfly.rxsociallib.SocialUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by caowy on 2019/4/17.
 * email:cwy.fly2@gmail.com
 */

public class DefaultImageDownloader extends ImageDownloader {
    private OkHttpClient mOkHttpClient;

    public DefaultImageDownloader() {
        mOkHttpClient = new OkHttpClient();
    }

    @Override
    public File download(String url) throws Exception {
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
            Request request = new Request.Builder().url(url).build();
            is = mOkHttpClient.newCall(request).execute().body().byteStream();
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

        return downloadFile;
    }
}
