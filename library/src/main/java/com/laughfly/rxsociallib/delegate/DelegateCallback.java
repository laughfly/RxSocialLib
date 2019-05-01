package com.laughfly.rxsociallib.delegate;

/**
 * Delegate的结果回调
 * author:caowy
 * date:2018-05-12
 *
 * @param <T>
 */
public interface DelegateCallback<T extends SocialDelegateActivity> {
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
