package com.laughfly.rxsociallib;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.os.Environment;
import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.microedition.khronos.opengles.GL10;

/**
 * 工具类
 * author:caowy
 * date:2018-04-20
 */
public class SocialUtils {

    public static boolean isEmpty(CharSequence charSequence) {
        return charSequence != null && charSequence.length() > 0;
    }

    public static boolean isHttpUri(String uri) {
        return uri != null && uri.matches("^(http|https)://");
    }

    public static boolean isLocalUri(String uri) {
        return uri != null && uri.startsWith("/");
    }

    public static String getAppKey(Context c, String metaName) {
        try {
            ApplicationInfo info = c.getPackageManager().getApplicationInfo(c.getPackageName(), PackageManager
                .GET_META_DATA);
            return info.metaData.get(metaName).toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean downloadFile(String imageUrl, String downloadPath) {
        InputStream inputStream = null;
        BufferedOutputStream outputStream = null;
        try {
            new File(downloadPath).delete();
            outputStream = new BufferedOutputStream(new FileOutputStream(downloadPath), 8192);
            inputStream = new URL(imageUrl).openStream();
            int readLen;
            byte[] buff = new byte[8192];
            while ((readLen = inputStream.read(buff, 0, 8192)) > 0) {
                outputStream.write(buff, 0, readLen);
            }
            outputStream.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeStream(inputStream);
            closeStream(outputStream);
        }
        return false;
    }

    public static void closeStream(Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
            }
        }
    }

    public static byte[] scaleImage(byte[] imageBytes, int maxBytes) {
        if(imageBytes.length <= maxBytes) return imageBytes;
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        return bitmapToBytes(bitmap, maxBytes, true);
    }

    public static byte[] loadImageBytes(String imageUri, int maxBytes) {
        Bitmap bitmap = loadBitmap(imageUri);
        return bitmapToBytes(bitmap, maxBytes, true);
    }

    public static byte[] loadLocalImageBytes(String imagePath, int maxBytes) {
        Bitmap bitmap = safeLoadLocalBitmap(imagePath);
        return bitmapToBytes(bitmap, maxBytes, true);
    }

    public static Bitmap loadBitmap(String imageUri) {
        if(TextUtils.isEmpty(imageUri)) return null;
        if (imageUri.startsWith("http")) {
            File downloadDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            String downloadPath = new File(downloadDirectory, "social_share_image.jpg").getPath();
            boolean success = downloadFile(imageUri, downloadPath);
            if(!success) return null;
            imageUri = downloadPath;
        }
        return safeLoadLocalBitmap(imageUri);
    }

    public static Bitmap scaleBitmap(Bitmap bitmap, int maxBytes) {
        byte[] bytes = bitmapToBytes(bitmap, maxBytes, true);
        if (bytes != null) {
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        }
        return null;
    }

    private static Bitmap safeLoadLocalBitmap(String imagePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);
        int maxSize = GLES20.GL_MAX_TEXTURE_SIZE;
        if(options.outWidth > maxSize || options.outHeight > maxSize) {
            float widthScale = options.outWidth / maxSize;
            float heightScale = options.outHeight / maxSize;
            options.inSampleSize = (int) (Math.ceil(Math.max(widthScale, heightScale) / 2f) * 2);
        }
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(imagePath, options);
    }

    public static byte[] bitmapToBytes(Bitmap bitmap, int maxBytes) {
        return bitmapToBytes(bitmap, maxBytes, true);
    }

    public static byte[] bitmapToBytes(Bitmap bitmap, int maxBytes, boolean recycleBitmap) {
        int length;
        int quality = 100;
        byte[] bytes;
        try{
            do {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
                bytes = baos.toByteArray();
                try {
                    baos.flush();
                    baos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                length = bytes.length;

                quality -= 20;

                if(length > maxBytes && quality <= 40) {//质量小于30%，还是过大的时候，降低图片尺寸
                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / 2, bitmap.getHeight() / 2, true);
                    bitmap.recycle();
                    bitmap = scaledBitmap;
                    quality = 80;
                }
            } while (length > maxBytes);
        } finally {
            if (recycleBitmap) {
                bitmap.recycle();
            }
        }
        return bytes;
    }

    public static Bitmap loadBitmapFromFile(String filePath, int maxDataSize) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        int outWidth = options.outWidth;
        int outHeight = options.outHeight;
        options.inJustDecodeBounds = false;
        if (outWidth > GL10.GL_MAX_TEXTURE_SIZE) {
            options.inSampleSize = (int) Math.pow(2, outWidth / GL10.GL_MAX_TEXTURE_SIZE);
        }
        if (outHeight > GL10.GL_MAX_TEXTURE_SIZE) {
            options.inSampleSize = (int) Math.pow(2, outHeight / GL10.GL_MAX_TEXTURE_SIZE);
        }
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
        outWidth = bitmap.getWidth();
        outHeight = bitmap.getHeight();
        Bitmap.Config config = bitmap.getConfig();
        int rowBytes = 4;
        switch (config) {
            case ALPHA_8:
                rowBytes = 1;
                break;
            case ARGB_4444:
            case RGB_565:
                rowBytes = 2;
                break;
            case ARGB_8888:
                rowBytes = 4;
                break;
            case HARDWARE:
                rowBytes = 2;
                break;
            case RGBA_F16:
                rowBytes = 8;
                break;
        }
        int maxArea = maxDataSize / rowBytes;
        int outArea = outWidth * outHeight;
        if (outArea > maxArea) {
            float scale = (float) Math.sqrt((double) outArea / maxArea) * 1f;
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() / scale),
                (int) (bitmap.getHeight() / scale), false);
            bitmap.recycle();
            return scaledBitmap;
        }
        return bitmap;
    }

    public static Bitmap downloadScaledImage(String imageUrl, int thumbSize) {
        InputStream inputStream = null;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            inputStream = new URL(imageUrl).openStream();
            if (inputStream.markSupported()) {
                inputStream.mark(1024);
            }
            BitmapFactory.decodeStream(inputStream, null, options);
            int sampleSize = Math.min(options.outWidth / thumbSize, options.outHeight / thumbSize);
            options.inSampleSize = sampleSize;
            options.inJustDecodeBounds = false;
            if (inputStream.markSupported()) {
                inputStream.reset();
            } else {
                inputStream.close();
                inputStream = new URL(imageUrl).openStream();
            }
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeStream(inputStream);
        }
        return null;
    }

    public static String quickHttpGet(String url) {
        BufferedInputStream bis = null;
        try {
            HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();
            urlConnection.setRequestMethod("GET");
            InputStream inputStream = urlConnection.getInputStream();

            bis = new BufferedInputStream(inputStream, 8192);
            StringBuffer sb = new StringBuffer(bis.available());
            byte[] buffer = new byte[8192];
            int read;
            while ((read = bis.read(buffer)) != -1) {
                sb.append(new String(buffer, 0, read));
            }
            urlConnection.disconnect();
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeStream(bis);
        }
        return null;
    }

    public static String readTextFromStream(InputStream is) {
        if (is != null) {
            BufferedInputStream bis = null;
            try {
                bis = new BufferedInputStream(is, 8192);
                StringBuffer sb = new StringBuffer(bis.available());
                byte[] buffer = new byte[8192];
                int read;
                while ((read = bis.read(buffer)) != -1) {
                    sb.append(new String(buffer, 0, read));
                }
                return sb.toString();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                closeStream(bis);
            }

        }
        return null;
    }

    /**
     * QQ客户端是否已安装
     * author:caowy
     * date:2018-05-11
     *
     * @param context
     * @return
     */
    public static boolean isQQInstalled(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            packageManager.getPackageInfo("com.tencent.tim", 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }
        try {
            packageManager.getPackageInfo("com.tencent.mobileqq", 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
