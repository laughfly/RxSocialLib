package com.laughfly.rxsociallib.delegate;

import android.content.Intent;

/**
 * Delegate的结果回调
 * author:caowy
 * date:2018-05-12
 *
 * @param <T>
 */
public interface ResultHandler<T extends SocialActivity> {
    /**
     * 第三方结果回调
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    void handleResult(int requestCode, int resultCode, Intent data);

    /**
     * 没有返回结果
     */
    void handleNoResult();

    /**
     * Delegate创建完成
     *
     * @param delegate
     */
    void onDelegateCreate(T delegate);

    /**
     * Delegate销毁
     *
     * @param delegate
     */
    void onDelegateDestroy(T delegate);
}
