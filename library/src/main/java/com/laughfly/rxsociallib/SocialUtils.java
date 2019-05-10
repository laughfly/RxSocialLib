package com.laughfly.rxsociallib;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.os.Bundle;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;

import javax.microedition.khronos.opengles.GL10;

/**
 * 工具类
 * author:caowy
 * date:2018-04-20
 */
public class SocialUtils {

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

    public static Bitmap scaleImage(Bitmap bitmap, int maxBytes) {
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

    public static byte[] loadImageBytes(String imageFilePath, int dataSizeLimit) {
        Bitmap bitmap = safeLoadLocalBitmap(imageFilePath);
        return bitmapToBytes(bitmap, dataSizeLimit);
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
                bitmap.compress(bitmap.hasAlpha() ? Bitmap.CompressFormat.PNG : Bitmap.CompressFormat.JPEG, quality, baos);
                bytes = baos.toByteArray();
                try {
                    baos.flush();
                    baos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                length = bytes.length;

                quality -= 10;

                if(length > maxBytes && quality <= 70) {//质量小于70%，还是过大的时候，降低图片尺寸
                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / 2, bitmap.getHeight() / 2, true);
                    bitmap.recycle();
                    bitmap = scaledBitmap;
                    quality = 100;
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

    public static boolean saveBitmapToFile(Bitmap bitmap, File file, int sizeLimit){
        byte[] bytes = bitmapToBytes(bitmap, sizeLimit, false);
        if(bytes != null && bytes.length > 0) {
            try {
                saveBytesToFile(bytes, file);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static void saveBytesToFile(byte[] bytes, File file) throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        FileChannel channel = new FileOutputStream(file).getChannel();
        channel.write(byteBuffer);
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

    public static boolean checkAppInstalled(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        try {
            packageManager.getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException ignore) {
        }
        return false;
    }

    public static String bundle2String(Bundle bundle) {
        if (bundle == null) {
            return null;
        }
        String string = "Bundle{";
        for (String key : bundle.keySet()) {
            string += " " + key + " => " + bundle.get(key) + ";";
        }
        string += " }Bundle";
        return string;
    }

    public static final String md5(final String toEncrypt) {
        try {
            final MessageDigest digest = MessageDigest.getInstance("md5");
            digest.update(toEncrypt.getBytes());
            final byte[] bytes = digest.digest();
            final StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(String.format("%02X", bytes[i]));
            }
            return sb.toString().toLowerCase();
        } catch (Exception exc) {
            return ""; // Impossibru!
        }
    }

    public static String decrypt(String text, String iv, String key) {

        return null;
    }
}
