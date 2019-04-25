package com.laughfly.rxsociallib.downloader;

import java.io.File;

/**
 * Created by caowy on 2019/4/17.
 * email:cwy.fly2@gmail.com
 */

public abstract class FileDownloader {
    public abstract File download(String url) throws Exception;
}
