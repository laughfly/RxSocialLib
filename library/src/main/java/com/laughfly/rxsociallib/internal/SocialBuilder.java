package com.laughfly.rxsociallib.internal;

import android.content.Context;
import android.text.TextUtils;

import com.laughfly.rxsociallib.PlatformConfig;
import com.laughfly.rxsociallib.SocialCallback;
import com.laughfly.rxsociallib.exception.SocialException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import rx.Emitter;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Cancellable;

public abstract class SocialBuilder<Action extends SocialAction, Result> {
    private final HashMap<String, Object> mData = new HashMap<>();

    private Context mContext;

    private String mPlatform;

    public SocialBuilder(Context context, String platform, PlatformConfig platformConfig) {
        mContext = context;
        mPlatform = platform;
        if(platformConfig != null) {
            setAppId(platformConfig.appId);
            setAppSecret(platformConfig.appSecret);
            setScope(platformConfig.scope);
            setRedirectUrl(platformConfig.redirectUrl);
            setState(platformConfig.state);
        }
    }

    protected void putAll(Map<String, Object> data) {
        mData.putAll(data);
    }

    protected <E> void put(String key, E entity) {
        mData.put(key, entity);
    }

    protected boolean has(String key) {
        Object value = mData.get(key);
        if(value != null) {
            if(value instanceof CharSequence) {
                return !TextUtils.isEmpty((CharSequence) value);
            } else if(value instanceof Collection) {
                return !((Collection) value).isEmpty();
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    protected <E> E get(String key) {
        return (E) mData.get(key);
    }

    protected <E> E get(String key, E defaultVal) {
        Object o = mData.get(key);
        return o != null ? (E) o : defaultVal;
    }

    public Context getContext() {
        return mContext;
    }

    public String getPlatform() {
        return mPlatform;
    }

    protected SocialBuilder setAppId(String appId){
        put("appId", appId);
        return this;
    }

    public String getAppId() {
        return get("appId");
    }

    protected SocialBuilder setAppSecret(String appSecret) {
        put("appSecret", appSecret);
        return this;
    }

    public String getAppSecret() {
        return get("appSecret");
    }

    protected SocialBuilder setScope(String scope) {
        put("scope", scope);
        return this;
    }

    public String getScope() {
        return get("scope");
    }

    protected SocialBuilder setRedirectUrl(String redirectUrl) {
        put("redirectUrl", redirectUrl);
        return this;
    }

    public String getRedirectUrl( ) {
        return get("redirectUrl");
    }

    protected SocialBuilder setState(String state) {
        put("state", state);
        return this;
    }

    public String getState() {
        return get("state");
    }

    public Observable<Result> toObservable() {
        Action action = build();
        return Observable.create(new Action1<Emitter<Result>>() {
            @Override
            public void call(Emitter<Result> emitter) {
                emitter.setCancellation(new Cancellable() {
                    @Override
                    public void cancel() throws Exception {
                        action.cancel();
                    }
                });
                SocialCallback<Result> callback = new SocialCallback<Result>() {
                    @Override
                    public void onError(String platform, SocialException e) {
                        emitter.onError(e);
                    }

                    @Override
                    public void onSuccess(String platform, Result resp) {
                        emitter.onNext(resp);
                    }

                    @Override
                    public void onFinish(String platform) {
                        emitter.onCompleted();
                    }
                };
                action.setCallback(callback);
                action.start();
            }
        }, Emitter.BackpressureMode.LATEST);
    }

    public io.reactivex.Observable<Result> toObservable2() {
        Action action = build();
        return io.reactivex.Observable.create(new ObservableOnSubscribe<Result>() {
            @Override
            public void subscribe(ObservableEmitter<Result> emitter) throws Exception {
                emitter.setCancellable(new io.reactivex.functions.Cancellable() {
                    @Override
                    public void cancel() throws Exception {
                        action.cancel();
                    }
                });
                SocialCallback<Result> callback = new SocialCallback<Result>() {
                    @Override
                    public void onError(String platform, SocialException e) {
                        emitter.onError(e);
                    }

                    @Override
                    public void onSuccess(String platform, Result resp) {
                        emitter.onNext(resp);
                    }

                    @Override
                    public void onFinish(String platform) {
                        emitter.onComplete();
                    }
                };
                action.setCallback(callback);
                action.start();
            }
        });
    }

    public Flowable<Result> toFlowable() {
        Action action = build();
        return Flowable.create(new FlowableOnSubscribe<Result>() {
            @Override
            public void subscribe(FlowableEmitter<Result> emitter) throws Exception {
                emitter.setCancellable(new io.reactivex.functions.Cancellable() {
                    @Override
                    public void cancel() throws Exception {
                        action.cancel();
                    }
                });
                SocialCallback<Result> callback = new SocialCallback<Result>() {
                    @Override
                    public void onError(String platform, SocialException e) {
                        emitter.onError(e);
                    }

                    @Override
                    public void onSuccess(String platform, Result resp) {
                        emitter.onNext(resp);
                    }

                    @Override
                    public void onFinish(String platform) {
                        emitter.onComplete();
                    }
                };
                action.setCallback(callback);
                action.start();
            }
        }, BackpressureStrategy.ERROR);
    }

    public void start(SocialCallback<Result> callback) {
        Action action = build();
        action.setCallback(callback);
        action.start();
    }

    protected abstract Action build();
}
